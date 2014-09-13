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

import com.example.houseremote.EditLightSwitchActivity;
import com.example.houseremote.R;
import com.example.houseremote.adapters.GridAdapter;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseQueryManager;
import com.example.houseremote.interfaces.ControllerDatabaseChangeListener;
import com.example.houseremote.interfaces.ControllersAdapterProvider;
import com.example.houseremote.interfaces.NetworkCommandListener;
import com.example.houseremote.interfaces.QueryManagerProvider;
import com.example.houseremote.interfaces.ReplyListener;
import com.example.houseremote.interfaces.SelectedHouseProvider;
import com.example.houseremote.interfaces.SelectedRoomProvider;
import com.example.houseremote.network.PinFlipPacket;
import com.example.houseremote.observers.ControllerObserver;

/**
 * A placeholder fragment containing a simple view.
 */

public class ControllersFragment extends Fragment implements ControllerDatabaseChangeListener {

	private String houseName;
	private String roomName;
	private GridView mGrid;
	private String roomIp;
	private GridAdapter mAdapter;
	private ReplyListener mCallback;
	private DataBaseQueryManager mAsyncQueryManager;
	private ControllerObserver mObserver;


	public ControllersFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mObserver=new ControllerObserver(new Handler(),this);
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
		houseName = ((SelectedHouseProvider) mCallback).getSelectedHouse();
		roomName = ((SelectedRoomProvider) mCallback).getSelectedRoom();
		roomIp = ((SelectedRoomProvider) mCallback).getSelectedRoomIp();
		mAdapter = ((ControllersAdapterProvider) mCallback).getControllersAdapter();
		mAsyncQueryManager = ((QueryManagerProvider) mCallback).getQueryManager();

		mGrid = (GridView) getActivity().findViewById(R.id.controllerGrid);
		mGrid.setAdapter(mAdapter);
		mGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				((NetworkCommandListener) mCallback).addToNetworkSender(new PinFlipPacket(((Cursor) mAdapter
						.getItem(position)).getInt(mAdapter.getCursor().getColumnIndex(
						DBHandler.CONTROL_PIN1_NUMBER))));
			}
		});
		registerForContextMenu(mGrid);
		loadInitialControllerData(mAdapter);
		

		super.onActivityCreated(savedInstanceState);
	}
	
	private void loadInitialControllerData(GridAdapter mAdapter2) {
		if(((ControllersAdapterProvider)mCallback).isInitialControllerDataLoaded()) return;
		getActivity().findViewById(R.id.linlaHeaderProgress).setVisibility(View.VISIBLE);
		((ControllersAdapterProvider)mCallback).setInitialControllerDataLoaded(true);
		((ReplyListener) mCallback).dataSetChanged(2,mAdapter);
		
	}
	
	@Override
	public void onStart() {
		getActivity().getContentResolver().registerContentObserver(DBProvider.CONTROLLERS_URI, true, mObserver);
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
	    	((ReplyListener) mCallback).dataSetChanged(2,mAdapter);	        
	    }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		String controllerName;

		if (item.getItemId() == R.id.action_edit_controller) {
			controllerName = ((Cursor) mAdapter.getItem(info.position)).getString(mAdapter.getCursor()
					.getColumnIndex(DBHandler.CONTROLLER_INTERFACE_NAME));

			String selectedType = ((Cursor) mAdapter.getItem(info.position)).getString(mAdapter.getCursor()
					.getColumnIndex(DBHandler.CONTROLLER_TYPE));
			// TODO
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

			i.putExtra(DBHandler.ROOM_NAME, roomName);// give info about the
														// house
			i.putExtra(DBHandler.HOUSE_NAME, houseName);
			i.putExtra(DBHandler.CONTROLLER_INTERFACE_NAME, controllerName);
			startActivityForResult(i,2);// start the activity
			return true;
		}
		if (item.getItemId() == R.id.action_delete_controller) {
			controllerName = ((Cursor) mAdapter.getItem(info.position)).getString(mAdapter.getCursor()
					.getColumnIndex(DBHandler.CONTROLLER_INTERFACE_NAME));
			String selection = DBHandler.HOUSE_NAME + "=?" + " AND " + DBHandler.ROOM_NAME + "=?" + " AND "
					+ DBHandler.CONTROLLER_INTERFACE_NAME + "=?";
			String[] selectionArgs = { houseName, roomName, controllerName };
			mAsyncQueryManager.startDelete(0, null, DBProvider.CONTROLLERS_URI, selection, selectionArgs);//TODO might need token
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
			cv.put(DBHandler.CONTROLLER_IMAGE_NAME, "light");
			cv.put(DBHandler.CONTROLLER_TYPE, "lightSwitch");// TODO not really
			cv.put(DBHandler.CONTROL_PIN1_NUMBER, 0);
			mAsyncQueryManager.startInsert(0, null, DBProvider.CONTROLLERS_URI, cv);
			Intent i = new Intent(getActivity(), EditLightSwitchActivity.class);// TODO
																				// switch
																				// based
																				// on
																				// switchtype
			i.putExtra(DBHandler.ROOM_NAME, roomName);// give info about the
														// house
			i.putExtra(DBHandler.HOUSE_NAME, houseName);
			i.putExtra(DBHandler.CONTROLLER_INTERFACE_NAME, "New LigthSwitch");
			startActivityForResult(i,2);// start the activity
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void replaceData(String houseName, String roomName, String roomIp) {
		this.houseName = houseName;
		this.roomName = roomName;
		this.roomIp = roomIp;
	}

	@Override
	public void controllerDatabaseChanged() {
		((ReplyListener) mCallback).dataSetChanged(2,mAdapter);
		
	}

}