package com.example.houseremote.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;

import com.example.houseremote.adapters.GridAdapter;
import com.example.houseremote.adapters.ListAdapter;
import com.example.houseremote.database.AsyncQueryManager;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.AsyncQueryManager.ReplyListener;
import com.example.houseremote.fragments.ControllersFragment.ControllersAdapterProvider;
import com.example.houseremote.fragments.HousesFragment.HousesAdapterProvider;
import com.example.houseremote.fragments.RoomsFragment.RoomsAdapterProvider;

public class HeadlessFragment extends Fragment implements ReplyListener, ControllersAdapterProvider, RoomsAdapterProvider,HousesAdapterProvider{
	private GridAdapter controllerAdapter;
	private ListAdapter houseAdapter;
	private ListAdapter roomAdapter;
	private AsyncQueryManager queryManager;
	private String selectedHouse;
	private String selectedRoom;
	private String selectedRoomIp;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		queryManager=new AsyncQueryManager(getActivity().getContentResolver(),	this);
		houseAdapter = new ListAdapter(getActivity(), null, 0);
		roomAdapter = new ListAdapter(getActivity(), null, 0);
		controllerAdapter = new GridAdapter(getActivity(), null, 0);
		setRetainInstance(true);
		
	}
	@Override
	public void dataSetChanged(int token, Object adapter) {
		String selection;
		switch(token){		
			case 0:
				String[] houseProjection= { DBHandler.HOUSE_ID, DBHandler.HOUSE_NAME, DBHandler.HOUSE_IMAGE_NAME };
				queryManager.startQuery(0, houseAdapter, DBProvider.HOUSES_URI, houseProjection, null, null, null);
			case 1:
				if(selectedHouse!=null){
				String[] roomProjection = { DBHandler.ROOM_ID, DBHandler.ROOM_NAME, DBHandler.ROOM_IMAGE_NAME,
						DBHandler.CONTROLLER_IP };
				selection = DBHandler.HOUSE_NAME + "=?";
				String[] roomSelectionArgs = { selectedHouse };
				queryManager.startQuery(1, roomAdapter, DBProvider.ROOMS_URI, roomProjection, selection, roomSelectionArgs,
						null);
				}
			case 2:
				if(selectedHouse!=null && selectedRoom!=null){
				String[] controllerProjection = { DBHandler.CONTROLLER_ID, DBHandler.CONTROLLER_INTERFACE_NAME,
						DBHandler.CONTROLLER_IMAGE_NAME, DBHandler.CONTROLLER_TYPE };
				selection = DBHandler.HOUSE_NAME + "=?" + " AND " + DBHandler.ROOM_NAME + "=?";
				String[] controllerSelectionArgs = { selectedHouse, selectedRoom };
				queryManager.startQuery(2, controllerAdapter, DBProvider.CONTROLLERS_URI, controllerProjection, selection,
						controllerSelectionArgs, null);
				}
				break;
			default:
		}
		
	}
	@Override
	public void replaceCursor(Cursor cursor,Object adapter) {
		Cursor temp = ((CursorAdapter)adapter).swapCursor(cursor);
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
	public ListAdapter getHousesAdapter(){
		return houseAdapter;
	}
	public AsyncQueryManager getQueryManager(){
		return queryManager;
	}
	public void setSelectedHouse(String houseName) {
		selectedHouse=houseName;
		
	}
	public void setSelectedRoomWithIp(String roomName,String roomIp) {
		selectedRoom=roomName;
		selectedRoomIp=roomIp;
		
	}
	

}
