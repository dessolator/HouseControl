package com.example.houseremote.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.widget.TextView;

import com.example.houseremote.EditRoomActivity;
import com.example.houseremote.R;
import com.example.houseremote.adapters.RoomListAdapter;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;

/**
 * A placeholder fragment containing a simple view.
 */

public class RoomsFragment extends Fragment implements LoaderCallbacks<Cursor> {

	private String mHouseName;
	private ListView mList;
	private RoomListAdapter mAdapter;
	private RoomSelectionListener mCallback;

	public RoomsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.mHouseName = getArguments().getString("house_name");
		mAdapter=new RoomListAdapter(getActivity(), null, 0);
		getLoaderManager().initLoader(0, null, this);
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
 
		return inflater.inflate(R.layout.fragment_rooms, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		mCallback = (RoomSelectionListener) getActivity();
		mList = (ListView) getActivity().findViewById(R.id.roomList);
		mList.setAdapter(mAdapter);

		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				mCallback.roomSelected((((TextView) (v
						.findViewById(R.id.gridItemText))).getText().toString()));
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
		getLoaderManager().restartLoader(0, null, this);

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
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		if (item.getItemId() == R.id.action_edit_room) {
			Intent i = new Intent(getActivity(), EditRoomActivity.class);
			i.putExtra("roomName",
					((Cursor)((ListView)getActivity().findViewById(R.id.roomList)).getAdapter().getItem(info.position)).getString(1)
					);
			i.putExtra("houseName", mHouseName);
			startActivity(i);	   		
	   		return true;
		}
		if (item.getItemId() == R.id.action_delete_room) {
			SQLiteDatabase db = new DBHandler(getActivity()).getWritableDatabase();
	   		db.execSQL("DELETE FROM room WHERE room_name='"
	   				+((Cursor)((ListView)getActivity().findViewById(R.id.roomList)).getAdapter().getItem(info.position)).getString(1)
	   				+"' AND house_name='"+mHouseName+"'");
	   		db.close();
			getLoaderManager().restartLoader(0, null, this);
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
			SQLiteDatabase db = new DBHandler(getActivity()).getWritableDatabase();
			db.execSQL("INSERT INTO room(room_name,controler_ip,house_name,room_image_name) "
					+ "VALUES('"
					+ getString(R.string.newRoomName)
					+ "','','"
					+ mHouseName
					+ "','bed')");
			Intent i = new Intent(getActivity(),
					EditRoomActivity.class);
			i.putExtra("roomName", getString(R.string.newRoomName));
			i.putExtra("houseName", mHouseName);
			startActivity(i);
			db.close();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public interface RoomSelectionListener {
		void roomSelected(String roomName);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { DBHandler.ROOM_ID, DBHandler.ROOM_NAME,
				DBHandler.ROOM_IMAGE_NAME};
		String selection = DBHandler.HOUSE_NAME+"=?";
		String[] selectionArgs={mHouseName};
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				DBProvider.ROOMS_URI, projection, selection, selectionArgs, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
		
	}

}