package com.example.houseremote.fragments;

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
import com.example.houseremote.database.DataBaseAsyncQueryHandler;
import com.example.houseremote.database.adapters.GridAdapter;
import com.example.houseremote.interfaces.ControllersActivityHeadlessFragmentInterface;
import com.example.houseremote.interfaces.RunnableOnUIThread;
import com.example.houseremote.network.NetworkSet;
import com.example.houseremote.network.dataclasses.InitialStateQueryPacket;
import com.example.houseremote.network.dataclasses.PinStatus;
import com.example.houseremote.network.dataclasses.PinStatusSet;
import com.example.houseremote.network.dataclasses.ServerInfo;
import com.example.houseremote.network.interfaces.Sendable;

public class ControllersActivityHeadlessFragment extends Fragment implements ControllersActivityHeadlessFragmentInterface {

	/*
	 * String constants for DB lookups
	 */
	private static final String[] controllerProjection = { DBHandler.CONTROLLER_ID,
			DBHandler.CONTROLLER_NAME, DBHandler.CONTROLLER_IMAGE_NAME, DBHandler.CONTROLLER_TYPE,
			DBHandler.CONTROLLER_IP, DBHandler.CONTROLLER_PORT, DBHandler.CONTROL_PIN_NUMBER };
	private static final String controllerSelection = DBHandler.ROOM_ID_ALT + "=?";

	/*
	 * UI adapters for other fragments
	 */
	private GridAdapter controllerAdapter;

	/*
	 * Storing Selected Data
	 */
	private long selectedRoomID;
	private DataBaseAsyncQueryHandler queryManager;
	private boolean initialControllerDataLoaded = false;
	private HashMap<String, NetworkSet> mNetSets = new HashMap<String, NetworkSet>();

	/**
	 * Create the necessary Adapters, Threads and DataBaseAccess also set this
	 * fragment to be retained
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		controllerAdapter = new GridAdapter(getActivity(), null, 0);
		queryManager = new DataBaseAsyncQueryHandler(getActivity().getContentResolver(), this);
		setRetainInstance(true);

	}

	/**
	 * Restart network threads if they were at all started
	 */
	@Override
	public void onStart() {
		super.onStart();
		if (selectedRoomID > 0) {
			Iterator<Map.Entry<String, NetworkSet>> iter = mNetSets.entrySet().iterator();
			while (iter.hasNext()) {
				iter.next().getValue().resume();
			}
		}
	}

	/**
	 * Suspend network threads
	 */
	@Override
	public void onStop() {
		super.onStop();
		if (selectedRoomID > 0) {
			Iterator<Map.Entry<String, NetworkSet>> iter = mNetSets.entrySet().iterator();
			while (iter.hasNext()) {
				iter.next().getValue().pause();
			}
		}
	}

	/**
	 * Stop network threads
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (selectedRoomID > 0) {
			Iterator<Map.Entry<String, NetworkSet>> iter = mNetSets.entrySet().iterator();
			while (iter.hasNext()) {
				iter.next().getValue().kill();
				iter.remove();
			}
		}
	}

	public void onControllersDataChanged() {
		Cursor c = controllerAdapter.getCursor();
		if (c == null)
			return;
		c.moveToPosition(-1);
		ArrayList<ServerInfo> ips = new ArrayList<ServerInfo>();
		while (c.moveToNext()) {
			ips.add(new ServerInfo(null, c.getString(c.getColumnIndex(DBHandler.CONTROLLER_IP)), c.getInt(c
					.getColumnIndex(DBHandler.CONTROLLER_PORT))));
		}
		addNetSetsForIps(ips);

	}

	private void addNetSetsForIps(ArrayList<ServerInfo> ips) {
		// get a list of NetworkSets that don't need to be modified
		ArrayList<ServerInfo> temp = new ArrayList<ServerInfo>();
		for (ServerInfo item : ips) {
			if (mNetSets.containsKey(item.getIp())) {
				if (mNetSets.get(item.getIp()).getPort() != item.getPort()) {
					mNetSets.get(item.getIp()).registerChange(item.getIp(), item.getPort());
				}
				temp.add(item);
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
		for (ServerInfo item : ips) {
			if (!temp.contains(item.getIp())) {

				if (!modifiable.isEmpty()) {
					NetworkSet mod = modifiable.pop();
					mod.registerChange(item.getIp(), item.getPort());
					mNetSets.put(item.getIp(), mod);
				} else {
					NetworkSet mod = new NetworkSet(this, item.getIp(), item.getPort());
					mNetSets.put(item.getIp(), mod);
					mod.init();
				}

			}
		}
		// get rid of extra NetworkSets
		while (!modifiable.isEmpty()) {
			modifiable.pop().kill();
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
	public void onQueryFinished(Cursor cursor, Object adapter) {

		Cursor temp = ((CursorAdapter) adapter).swapCursor(cursor);
		if (temp != null)
			temp.close();
		if (adapter == controllerAdapter) {
			onControllersDataChanged();
			Iterator<Map.Entry<String, NetworkSet>> iter = mNetSets.entrySet().iterator();
			if (iter.hasNext())
				getActivity().findViewById(R.id.linlaHeaderProgress).setVisibility(View.VISIBLE);
			while (iter.hasNext()) {
				iter.next().getValue().addToSenderQueue(new InitialStateQueryPacket());
				;
			}
		}

	}

	/*
	 * Getters for data.
	 */





	public GridAdapter getControllersAdapter() {
		return controllerAdapter;
	}


	public DataBaseAsyncQueryHandler getQueryManager() {
		return queryManager;
	}

	/*
	 * Setters for data.
	 */

	public void setSelectedRoomID(long roomID) {
		if (roomID <= 0) {
			Iterator<Map.Entry<String, NetworkSet>> iter = mNetSets.entrySet().iterator();
			while (iter.hasNext()) {
				iter.next().getValue().kill();
				iter.remove();
			}
		}
		selectedRoomID = roomID;

	}

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
	public void postValueChange(PinStatus newData) {
		controllerAdapter.addToStatusSet(newData);
	}

	/**
	 * Posts a PinStatusSet to the UI.
	 * 
	 * @param pinStatusSet
	 *            The PinStatusSet to be posted to the UI.
	 */

	public void postLookupValues(PinStatusSet pinStatusSet) {
		controllerAdapter.addStatusSet(pinStatusSet);
		getActivity().findViewById(R.id.linlaHeaderProgress).setVisibility(View.GONE);
	}

	


	public boolean isInitialControllerDataLoaded() {
		return initialControllerDataLoaded;
	}

	public void setInitialControllerDataLoaded(boolean initialControllerDataLoaded) {
		this.initialControllerDataLoaded = initialControllerDataLoaded;
	}

	public void connectFailedOnIp(String ip) {
		mNetSets.remove(ip).kill();
		reportFailiureToConnectToServer(ip);
	}

	public void reportFailiureToConnectToServer(String ip) {
		if (getActivity() == null)
			return;
		Toast.makeText(getActivity(), "Failed To Connect To Host" + ip, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onControllerDataChanged() {
		if (selectedRoomID > 0) {
			/*
			 * Start DBsearch
			 */

			String[] controllerSelectionArgs = { selectedRoomID + "" };
			queryManager.startQuery(2, controllerAdapter, DBProvider.CONTROLLERS_URI, controllerProjection,
					controllerSelection, controllerSelectionArgs, null);

		}

	}



	

	@Override
	public void execRequiredFunction(RunnableOnUIThread uiReadable) {
		uiReadable.runOnUIThread(this);

	}

	@Override
	public void onInsertFinished(long parseId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHouseDataChanged() {
	}

	@Override
	public void onRoomDataChanged() {
	}

	@Override
	public long getSelectedRoomID() {
		// TODO Auto-generated method stub
		return 0;
	}

}
