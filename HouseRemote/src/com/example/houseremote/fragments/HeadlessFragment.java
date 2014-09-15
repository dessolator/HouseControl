package com.example.houseremote.fragments;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.Toast;

import com.example.houseremote.R;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseQueryManager;
import com.example.houseremote.database.adapters.GridAdapter;
import com.example.houseremote.database.adapters.ListAdapter;
import com.example.houseremote.database.interfaces.ControllersAdapterProvider;
import com.example.houseremote.database.interfaces.HousesAdapterProvider;
import com.example.houseremote.database.interfaces.ReplyListener;
import com.example.houseremote.database.interfaces.RoomsAdapterProvider;
import com.example.houseremote.network.NetworkSet;
import com.example.houseremote.network.dataclasses.InitialStateQueryPacket;
import com.example.houseremote.network.dataclasses.PinStatus;
import com.example.houseremote.network.dataclasses.PinStatusSet;
import com.example.houseremote.network.interfaces.NetworkCommandListener;
import com.example.houseremote.network.interfaces.Sendable;
import com.example.houseremote.network.interfaces.SwitchStateListener;
import com.example.houseremote.network.interfaces.UILockupListener;

public class HeadlessFragment extends Fragment implements ReplyListener, ControllersAdapterProvider,
		RoomsAdapterProvider, HousesAdapterProvider, NetworkCommandListener, SwitchStateListener,
		UILockupListener {

	/*
	 * UI adapters for other fragments
	 */
	private GridAdapter controllerAdapter;
	private ListAdapter houseAdapter;
	private ListAdapter roomAdapter;

	/*
	 * Database Manager
	 */
	private DataBaseQueryManager queryManager;

	/*
	 * Storing Selected Data
	 */
	private long selectedHouseID;
	private long selectedRoomID;

	private boolean initialControllerDataLoaded = false;
	private boolean initialRoomDataLoaded = false;
	private boolean initialHouseDataLoaded = false;
	private HashMap<String, NetworkSet> mNetSets;

	public HeadlessFragment() {
		mNetSets = new HashMap<String, NetworkSet>();
	}

	/**
	 * Create the necessary Adapters, Threads and DataBaseAccess also set this
	 * fragment to be retained
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		queryManager = new DataBaseQueryManager(getActivity().getContentResolver(), this);
		houseAdapter = new ListAdapter(getActivity(), null, 0);
		roomAdapter = new ListAdapter(getActivity(), null, 0);
		controllerAdapter = new GridAdapter(getActivity(), null, 0);
		setRetainInstance(true);

	}

	/**
	 * Restart network threads if they were at all started
	 */
	@Override
	public void onStart() {
		super.onStart();
		Iterator<Map.Entry<String, NetworkSet>> iter = mNetSets.entrySet().iterator();
		while (iter.hasNext()) {
			iter.next().getValue().resume();
		}
	}

	/**
	 * Suspend network threads
	 */
	@Override
	public void onStop() {
		super.onStop();
		Iterator<Map.Entry<String, NetworkSet>> iter = mNetSets.entrySet().iterator();
		while (iter.hasNext()) {
			iter.next().getValue().pause();
		}
	}

	/**
	 * Stop network threads
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Iterator<Map.Entry<String, NetworkSet>> iter = mNetSets.entrySet().iterator();
		while (iter.hasNext()) {
			iter.next().getValue().kill();
		}
	}

	public void onControllersDataChanged() {
		Cursor c = controllerAdapter.getCursor();
		c.moveToPosition(-1);
		ArrayList<String> ips = new ArrayList<String>();
		while (c.moveToNext()) {
			ips.add(c.getString(c.getColumnIndex(DBHandler.CONTROLLER_IP)));
		}
		addNetSetsForIps(ips);

	}

	private void addNetSetsForIps(ArrayList<String> ips) {
		// get a list of NetworkSets that don't need to be modified
		ArrayList<String> temp = new ArrayList<String>();
		for (String ip : ips) {
			if (mNetSets.containsKey(ip)) {
				temp.add(ip);
			}
		}
		// put all the modifiable ons on the stack
		Stack<NetworkSet> modifiable = new Stack<NetworkSet>();
		Iterator<Map.Entry<String, NetworkSet>> iter = mNetSets.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, NetworkSet> entry = iter.next();
			if (!ips.contains(entry.getKey())) {
				modifiable.push(entry.getValue());
				iter.remove();
			}
		}
		// for each ip that doesn't yet have a NetworkSet either modify an old
		// one or make a new one
		for (String ip : ips) {
			if (!temp.contains(ip)) {
				try {
					if (!modifiable.isEmpty()) {
						NetworkSet mod = modifiable.pop();
						mod.registerChange(ip);
						mNetSets.put(ip, mod);
					} else {
						NetworkSet mod = new NetworkSet(this, ip);
						mNetSets.put(ip, mod);
						mod.init();
					}
				} catch (UnknownHostException e) {
					mNetSets.remove(ip).kill();
					reportFailiureToConnectToServer(ip);
					e.printStackTrace();
				} catch (IOException e) {
					mNetSets.remove(ip).kill();
					reportFailiureToConnectToServer(ip);
					e.printStackTrace();
				}

			}
		}
		// get rid of extra NetworkSets
		while (!modifiable.isEmpty()) {
			modifiable.pop().kill();
		}

	}

	/**
	 * Requeries the appropriate sources: if house dataset changed: requeries
	 * the database if room dataset changed: requeries the database if
	 * controller dataset changed: requeries the database and server
	 * 
	 * @param token
	 *            The token value of the dataset: 0 for house dataset 1 for room
	 *            dataset 2 for controller dataset
	 * 
	 * @param adapter
	 *            The adapter to which the results are returned.
	 * 
	 */
	@Override
	public void dataSetChanged(int token, Object adapter) {
		String selection;
		switch (token) {
		// House data change
		case 0:
			String[] houseProjection = { DBHandler.HOUSE_ID, DBHandler.HOUSE_NAME, DBHandler.HOUSE_IMAGE_NAME };
			queryManager
					.startQuery(0, houseAdapter, DBProvider.HOUSES_URI, houseProjection, null, null, null);
			break;
		// Room data change
		case 1:
			if (selectedHouseID >= 0) {
				String[] roomProjection = { DBHandler.ROOM_ID, DBHandler.ROOM_NAME, DBHandler.ROOM_IMAGE_NAME };
				selection = DBHandler.HOUSE_ID + "=?";
				String[] roomSelectionArgs = { selectedHouseID + "" };
				queryManager.startQuery(1, roomAdapter, DBProvider.ROOMS_URI, roomProjection, selection,
						roomSelectionArgs, null);
			}
			// Controller data change
			break;
		case 2:
			if (selectedHouseID >= 0 && selectedRoomID >= 0) {
				/*
				 * Start DBsearch
				 */
				String[] controllerProjection = { DBHandler.CONTROLLER_ID, DBHandler.CONTROLLER_NAME,
						DBHandler.CONTROLLER_IMAGE_NAME, DBHandler.CONTROLLER_TYPE, DBHandler.CONTROLLER_IP,
						DBHandler.CONTROL_PIN_NUMBER };
				selection = DBHandler.ROOM_ID + "=?";
				String[] controllerSelectionArgs = { selectedRoomID + "" };
				queryManager.startQuery(2, controllerAdapter, DBProvider.CONTROLLERS_URI,
						controllerProjection, selection, controllerSelectionArgs, null);
				/*
				 * Start NetStatusLookup
				 */
				Iterator<Map.Entry<String, NetworkSet>> iter = mNetSets.entrySet().iterator();
				while (iter.hasNext()) {
					iter.next().getValue().addToSenderQueue(new InitialStateQueryPacket());
					;
				}
//				mNetworkSender.addToQueue(new InitialStateQueryPacket());

			}
			break;
		default:
		}

	}

	/**
	 * Replaces the database cursor on the given adapter.
	 * 
	 * @param cursor
	 *            The new cursor to be used.
	 * @param adapter
	 *            The adapter to which the cursor is to be posted.
	 * 
	 */
	@Override
	public void replaceCursor(Cursor cursor, Object adapter) {

		Cursor temp = ((CursorAdapter) adapter).swapCursor(cursor);
		if (temp != null)
			temp.close();

	}

	/*
	 * Getters for data.
	 */

	public long getSelectedHouseID() {
		return selectedHouseID;
	}

	public long getSelectedRoomID() {
		return selectedRoomID;
	}

	public ListAdapter getRoomsAdapter() {
		return roomAdapter;
	}

	public GridAdapter getControllersAdapter() {
		return controllerAdapter;
	}

	public ListAdapter getHousesAdapter() {
		return houseAdapter;
	}

	public DataBaseQueryManager getQueryManager() {
		return queryManager;
	}

	/*
	 * Setters for data.
	 */
	public void setSelectedHouseID(long houseID) {
		selectedHouseID = houseID;

	}

	public void setSelectedRoomID(long roomID) {
		selectedRoomID = roomID;
		onControllersDataChanged();
	}

	/**
	 * Opens or returns a socket to the selected server on the passed port.
	 * 
	 * @param The
	 *            port to open the socket to.
	 * @return The Socket object to the server.
	 * @throws IOException
	 * @throws UnknownHostException
	 */
//	@Override
//	synchronized public Socket acquireSocket(int port) throws UnknownHostException, IOException {
//		if ((mSocket == null) || mSocket.isClosed()) {
//
//			mSocket = new Socket(InetAddress.getByName(selectedRoomIp), port);
//
//		}
//		return mSocket;
//	}

	/**
	 * Adds a packet to be sent to the server.
	 * 
	 * @param switchPacket
	 *            The switch packet to be sent to the server.
	 */
	@Override
	public void addToNetworkSender(String senderIp, Sendable switchPacket) {
		mNetSets.get(senderIp).addToSenderQueue(switchPacket);

	}

	/**
	 * Posts a PinStatus to the UI.
	 * 
	 * @param newData
	 *            The PinStatus to be posted to the UI.
	 */
	@Override
	public void postValueChange(PinStatus newData) {
		controllerAdapter.addToStatusSet(newData);
	}

	/**
	 * Posts a PinStatusSet to the UI.
	 * 
	 * @param pinStatusSet
	 *            The PinStatusSet to be posted to the UI.
	 */
	@Override
	public void postLookupValues(PinStatusSet pinStatusSet) {
		controllerAdapter.addStatusSet(pinStatusSet);
//		getActivity().setProgressBarIndeterminateVisibility(false);
		getActivity().findViewById(R.id.linlaHeaderProgress).setVisibility(View.GONE);
		Toast.makeText(getActivity(), "MOOOO", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean isInitialHouseDataLoaded() {
		return initialHouseDataLoaded;
	}

	@Override
	public void setInitialHouseDataLoaded(boolean b) {
		this.initialHouseDataLoaded = b;

	}

	@Override
	public boolean isInitialRoomDataLoaded() {
		return initialRoomDataLoaded;
	}

	@Override
	public void setInitialRoomDataLoaded(boolean b) {
		this.initialRoomDataLoaded = b;

	}

	public boolean isInitialControllerDataLoaded() {
		return initialControllerDataLoaded;
	}

	public void setInitialControllerDataLoaded(boolean initialControllerDataLoaded) {
		this.initialControllerDataLoaded = initialControllerDataLoaded;
	}

//	@Override
	public void reportFailiureToConnectToServer(String ip) {
		if (getActivity() == null)
			return;
//		getActivity().findViewById(R.id.linlaHeaderProgress).setVisibility(View.GONE);// TODO
//		mNetworkListener = new NetworkListenerAsyncTask(this, this, this);
//		mNetworkSender = new NetworkSenderThread(this);
		Toast.makeText(getActivity(), "Failed To Connect To Host" + ip, Toast.LENGTH_SHORT).show();
	}

}
