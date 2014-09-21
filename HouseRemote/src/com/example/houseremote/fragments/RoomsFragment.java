package com.example.houseremote.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.houseremote.R;
import com.example.houseremote.activities.EditRoomActivity;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseAsyncQueryHandler;
import com.example.houseremote.database.adapters.ListAdapter;
import com.example.houseremote.database.interfaces.DBInsertResponder;
import com.example.houseremote.database.interfaces.QueryManagerProvider;
import com.example.houseremote.database.interfaces.ReplyListener;
import com.example.houseremote.database.interfaces.RoomDatabaseChangeListener;
import com.example.houseremote.database.interfaces.RoomsAdapterProvider;
import com.example.houseremote.database.observers.RoomObserver;
import com.example.houseremote.interfaces.RoomSelectionListener;
import com.example.houseremote.interfaces.SelectedHouseProvider;

/**
 * A placeholder fragment containing a simple view.
 */

public class RoomsFragment extends Fragment implements RoomDatabaseChangeListener, DBInsertResponder {

	
	private long mHouseID;
	private ListView mList;
	private ListAdapter mAdapter;
	private RoomSelectionListener mCallback;
	private DataBaseAsyncQueryHandler mAsyncQueryManager;
	private RoomObserver mObserver;

	public RoomsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mObserver=new RoomObserver(new Handler(),this);
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_rooms, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		mCallback=(RoomSelectionListener) getActivity();
		mHouseID=((SelectedHouseProvider) mCallback).getSelectedHouseID();
		mAdapter=((RoomsAdapterProvider) mCallback).getRoomsAdapter();
		mAsyncQueryManager=((QueryManagerProvider) mCallback).getQueryManager();
		
		mList = (ListView) getActivity().findViewById(R.id.roomList);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				mCallback.roomSelected(((Cursor) mAdapter.getItem(position)).getLong(mAdapter.getCursor().getColumnIndex(DBHandler.ROOM_ID)));
			}
		});
		registerForContextMenu(mList);
		loadInitialControllerData(mAdapter);
		super.onActivityCreated(savedInstanceState);
	}
	
	/**
	 * Gets called when returning from another activity e.g. editing a room
	 * Refresh DataSet
	 */

	private void loadInitialControllerData(ListAdapter mAdapter2) {
		if(((RoomsAdapterProvider)mCallback).isInitialRoomDataLoaded()) return;
		((RoomsAdapterProvider)mCallback).setInitialRoomDataLoaded(true);
		((ReplyListener) mCallback).reloadRoomData();
		
	}
	@Override
	public void onStart() {
		getActivity().getContentResolver().registerContentObserver(DBProvider.ROOMS_URI, true, mObserver);
		super.onStart();
	}
	@Override
	public void onStop() {
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
		super.onStop();
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.room_fragment_context_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);

	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		long selectedRoomID;
		if (item.getItemId() == R.id.action_edit_room) {
			selectedRoomID = ((Cursor) mAdapter.getItem(info.position)).getLong(mAdapter.getCursor()
					.getColumnIndex(DBHandler.ROOM_ID));
			Intent i = new Intent(getActivity(), EditRoomActivity.class);
			i.putExtra(DBHandler.ROOM_ID, selectedRoomID);
			startActivityForResult(i,1);
			return true;
		}
		if (item.getItemId() == R.id.action_delete_room) {
			selectedRoomID = ((Cursor) mAdapter.getItem(info.position)).getLong(mAdapter.getCursor()
					.getColumnIndex(DBHandler.ROOM_ID));
			String selection = DBHandler.ROOM_ID + "=?";
			String[] selectionArgs = {selectedRoomID+""};
			mAsyncQueryManager.startDelete(0, null, DBProvider.ROOMS_URI, selection, selectionArgs);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.room_fragment_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == 1) {
	    	((ReplyListener) mCallback).reloadRoomData();
	    	((ReplyListener) mCallback).reloadControllerData();
	        
	    }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_add_a_room) {

			ContentValues cv = new ContentValues();
			cv.put(DBHandler.ROOM_NAME, getString(R.string.newRoomName));
			cv.put(DBHandler.HOUSE_ID_ALT, mHouseID);
			cv.put(DBHandler.ROOM_IMAGE_NAME, "bed");
			mAsyncQueryManager.startInsert(0, this, DBProvider.ROOMS_URI, cv);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void uponInsertFinished(long roomID) {
		Intent i = new Intent(getActivity(), EditRoomActivity.class);
		i.putExtra(DBHandler.ROOM_ID, roomID);
		startActivityForResult(i,1);
	}

	public void replaceData(long houseID){
		this.mHouseID=houseID;
	}

	@Override
	public void roomDatabaseChanged() {
		((ReplyListener) mCallback).reloadRoomData();
	}

	@Override
	public void controllerDatabaseChanged() {
		((ReplyListener) mCallback).reloadControllerData();
	}

}