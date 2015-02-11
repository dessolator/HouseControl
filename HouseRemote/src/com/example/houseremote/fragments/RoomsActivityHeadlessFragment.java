package com.example.houseremote.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;

import com.example.houseremote.activities.EditRoomActivity;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseAsyncQueryHandler;
import com.example.houseremote.database.adapters.ListAdapter;
import com.example.houseremote.fragments.interfaces.RoomsActivityHeadlessFragmentInterface;

public class RoomsActivityHeadlessFragment extends Fragment implements RoomsActivityHeadlessFragmentInterface{

	private static final String roomSelection = DBHandler.HOUSE_ID_ALT + "=?";
	private static final String[] roomProjection = { DBHandler.ROOM_ID, DBHandler.ROOM_NAME,
		DBHandler.ROOM_IMAGE_NAME };
	private ListAdapter roomAdapter;
	private long selectedHouseID;
	private boolean initialRoomDataLoaded = false;
	private DataBaseAsyncQueryHandler queryManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		roomAdapter = new ListAdapter(getActivity(), null, 0);
		queryManager = new DataBaseAsyncQueryHandler(getActivity().getContentResolver(), this);
		setRetainInstance(true);

	}
	
	public void setSelectedHouseID(long houseID) {
		selectedHouseID = houseID;

	}
	@Override
	public void onQueryFinished(Cursor cursor, CursorAdapter cookie) {
		Cursor temp = cookie.swapCursor(cursor);
		if (temp != null)
			temp.close();		
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
	public void onInsertFinished(long parseId, int token) {
		Intent i = new Intent(getActivity(), EditRoomActivity.class);
		i.putExtra(DBHandler.ROOM_ID, parseId);
		startActivityForResult(i,1);
		
	}
	
	@Override
	public boolean isInitialRoomDataLoaded() {
		return initialRoomDataLoaded;
	}

	@Override
	public void setInitialRoomDataLoaded(boolean b) {
		this.initialRoomDataLoaded = b;

	}

	@Override
	public ListAdapter getRoomsAdapter() {
		return roomAdapter;
	}

	@Override
	public long getSelectedHouseID() {
		return selectedHouseID;
	}

	@Override
	public DataBaseAsyncQueryHandler getQueryManager() {
		return queryManager;
	}



}
