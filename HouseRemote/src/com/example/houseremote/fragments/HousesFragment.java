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
import com.example.houseremote.activities.EditHouseActivity;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseAsyncQueryHandler;
import com.example.houseremote.database.adapters.ListAdapter;
import com.example.houseremote.database.interfaces.HouseDatabaseChangeListener;
import com.example.houseremote.database.observers.HouseObserver;
import com.example.houseremote.fragments.interfaces.MainActivityHeadlessProvider;

/**
 * MAJOR TODOS 
 * TODO SETUP SERVICE TO LISTEN TO WIDGET CLICKS
 * TODO SETUP ACTIVITY FOR CONTROLLERS (SHOW AVAILABLE WIFIs -> AVAILABLE CONTROLLERS -> AVAILABLE SWITCHES) (Avoid manual adition of nodes)
 * TODO ADD WIDGET SETUP ACTIVITY
 * TODO WHAT IF CONNECTING TO ALL SERVERS FAILS???? keep the spinny thing looping?
 * TODO WHEN LEAVING HOUSE FRAGMENT REMOVE THE SPINNY THING
 * MINOR TODOS 
 * TODO WHAT IF SERVER CLOSES CONNECTION?
 * TODO FOR SOME REASON IT TAKES ABOUT 5s TO UPDATE THE STATE IMAGE (PROBLEM FOUND SERVER DOESN'T CLOSE THREADS AND BOGGS DOWN)
 * TODO LOOK INTO NETWORK DATATYPES... THEY'RE... UGLY...
 * TODO SWITCH SERVER-CLIENT COMMUNICATION FROM STRINGS TO MASK-MANAGED INTS
 * TODO WIDGET
 * TODO ADD MULTIPIN TO DATABASE
 * TODO USE UI DESIGN TO HIGHLIGHT SELECTED ELEMENTS
 * TODO CHECK RESOURCE USAGE, NAMELY CLOSE THE DAMN CURSORS IN ONSTOP ONPAUSE ETC. (MAT analysis)
 * TODO ANIMATE THE FRAGMENT TRANSITIONS
 * TODO ADD AND RESCALE IMAGES
 * TODO ADD ABILITY TO SELECT IMAGES FOR ROOMS/HOUSES/CONTROLLERS
 * TODO WHAT IF ATTEMPTING TO SAVE INVALID STATE???
 */

public class HousesFragment extends Fragment implements HouseDatabaseChangeListener {
	
	private ListView mList;
	private ListAdapter mAdapter;
	private MainActivityHeadlessProvider mCallback;
	private DataBaseAsyncQueryHandler asyncQ;
	private HouseObserver mObserver;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		mObserver=new HouseObserver(new Handler(),this);
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
		

	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_houses, container, false);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		mCallback=(MainActivityHeadlessProvider) getActivity();
		mAdapter=(mCallback.getMainHeadlessFragment()).getHousesAdapter();
		asyncQ=mCallback.getMainHeadlessFragment().getQueryManager();
		mList = (ListView) getActivity().findViewById(R.id.houseList);
		
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				( mCallback).houseSelected(((Cursor) mAdapter.getItem(position)).getInt(mAdapter.getCursor()
						.getColumnIndex(DBHandler.HOUSE_ID)));
			}
		});
		registerForContextMenu(mList);
		loadInitialControllerData(mAdapter);
		super.onActivityCreated(savedInstanceState);
		
	}

	private void loadInitialControllerData(ListAdapter mAdapter2) {
		if((mCallback.getMainHeadlessFragment()).isInitialHouseDataLoaded()) return;
		( mCallback.getMainHeadlessFragment()).setInitialHouseDataLoaded(true);
		mCallback.getMainHeadlessFragment().onHouseDataChanged();
		
	}


	@Override
	public void onStart() {
		getActivity().getContentResolver().registerContentObserver(DBProvider.HOUSES_URI, true, mObserver);
		super.onStart();
	}
	@Override
	public void onStop() {
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
		super.onStop();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.house_fragment_context_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		int selectedHouseID;

		if (item.getItemId() == R.id.action_edit_house) {
			selectedHouseID = ((Cursor) mAdapter.getItem(info.position)).getInt(mAdapter.getCursor()
					.getColumnIndex(DBHandler.HOUSE_ID));
			Intent i = new Intent(getActivity(), EditHouseActivity.class);
			i.putExtra(DBHandler.HOUSE_ID, selectedHouseID);
			startActivityForResult(i,0);
		}
		
		if (item.getItemId() == R.id.action_delete_house) {
			selectedHouseID = ((Cursor) mAdapter.getItem(info.position)).getInt(mAdapter.getCursor()
					.getColumnIndex(DBHandler.HOUSE_ID));
			asyncQ.startDelete(0, null, DBProvider.HOUSES_URI, DBHandler.HOUSE_ID + "=?",
					new String[] { ""+selectedHouseID });
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.house_fragment_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == 0) {
	    	mCallback.getMainHeadlessFragment().onHouseDataChanged();
	    	mCallback.getMainHeadlessFragment().onRoomDataChanged();
	        
	    }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_add_a_house) {

			ContentValues cv = new ContentValues();
			cv.put(DBHandler.HOUSE_NAME, getString(R.string.newHouseName));
			cv.put(DBHandler.HOUSE_WIFI_NAME, "");
			cv.put(DBHandler.HOUSE_WIFI_TYPE, "");
			cv.put(DBHandler.HOUSE_WIFI_PASS, "");
			cv.put(DBHandler.HOUSE_IMAGE_NAME, "house");

			asyncQ.startInsert(0, this, DBProvider.HOUSES_URI, cv);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void houseDatabaseChanged() {
		mCallback.getMainHeadlessFragment().onHouseDataChanged();
		
	}


	@Override
	public void roomDatabaseChanged() {
		mCallback.getMainHeadlessFragment().onRoomDataChanged();
		
	}


	@Override
	public void controllerDatabaseChanged() {
		mCallback.getMainHeadlessFragment().onControllerDataChanged();
		
	}


}