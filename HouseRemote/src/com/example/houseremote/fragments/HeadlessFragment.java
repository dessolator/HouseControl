package com.example.houseremote.fragments;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.widget.Toast;

import com.example.houseremote.adapters.GridAdapter;
import com.example.houseremote.adapters.ListAdapter;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseQueryManager;
import com.example.houseremote.interfaces.ControllersAdapterProvider;
import com.example.houseremote.interfaces.HousesAdapterProvider;
import com.example.houseremote.interfaces.NetworkCallbackListener;
import com.example.houseremote.interfaces.NetworkCommandListener;
import com.example.houseremote.interfaces.ReplyListener;
import com.example.houseremote.interfaces.RoomsAdapterProvider;
import com.example.houseremote.interfaces.SocketProvider;
import com.example.houseremote.interfaces.SwitchStateListener;
import com.example.houseremote.network.ControllerStateQueryAsyncTask;
import com.example.houseremote.network.NetData;
import com.example.houseremote.network.NetworkListenerAsyncTask;
import com.example.houseremote.network.NetworkSenderThread;
import com.example.houseremote.network.PinStatusSet;
import com.example.houseremote.network.SwitchPacket;

public class HeadlessFragment extends Fragment implements ReplyListener, ControllersAdapterProvider,
		RoomsAdapterProvider, HousesAdapterProvider, NetworkCommandListener, SwitchStateListener,
		SocketProvider, NetworkCallbackListener {

	private GridAdapter controllerAdapter;
	private ListAdapter houseAdapter;
	private ListAdapter roomAdapter;
	private DataBaseQueryManager queryManager;
	private String selectedHouse;
	private String selectedRoom;
	private String selectedRoomIp;
	private NetworkListenerAsyncTask mNetworkListener;
	private NetworkSenderThread mNetworkSender;
	private Socket mSocket;
	private ControllerStateQueryAsyncTask mControllerStateQuery;
	private ControllersFragment mControllersFragment;// TODO

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		queryManager = new DataBaseQueryManager(getActivity().getContentResolver(), this);
		houseAdapter = new ListAdapter(getActivity(), null, 0);
		roomAdapter = new ListAdapter(getActivity(), null, 0);
		controllerAdapter = new GridAdapter(getActivity(), null, 0);
		mNetworkListener = new NetworkListenerAsyncTask(this, this);
		mNetworkSender = new NetworkSenderThread(this);
		mControllerStateQuery = new ControllerStateQueryAsyncTask(this);
		setRetainInstance(true);

	}

	@Override
	public void dataSetChanged(int token, Object adapter) {
		String selection;
		switch (token) {
		// House data change
		case 0:
			String[] houseProjection = { DBHandler.HOUSE_ID, DBHandler.HOUSE_NAME, DBHandler.HOUSE_IMAGE_NAME };
			queryManager
					.startQuery(0, houseAdapter, DBProvider.HOUSES_URI, houseProjection, null, null, null);
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
				if (mControllerStateQuery.getStatus() != AsyncTask.Status.RUNNING) {
					mControllerStateQuery.execute();
				}
			}
			break;
		default:
		}

	}

	@Override
	public void replaceCursor(Cursor cursor, Object adapter) {
		Cursor temp = ((CursorAdapter) adapter).swapCursor(cursor);
		if (temp != null)
			temp.close();

	}

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

	public void setSelectedHouse(String houseName) {
		selectedHouse = houseName;

	}

	public void setSelectedRoomWithIp(String roomName, String roomIp) {
		selectedRoom = roomName;
		selectedRoomIp = roomIp;
		mNetworkListener.registerChange();
	}

	@Override
	public void startNetworkListener() {
		if (mNetworkListener.getStatus() != AsyncTask.Status.RUNNING)
			mNetworkListener.execute((Void) null);

	}

	@Override
	public void stopNetworkListener() {
		mNetworkListener.registerKill();
		try {
			mSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}



	@Override
	public void postValueChange(NetData newData) {
		// TODO needs to be passed to activity and then to controllers fragment
		Toast.makeText(getActivity(), "switch change", Toast.LENGTH_LONG).show();
	}



	@Override
	public void addToNetworkSender(SwitchPacket switchPacket) {
		mNetworkSender.addToQueue(switchPacket);
		synchronized (mNetworkSender) {
			mNetworkSender.notify();
		}

	}

	@Override
	public void startNetworkSender() {
		if (!mNetworkSender.isAlive())
			mNetworkSender.start();

	}

	@Override
	public void stopNetworkSender() {
		mNetworkSender.registerKill();
		mNetworkSender.interrupt();

	}

	@Override
	public void pinStateQueryComplete(PinStatusSet result) {
		controllerAdapter.addStatusSet(result);
		mControllersFragment.unlockInterface();// TODO twopart lock
	}

	public void setmControllersFragment(ControllersFragment mControllersFragment) {
		this.mControllersFragment = mControllersFragment;
	}

	@Override
	public Socket acquireSocket() {
		if (mSocket == null) {
			try {
				mSocket = new Socket(InetAddress.getByName(selectedRoomIp), 55000);//TODO hardcoded
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mSocket;
	}

}
