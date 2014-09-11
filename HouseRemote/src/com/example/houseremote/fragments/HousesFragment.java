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

import com.example.houseremote.EditHouseActivity;
import com.example.houseremote.R;
import com.example.houseremote.adapters.ListAdapter;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseQueryManager;
import com.example.houseremote.interfaces.HouseDatabaseChangeListener;
import com.example.houseremote.interfaces.HouseSelectionListener;
import com.example.houseremote.interfaces.HousesAdapterProvider;
import com.example.houseremote.interfaces.QueryManagerProvider;
import com.example.houseremote.interfaces.ReplyListener;
import com.example.houseremote.observers.HouseObserver;

/**
 * MAJOR TODOS 
 * TODO COMMUNICATE FAILIURE REACHING HOSTS
 * MINOR TODOS 
 * TODO REDESIGN DATABASE
 * TODO MAKE ACTIVITY ACTIONBARS A BIT MORE CUSTOM ADD UP BUTTON TO PHONE VERSION
 * TODO USE UI DESIGN TO HILIGHT SELECTED ELEMENTS
 * TODO CHECK RESOURCE USAGE, NAMELY CLOSE THE DAMN CURSORS IN ONSTOP ONPAUSE ETC. 
 * TODO ANIMATE THE FRAGMENT TRANSITIONS
 * TODO HAVE NEW HOUSE NAME AUTOINCREMENT
 */

public class HousesFragment extends Fragment implements HouseDatabaseChangeListener {
	
	private ListView mList;
	private ListAdapter mAdapter;
	private HouseSelectionListener mCallback;
	private DataBaseQueryManager asyncQ;
	private HouseObserver mObserver;

	public HousesFragment() {
	}
		

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
		mCallback=(HouseSelectionListener) getActivity();
		mAdapter=((HousesAdapterProvider) mCallback).getHousesAdapter();
		asyncQ=((QueryManagerProvider) mCallback).getQueryManager();
		mList = (ListView) getActivity().findViewById(R.id.houseList);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				mCallback.houseSelected(((Cursor) mAdapter.getItem(position)).getString(mAdapter.getCursor()
						.getColumnIndex(DBHandler.HOUSE_NAME)));
			}
		});
		registerForContextMenu(mList);
		loadInitialControllerData(mAdapter);
		super.onActivityCreated(savedInstanceState);
		
	}

	private void loadInitialControllerData(ListAdapter mAdapter2) {
		if(((HousesAdapterProvider)mCallback).isInitialHouseDataLoaded()) return;
		((HousesAdapterProvider)mCallback).setInitialHouseDataLoaded(true);
		((ReplyListener) mCallback).dataSetChanged(0,mAdapter);
		
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

		String selectedHouseName;

		if (item.getItemId() == R.id.action_edit_house) {
			selectedHouseName = ((Cursor) mAdapter.getItem(info.position)).getString(mAdapter.getCursor()
					.getColumnIndex(DBHandler.HOUSE_NAME));
			Intent i = new Intent(getActivity(), EditHouseActivity.class);
			i.putExtra(DBHandler.HOUSE_NAME, selectedHouseName);
			startActivityForResult(i,0);
		}
		
		if (item.getItemId() == R.id.action_delete_house) {
			selectedHouseName = ((Cursor) mAdapter.getItem(info.position)).getString(mAdapter.getCursor()
					.getColumnIndex(DBHandler.HOUSE_NAME));
			asyncQ.startDelete(0, null, DBProvider.HOUSES_URI, DBHandler.HOUSE_NAME + "=?",
					new String[] { selectedHouseName });
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
	    	((ReplyListener) mCallback).dataSetChanged(0,mAdapter);
	    	((ReplyListener) mCallback).dataSetChanged(1,mAdapter);
	        
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

			asyncQ.startInsert(0, null, DBProvider.HOUSES_URI, cv);

			Intent i = new Intent(getActivity(), EditHouseActivity.class);
			i.putExtra(DBHandler.HOUSE_NAME, getString(R.string.newHouseName));
			startActivityForResult(i,0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void houseDatabaseChanged() {
		((ReplyListener) mCallback).dataSetChanged(0,mAdapter);
		
	}


	@Override
	public void roomDatabaseChanged() {
		((ReplyListener) mCallback).dataSetChanged(1,mAdapter);
		
	}


	@Override
	public void controllerDatabaseChanged() {
		((ReplyListener) mCallback).dataSetChanged(2,mAdapter);
		
	}


}