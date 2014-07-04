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
import android.widget.GridView;

import com.example.houseremote.EditLightSwitchActivity;
import com.example.houseremote.R;
import com.example.houseremote.adapters.GridAdapter;
import com.example.houseremote.database.AsyncQueryManager;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.AsyncQueryManager.ReplyListener;

/**
 * A placeholder fragment containing a simple view.
 */

public class ControllersFragment extends Fragment implements ReplyListener {


	private String houseName;
	private String roomName;
	private GridView mGrid;
	private String roomIp;
	private GridAdapter mAdapter;
	AsyncQueryManager mAsyncQueryManager;


	public ControllersFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.houseName = getArguments().getString("house_name");
		this.roomName = getArguments().getString("room_name");

		mAdapter = new GridAdapter(getActivity(), null, 0);
		mAsyncQueryManager = new AsyncQueryManager(getActivity()
				.getContentResolver(), this);

		String[] projection = { DBHandler.CONTROLLER_IP };
		String selection = DBHandler.HOUSE_NAME + "=?" + " AND "
				+ DBHandler.ROOM_NAME + "=?";
		String[] selectionArgs = { houseName, roomName };
		mAsyncQueryManager.startQuery(1, null, DBProvider.ROOMS_URI,
				projection, selection, selectionArgs, null);

		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_controllers,
				container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		mGrid = (GridView) getActivity().findViewById(R.id.controllerGrid);
		mGrid.setAdapter(mAdapter);

		mGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO ACTUAL LOGIC
			}
		});
		registerForContextMenu(mGrid);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		dataSetChanged();

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(
				R.menu.controller_fragment_context_menu, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();// yank the context menu's info

		String controllerName = ((Cursor) mAdapter.getItem(info.position))
				.getString(mAdapter.getCursor().getColumnIndex(
						DBHandler.CONTROLLER_INTERFACE_NAME));

		if (item.getItemId() == R.id.action_edit_controller) {

			String selectedType = ((Cursor) mAdapter.getItem(info.position))// TODO
																			// change
																			// switchType
																			// from
																			// string
																			// to
																			// int
																			// to
																			// allow
																			// a
																			// switch
																			// case
																			// below
					.getString(mAdapter.getCursor().getColumnIndex(
							DBHandler.CONTROLLER_TYPE));
			Intent i = null;

			if (selectedType.equals("lightSwitch")) {// TODO change to switch
														// case
				i = new Intent(getActivity(), EditLightSwitchActivity.class);
			} else if (selectedType.equals("outletSwitch")) {
				// Intent i = new Intent(getApplicationContext(),
				// EditOutletSwitchActivity.class);
			} else if (selectedType.equals("someSwitch")) {
				// Intent i = new Intent(getApplicationContext(),
				// EditSomeSwitchActivity.class);
			}

			i.putExtra("roomName", roomName);// give info about the house
			i.putExtra("houseName", houseName);
			i.putExtra("lightSwitchName", controllerName);
			startActivity(i);// start the activity
			return true;
		}
		if (item.getItemId() == R.id.action_delete_controller) {
			String selection = DBHandler.HOUSE_NAME + "=?" + " AND "
					+ DBHandler.ROOM_NAME + "=?" + " AND "
					+ DBHandler.CONTROLLER_INTERFACE_NAME + "=?";
			String[] selectionArgs = { houseName, roomName, controllerName };
			mAsyncQueryManager.startDelete(0, null, DBProvider.CONTROLLERS_URI,
					selection, selectionArgs);
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.controller_fragment_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_add_a_controller) {
			// TODO present a dialog for which type of controller to make
			ContentValues cv = new ContentValues();
			cv.put(DBHandler.HOUSE_NAME, houseName);
			cv.put(DBHandler.ROOM_NAME, roomName);
			cv.put(DBHandler.CONTROLLER_INTERFACE_NAME, "New LigthSwitch");// TODO
																			// change
																			// with
																			// type
			cv.put(DBHandler.CONTROLLER_IP, roomIp);
			cv.put(DBHandler.CONTROLLER_IMAGE_NAME, "bed");// TODO not really
			cv.put(DBHandler.CONTROLLER_TYPE, "lightSwitch");// TODO not really
			cv.put(DBHandler.CONTROL_PIN1_NUMBER, 0);
			mAsyncQueryManager.startInsert(0, null, DBProvider.CONTROLLERS_URI,
					cv);
			Intent i = new Intent(getActivity(), EditLightSwitchActivity.class);// TODO
																				// switch
																				// based
																				// on
																				// switchtype
			i.putExtra("roomName", roomName);// give info about the house
			i.putExtra("houseName", houseName);
			i.putExtra("lightSwitchName", "New LigthSwitch");
			startActivity(i);// start the activity
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void dataSetChanged() {
		String[] projection = { DBHandler.CONTROLLER_ID,
				DBHandler.CONTROLLER_INTERFACE_NAME,
				DBHandler.CONTROLLER_IMAGE_NAME, DBHandler.CONTROLLER_TYPE };
		String selection = DBHandler.HOUSE_NAME + "=?" + " AND "
				+ DBHandler.ROOM_NAME + "=?";
		String[] selectionArgs = { houseName, roomName };
		mAsyncQueryManager.startQuery(0, null, DBProvider.CONTROLLERS_URI,
				projection, selection, selectionArgs, null);

	}

	@Override
	public void replaceCursor(Cursor cursor, int token) {
		switch (token) {
		case 0:
			mAdapter.swapCursor(cursor);// TODO close the old one
			break;
		case 1:
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					roomIp = cursor.getString(cursor
							.getColumnIndex(DBHandler.CONTROLLER_IP));// TODO
																		// prolly
																		// better
																		// if
																		// passed
																		// through
																		// intent
				}
			}
			break;
		}

	}

}