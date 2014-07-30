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
import android.util.Log;
import android.widget.Toast;

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
import com.example.houseremote.network.NetworkListenerAsyncTask;
import com.example.houseremote.network.NetworkSenderThread;
import com.example.houseremote.network.PinStatus;
import com.example.houseremote.network.PinStatusSet;
import com.example.houseremote.network.SwitchPacket;

public class HeadlessFragment extends Fragment implements ReplyListener, ControllersAdapterProvider,
		RoomsAdapterProvider, HousesAdapterProvider, NetworkCommandListener, 
		SocketProvider,  SwitchStateListener, UILockupListener {
	


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



	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		queryManager = new DataBaseQueryManager(getActivity().getContentResolver(), this);
		houseAdapter = new ListAdapter(getActivity(), null, 0);
		roomAdapter = new ListAdapter(getActivity(), null, 0);
		controllerAdapter = new GridAdapter(getActivity(), null, 0);
		mNetworkListener = new NetworkListenerAsyncTask(this,this,this);
		mNetworkSender = new NetworkSenderThread(this);
		setRetainInstance(true);

	}

	@SuppressLint("NewApi")
	@Override
	public void onStart() {
		super.onStart();
		if(android.os.Build.VERSION.SDK_INT>=11){
			mNetworkListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
		}
		else{
			mNetworkListener.execute((Void[])null);
		}
		
		mNetworkSender.start();
	}
	
	@Override
	public void onStop() {
		super.onStop();
//		mNetworkListener.pause();
//		mNetworkSender.pause();
//		mUIManager.pause();
//		mUIUpdater.pasue();
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
				mNetworkSender.addToQueue(new SwitchPacket(0,true));

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
		mNetworkSender.registerChange();
	}



	@Override
	public void postValueChange(PinStatus newData) {
		// TODO needs to be passed to activity and then to controllers fragment
		controllerAdapter.addToStatusSet(newData);
		Toast.makeText(getActivity(), "switch change", Toast.LENGTH_SHORT).show();
	}



	@Override
	public void addToNetworkSender(SwitchPacket switchPacket) {
		mNetworkSender.addToQueue(switchPacket);
		synchronized (mNetworkSender) {
			mNetworkSender.notify();
		}

	}



	@Override
	synchronized public Socket acquireSocket(int port) {
		if (mSocket == null) {
			try {
				mSocket = new Socket(InetAddress.getByName(selectedRoomIp), port);
				Log.d("MOO","OPENING SOCKET");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mSocket;
	}

	@Override
	public void postLookupValues(PinStatusSet pinStatusSet) {
		controllerAdapter.addStatusSet(pinStatusSet);
		// TODO needs to be passed to activity and then to controllers fragment
		Toast.makeText(getActivity(), "switch state found", Toast.LENGTH_SHORT).show();
		
	}

}
