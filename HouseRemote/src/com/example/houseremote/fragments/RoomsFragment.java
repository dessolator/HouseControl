package com.example.houseremote.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import com.example.houseremote.EditRoomActivity;
import com.example.houseremote.R;
import com.example.houseremote.adapters.ListAdapter;
import com.example.houseremote.database.AsyncQueryManager;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.AsyncQueryManager.ReplyListener;

/**
 * A placeholder fragment containing a simple view.
 */

public class RoomsFragment extends Fragment implements ReplyListener {

	private String mHouseName;
	private ListView mList;
	private ListAdapter mAdapter;
	private RoomSelectionListener mCallback;
	private AsyncQueryManager mAsyncQueryManager;

	public RoomsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.mHouseName = getArguments().getString("house_name");
		mAdapter = new ListAdapter(getActivity(), null, 0);
		mAsyncQueryManager = new AsyncQueryManager(getActivity()
				.getContentResolver(), this);
		mCallback = (RoomSelectionListener) getActivity();
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_rooms, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		mList = (ListView) getActivity().findViewById(R.id.roomList);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				mCallback.roomSelected(((Cursor) mAdapter.getItem(position))
						.getString(1),((Cursor) mAdapter.getItem(position))
						.getString(3));
			}
		});
		registerForContextMenu(mList);
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * Gets called when returning from another activity e.g. editing a room
	 * Refresh DataSet
	 */
	@Override
	public void onStart() {
		super.onStart();
		String[] projection = { DBHandler.ROOM_ID, DBHandler.ROOM_NAME,
				DBHandler.ROOM_IMAGE_NAME,DBHandler.CONTROLLER_IP };
		String selection = DBHandler.HOUSE_NAME + "=?";
		String[] selectionArgs = { mHouseName };
		mAsyncQueryManager.startQuery(0, null, DBProvider.ROOMS_URI,
				projection, selection, selectionArgs, null);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(
				R.menu.room_fragment_context_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		String selectedRoomName = ((Cursor) mAdapter.getItem(info.position))
				.getString(mAdapter.getCursor().getColumnIndex(DBHandler.ROOM_NAME));//TODO HARDCODED
		if (item.getItemId() == R.id.action_edit_room) {
			Intent i = new Intent(getActivity(), EditRoomActivity.class);
			i.putExtra("roomName", selectedRoomName);
			i.putExtra("houseName", mHouseName);
			startActivity(i);
			return true;
		}
		if (item.getItemId() == R.id.action_delete_room) {
			String selection = DBHandler.HOUSE_NAME + "=?"+" AND "
					+ DBHandler.ROOM_NAME + "=?";
			String[] selectionArgs = { mHouseName, selectedRoomName };
			mAsyncQueryManager.startDelete(0, null, DBProvider.ROOMS_URI,
					selection, selectionArgs);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_add_a_room) {

			ContentValues cv = new ContentValues();
			cv.put(DBHandler.ROOM_NAME, getString(R.string.newRoomName));
			cv.put(DBHandler.CONTROLLER_IP, "");
			cv.put(DBHandler.HOUSE_NAME, mHouseName);
			cv.put(DBHandler.ROOM_IMAGE_NAME, "bed");
			mAsyncQueryManager.startInsert(0, null, DBProvider.ROOMS_URI, cv);

			Intent i = new Intent(getActivity(), EditRoomActivity.class);
			i.putExtra("roomName", getString(R.string.newRoomName));
			i.putExtra("houseName", mHouseName);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public interface RoomSelectionListener {
		void roomSelected(String roomName, String roomIp);
	}

	@Override
	public void dataSetChanged() {
		String[] projection = { DBHandler.ROOM_ID, DBHandler.ROOM_NAME,
				DBHandler.ROOM_IMAGE_NAME };
		String selection = DBHandler.HOUSE_NAME + "=?";
		String[] selectionArgs = { mHouseName };
		mAsyncQueryManager.startQuery(0, null, DBProvider.ROOMS_URI,
				projection, selection, selectionArgs, null);

	}

	@Override
	public void replaceCursor(Cursor cursor) {
		Cursor temp=mAdapter.swapCursor(cursor);
		if(temp!=null)
			temp.close();

	}

}