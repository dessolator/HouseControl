package com.example.houseremote.fragments;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.Toast;

import com.example.houseremote.R;
import com.example.houseremote.adapters.GridAdapter;
import com.example.houseremote.adapters.ListAdapter;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseQueryManager;
import com.example.houseremote.interfaces.ControllersAdapterProvider;
import com.example.houseremote.interfaces.HousesAdapterProvider;
import com.example.houseremote.interfaces.NetworkCommandListener;
import com.example.houseremote.interfaces.ReplyListener;
import com.example.houseremote.interfaces.RoomsAdapterProvider;
import com.example.houseremote.interfaces.SocketProvider;
import com.example.houseremote.interfaces.SwitchStateListener;
import com.example.houseremote.interfaces.UILockupListener;
import com.example.houseremote.network.InitialStateQueryPacket;
import com.example.houseremote.network.NetworkListenerAsyncTask;
import com.example.houseremote.network.NetworkSenderThread;
import com.example.houseremote.network.PinStatus;
import com.example.houseremote.network.PinStatusSet;
import com.example.houseremote.network.PinFlipPacket;

public class HeadlessFragment extends Fragment implements ReplyListener, ControllersAdapterProvider,
		RoomsAdapterProvider, HousesAdapterProvider, NetworkCommandListener, SocketProvider,
		SwitchStateListener, UILockupListener {

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
	private String selectedHouse;
	private String selectedRoom;
	private String selectedRoomIp;

	/*
	 * Network Threads
	 */
	private NetworkListenerAsyncTask mNetworkListener;
	private NetworkSenderThread mNetworkSender;
	/*
	 * Common socket
	 */
	private Socket mSocket;

	private boolean initialControllerDataLoaded = false;
	private boolean initialRoomDataLoaded = false;
	private boolean initialHouseDataLoaded = false;

	public HeadlessFragment() {
		mNetworkListener = new NetworkListenerAsyncTask(this, this, this);
		mNetworkSender = new NetworkSenderThread(this);
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
		if (mSocket != null)
			reStartNetwork();
	}

	/**
	 * Suspend network threads
	 */
	@Override
	public void onStop() {
		super.onStop();
		mNetworkListener.registerPause();
		mNetworkSender.registerPause();
	}

	/**
	 * Stop network threads
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		mNetworkListener.registerKill();
		mNetworkSender.registerKill();
	}

	/**
	 * Starts network threads if they weren't started, unpauses them if they
	 * were paused.
	 */
	@SuppressLint("NewApi")
	private void reStartNetwork() {
		if(mNetworkListener.getStatus().equals(AsyncTask.Status.FINISHED)) return;
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			if (!mNetworkListener.getStatus().equals(AsyncTask.Status.RUNNING))
				mNetworkListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
			else
				mNetworkListener.unpause();
		} else {
			if (!mNetworkListener.getStatus().equals(AsyncTask.Status.RUNNING))
				mNetworkListener.execute((Void[]) null);
			else
				mNetworkListener.unpause();
		}
		if (!mNetworkSender.isAlive())
			mNetworkSender.start();
		else
			mNetworkSender.unpause();
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
			if (selectedHouse != null) {
				String[] roomProjection = { DBHandler.ROOM_ID, DBHandler.ROOM_NAME,
						DBHandler.ROOM_IMAGE_NAME, DBHandler.CONTROLLER_IP };
				selection = DBHandler.HOUSE_NAME + "=?";
				String[] roomSelectionArgs = { selectedHouse };
				queryManager.startQuery(1, roomAdapter, DBProvider.ROOMS_URI, roomProjection, selection,
						roomSelectionArgs, null);
			}
			// Controller data change
			break;
		case 2:
			if (selectedHouse != null && selectedRoom != null) {
				/*
				 * Start DBsearch
				 */
				String[] controllerProjection = { DBHandler.CONTROLLER_ID,
						DBHandler.CONTROLLER_INTERFACE_NAME, DBHandler.CONTROLLER_IMAGE_NAME,
						DBHandler.CONTROLLER_TYPE, DBHandler.CONTROL_PIN1_NUMBER };
				selection = DBHandler.HOUSE_NAME + "=?" + " AND " + DBHandler.ROOM_NAME + "=?";
				String[] controllerSelectionArgs = { selectedHouse, selectedRoom };
				queryManager.startQuery(2, controllerAdapter, DBProvider.CONTROLLERS_URI,
						controllerProjection, selection, controllerSelectionArgs, null);
				/*
				 * Start NetStatusLookup
				 */
				mNetworkSender.addToQueue(new InitialStateQueryPacket());

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

	public String getSelectedHouse() {
		return selectedHouse;
	}

	public String getSelectedRoom() {
		return selectedRoom;
	}

	public String getSelectedRoomIp() {
		return selectedRoomIp;
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
	public void setSelectedHouse(String houseName) {
		selectedHouse = houseName;

	}

	public void setSelectedRoomWithIp(String roomName, String roomIp) {
		selectedRoom = roomName;
		selectedRoomIp = roomIp;
		reStartNetwork();
		mNetworkListener.registerChange();
		mNetworkSender.registerChange();
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
	@Override
	synchronized public Socket acquireSocket(int port) throws UnknownHostException, IOException {
		if ((mSocket == null) || mSocket.isClosed()) {

				mSocket = new Socket(InetAddress.getByName(selectedRoomIp), port);
			
		}
		return mSocket;
	}

	/**
	 * Adds a packet to be sent to the server.
	 * 
	 * @param switchPacket
	 *            The switch packet to be sent to the server.
	 */
	@Override
	public void addToNetworkSender(PinFlipPacket switchPacket) {
		mNetworkSender.addToQueue(switchPacket);
		synchronized (mNetworkSender) {
			mNetworkSender.notify();
		}

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

	@Override
	public void reportFailiureToConnectToServer() {
		if(getActivity()==null) return;
		getActivity().findViewById(R.id.linlaHeaderProgress).setVisibility(View.GONE);
		mNetworkListener= new NetworkListenerAsyncTask(this, this, this);
		mNetworkSender = new NetworkSenderThread(this);
		Toast.makeText(getActivity(), "Failed To Connect To Host", Toast.LENGTH_SHORT).show();
	}

}
