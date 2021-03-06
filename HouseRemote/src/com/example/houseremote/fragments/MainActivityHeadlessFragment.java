package com.example.houseremote.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.Toast;

import com.example.houseremote.R;
import com.example.houseremote.activities.EditHouseActivity;
import com.example.houseremote.activities.EditLightSwitchActivity;
import com.example.houseremote.activities.EditRoomActivity;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseAsyncQueryHandler;
import com.example.houseremote.database.adapters.GridAdapter;
import com.example.houseremote.database.adapters.ListAdapter;
import com.example.houseremote.fragments.interfaces.ControllersActivityHeadlessFragmentInterface;
import com.example.houseremote.fragments.interfaces.MainActivityHeadlessFragmentInterface;
import com.example.houseremote.fragments.interfaces.RoomsActivityHeadlessFragmentInterface;
import com.example.houseremote.interfaces.RunnableOnUIThread;
import com.example.houseremote.network.NetworkSet;
import com.example.houseremote.network.dataclasses.InitialStateQueryPacket;
import com.example.houseremote.network.dataclasses.PinStatus;
import com.example.houseremote.network.dataclasses.PinStatusSet;
import com.example.houseremote.network.dataclasses.ServerInfo;
import com.example.houseremote.network.interfaces.Sendable;

public class MainActivityHeadlessFragment extends Fragment implements
		MainActivityHeadlessFragmentInterface, RoomsActivityHeadlessFragmentInterface,
		ControllersActivityHeadlessFragmentInterface {

	/*
	 * String constants for DB lookups
	 */
	private static final String[] houseProjection = { DBHandler.HOUSE_ID, DBHandler.HOUSE_NAME,
			DBHandler.HOUSE_IMAGE_NAME };
	private static final String[] roomProjection = { DBHandler.ROOM_ID, DBHandler.ROOM_NAME,
			DBHandler.ROOM_IMAGE_NAME };
	private static final String[] controllerProjection = { DBHandler.CONTROLLER_ID,
			DBHandler.CONTROLLER_NAME, DBHandler.CONTROLLER_IMAGE_NAME, DBHandler.CONTROLLER_TYPE,
			DBHandler.CONTROLLER_IP, DBHandler.CONTROLLER_PORT, DBHandler.CONTROL_PIN_NUMBER };
	private static final String roomSelection = DBHandler.HOUSE_ID_ALT + "=?";
	private static final String controllerSelection = DBHandler.ROOM_ID_ALT + "=?";

	/*
	 * UI adapters for other fragments
	 */
	private GridAdapter controllerAdapter;
	private ListAdapter houseAdapter;
	private ListAdapter roomAdapter;

	/*
	 * NetworkSet for each connected controller
	 */
	private HashMap<String, NetworkSet> mNetSets;
	
	/*
	 * DatabaseAccessObject 
	 */
	private DataBaseAsyncQueryHandler queryManager;

	/*
	 * Storing Selected Data
	 */
	private long selectedHouseID=0l;
	private long selectedRoomID=0l;
	private boolean initialControllerDataLoaded = false;
	private boolean initialRoomDataLoaded = false;
	private boolean initialHouseDataLoaded = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNetSets = new HashMap<String, NetworkSet>();
		houseAdapter = new ListAdapter(getActivity(), null, 0);
		roomAdapter = new ListAdapter(getActivity(), null, 0);
		controllerAdapter = new GridAdapter(getActivity(), null, 0);
		queryManager = new DataBaseAsyncQueryHandler(getActivity().getContentResolver(), this);
		setRetainInstance(true);

	}

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

	@Override
	public void onQueryFinished(Cursor cursor, CursorAdapter adapter) {

		Cursor temp = adapter.swapCursor(cursor);
		if (temp != null)
			temp.close();
		if (adapter == controllerAdapter) {
			onControllersDataChanged();
			Iterator<Map.Entry<String, NetworkSet>> iter = mNetSets.entrySet().iterator();
			if (iter.hasNext())
				getActivity().findViewById(R.id.linlaHeaderProgress).setVisibility(View.VISIBLE);
			while (iter.hasNext()) {
				iter.next().getValue().addToSenderQueue(new InitialStateQueryPacket());
			}
		}

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

	public DataBaseAsyncQueryHandler getQueryManager() {
		return queryManager;
	}

	/*
	 * Setters for data.
	 */
	public void setSelectedHouseID(long houseID) {
		selectedHouseID = houseID;

	}

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

	public void connectFailedOnIp(String ip) {
		mNetSets.remove(ip).kill();
		reportFailiureToConnectToServer(ip);
	}

	public void reportFailiureToConnectToServer(String ip) {
		if (getActivity() == null)
			return;
		if(mNetSets.isEmpty()){//if connection failed to all hosts
			Toast.makeText(getActivity(), "Failed To Connect To Host" + ip, Toast.LENGTH_SHORT).show();
			getActivity().findViewById(R.id.linlaHeaderProgress).setVisibility(View.GONE);
		}
	}

	@Override
	public void onControllerDataChanged() {
		if (selectedHouseID > 0 && selectedRoomID > 0) {
			/*
			 * Start DBsearch
			 */

			String[] controllerSelectionArgs = { selectedRoomID + "" };
			queryManager.startQuery(2, controllerAdapter, DBProvider.CONTROLLERS_URI, controllerProjection,
					controllerSelection, controllerSelectionArgs, null);

		}

	}

	@Override
	public void onHouseDataChanged() {

		queryManager.startQuery(0, houseAdapter, DBProvider.HOUSES_URI, houseProjection, null, null, null);

	}

	@Override
	public void onRoomDataChanged() {
		if (selectedHouseID > 0) {

			String[] roomSelectionArgs = { selectedHouseID + "" };
			queryManager.startQuery(1, roomAdapter, DBProvider.ROOMS_URI, roomProjection, roomSelection,
					roomSelectionArgs, null);
		}

	}

	@Override
	public void execRequiredFunction(RunnableOnUIThread uiReadable) {
		uiReadable.runOnUIThread(this);

	}

	@Override
	public void onInsertFinished(long parseId, int token) {// TODO Should pass to appropriate object
		Intent i;
		switch(token){
		case 0:
			i = new Intent(getActivity(), EditHouseActivity.class);
			i.putExtra(DBHandler.HOUSE_ID, parseId);
			startActivityForResult(i,0);
			break;
		case 1:
			i = new Intent(getActivity(), EditRoomActivity.class);
			i.putExtra(DBHandler.ROOM_ID, parseId);
			startActivityForResult(i,1);
			break;
		case 2:
			i = new Intent(getActivity(), EditLightSwitchActivity.class);
			i.putExtra(DBHandler.CONTROLLER_ID, parseId);// give info about the
			startActivityForResult(i, 2);// start the activity
			break;
		}
	}



}
