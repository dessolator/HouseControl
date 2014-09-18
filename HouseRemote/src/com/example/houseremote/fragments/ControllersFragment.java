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
import android.widget.GridView;

import com.example.houseremote.R;
import com.example.houseremote.activities.EditLightSwitchActivity;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseQueryManager;
import com.example.houseremote.database.adapters.GridAdapter;
import com.example.houseremote.database.interfaces.ControllerDatabaseChangeListener;
import com.example.houseremote.database.interfaces.ControllersAdapterProvider;
import com.example.houseremote.database.interfaces.DBInsertResponder;
import com.example.houseremote.database.interfaces.QueryManagerProvider;
import com.example.houseremote.database.interfaces.ReplyListener;
import com.example.houseremote.database.observers.ControllerObserver;
import com.example.houseremote.interfaces.SelectedRoomProvider;
import com.example.houseremote.network.dataclasses.PinFlipPacket;
import com.example.houseremote.network.interfaces.NetworkCommandListener;

/**
 * A placeholder fragment containing a simple view.
 */

public class ControllersFragment extends Fragment implements ControllerDatabaseChangeListener,
		DBInsertResponder {

	private long roomID;
	private GridView mGrid;
	private GridAdapter mAdapter;
	private ReplyListener mCallback;
	private DataBaseQueryManager mAsyncQueryManager;
	private ControllerObserver mObserver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mObserver = new ControllerObserver(new Handler(), this);
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_controllers, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		mCallback = (ReplyListener) getActivity();
		roomID = ((SelectedRoomProvider) mCallback).getSelectedRoomID();
		mAdapter = ((ControllersAdapterProvider) mCallback).getControllersAdapter();
		mAsyncQueryManager = ((QueryManagerProvider) mCallback).getQueryManager();

		mGrid = (GridView) getActivity().findViewById(R.id.controllerGrid);
		mGrid.setAdapter(mAdapter);
		mGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				((NetworkCommandListener) mCallback).addToNetworkSender(
						((Cursor) mAdapter.getItem(position)).getString(mAdapter.getCursor().getColumnIndex(
								DBHandler.CONTROLLER_IP)),
						new PinFlipPacket(((Cursor) mAdapter.getItem(position)).getInt(mAdapter.getCursor()
								.getColumnIndex(DBHandler.CONTROL_PIN_NUMBER))));

			}
		});
		registerForContextMenu(mGrid);
		loadInitialControllerData(mAdapter);

		super.onActivityCreated(savedInstanceState);
	}

	private void loadInitialControllerData(GridAdapter mAdapter2) {
		if (((ControllersAdapterProvider) mCallback).isInitialControllerDataLoaded())
			return;
		((ControllersAdapterProvider) mCallback).setInitialControllerDataLoaded(true);
		((ReplyListener) mCallback).reloadControllerData();

	}

	@Override
	public void onStart() {
		getActivity().getContentResolver().registerContentObserver(DBProvider.CONTROLLERS_URI, true,
				mObserver);
		super.onStart();
	}

	@Override
	public void onStop() {
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
		super.onStop();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.controller_fragment_context_menu, menu);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 2) {
			((ReplyListener) mCallback).reloadControllerData();
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		long controllerID;

		if (item.getItemId() == R.id.action_edit_controller) {
			controllerID = ((Cursor) mAdapter.getItem(info.position)).getLong(mAdapter.getCursor()
					.getColumnIndex(DBHandler.CONTROLLER_ID));

			int selectedType = ((Cursor) mAdapter.getItem(info.position)).getInt(mAdapter.getCursor()
					.getColumnIndex(DBHandler.CONTROLLER_TYPE));

			Intent i = null;
			switch (selectedType) {
			case 0:
				i = new Intent(getActivity(), EditLightSwitchActivity.class);
				break;
			case 1:
//				i = new Intent(getActivity(), EditOutletSwitchActivity.class);
				break;
			case 2:
//				i = new Intent(getActivity(), EditSomeSwitchActivity.class);
				break;

			default:
				break;
			}

			i.putExtra(DBHandler.CONTROLLER_ID, controllerID);
			startActivityForResult(i, 2);// start the activity
			return true;
		}
		if (item.getItemId() == R.id.action_delete_controller) {
			controllerID = ((Cursor) mAdapter.getItem(info.position)).getLong(mAdapter.getCursor()
					.getColumnIndex(DBHandler.CONTROLLER_ID));
			String selection = DBHandler.CONTROLLER_ID + "=?";
			String[] selectionArgs = { controllerID + "" };
			mAsyncQueryManager.startDelete(0, null, DBProvider.CONTROLLERS_URI, selection, selectionArgs);
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
			cv.put(DBHandler.ROOM_ID_ALT, roomID);
			cv.put(DBHandler.CONTROLLER_NAME, "New LigthSwitch");
			cv.put(DBHandler.CONTROLLER_IP, "");
			cv.put(DBHandler.CONTROLLER_PORT, 55000);//TODO add to edit controller
			cv.put(DBHandler.CONTROLLER_IMAGE_NAME, "light");
			cv.put(DBHandler.CONTROLLER_TYPE, 0);
			cv.put(DBHandler.CONTROL_PIN_NUMBER, 0);
			mAsyncQueryManager.startInsert(0, this, DBProvider.CONTROLLERS_URI, cv);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void uponInsertFinished(long controllerID) {
		Intent i = new Intent(getActivity(), EditLightSwitchActivity.class);
		i.putExtra(DBHandler.CONTROLLER_ID, controllerID);// give info about the
		startActivityForResult(i, 2);// start the activity
	}

	public void replaceData(long roomID) {
		this.roomID = roomID;
	}

	@Override
	public void controllerDatabaseChanged() {
		((ReplyListener) mCallback).reloadControllerData();

	}

}