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

import com.example.houseremote.EditHouseActivity;
import com.example.houseremote.R;
import com.example.houseremote.adapters.ListAdapter;
import com.example.houseremote.database.AsyncQueryManager;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.AsyncQueryManager.ReplyListener;

/**
 * MAJOR TODOS 
 * TODO IMPLEMENT ADD A CONTROLLER?!?!?! 
 * TODO IMPLEMENT CONTROLLER LOGIC?!?!?! 
 * TODO DEAR LORD MAKE THIS PHONE FRIENDLY!!! 
 * TODO WHAT IF CURRENT HOUSE IS DELETED, RESET THE ROOM FRAGMENT AND CALLBACK WITH NULL 
 * TODO WHAT IF CURRENT HOUSE IS EDITED, RESET ROOM FRAGMENT AND CALLBACK WITH NULL 
 * TODO NAVIGATION MANAGEMENT 
 * MINOR TODOS
 * TODO CHECK RESOURCE USAGE, NAMELY CLOSE THE DAMN CURSORS IN ONSTOP ONPAUSE ETC.
 * TODO ANIMATE THE FRAGMENT TRANSITIONS 
 * TODO HAVE THE DATASET AUTOMATICALLY NOTIFIED VIA OBSERVERS/BROADCAST RECEIVERS?
 * TODO HAVE NEW HOUSE NAME AUTOINCREMENT
 */

public class HousesFragment extends Fragment implements ReplyListener {

	private ListView mList;
	private ListAdapter mAdapter;
	private HouseSelectionListener mCallback;
	private AsyncQueryManager asyncQ;

	public HousesFragment() {
	}

	/**
	 * Initialize the adapter Initialize the background loader
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mAdapter = new ListAdapter(getActivity(), null, 0);
		asyncQ = new AsyncQueryManager(getActivity().getContentResolver(), this);
		mCallback = (HouseSelectionListener) getActivity();// register the
															// activity for
															// inter-fragment
															// communication
		setHasOptionsMenu(true);// register for options menu callbacks
		super.onCreate(savedInstanceState);

	}

	/**
	 * Inflate the layout
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_houses, container, false);
	}

	/**
	 * The ViewHierarchy is now available Register callback activity Bind
	 * ListView Attach a Listener to ListView
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		/*
		 * ListView Setup, attach adapter, set onclick listener and register for
		 * context menu
		 */
		mList = (ListView) getActivity().findViewById(R.id.houseList);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				mCallback.houseSelected(((Cursor) mAdapter.getItem(position))
						.getString(1));
			}
		});
		registerForContextMenu(mList);

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		String[] projection = { DBHandler.HOUSE_ID, DBHandler.HOUSE_NAME,
				DBHandler.HOUSE_IMAGE_NAME };
		asyncQ.startQuery(0, null, DBProvider.HOUSES_URI, projection, null,
				null, null);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(
				R.menu.house_fragment_context_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		String selectedHouseName = ((Cursor) mAdapter.getItem(info.position))
				.getString(1);

		if (item.getItemId() == R.id.action_edit_house) {
			Intent i = new Intent(getActivity(), EditHouseActivity.class);
			i.putExtra("houseName", selectedHouseName);
			startActivity(i);
		}
		if (item.getItemId() == R.id.action_delete_house) {
			asyncQ.startDelete(0, null, DBProvider.HOUSES_URI, DBHandler.HOUSE_NAME+"=?",
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
			i.putExtra("houseName", getString(R.string.newHouseName));
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public interface HouseSelectionListener {
		void houseSelected(String houseName);
	}

	@Override
	public void dataSetChanged() {
		String[] projection = { DBHandler.HOUSE_ID, DBHandler.HOUSE_NAME,
				DBHandler.HOUSE_IMAGE_NAME };
		asyncQ.startQuery(0, null, DBProvider.HOUSES_URI, projection, null,
				null, null);// if data changed requery the database

	}

	@Override
	public void replaceCursor(Cursor cursor) {
		Cursor temp=mAdapter.swapCursor(cursor);
		if(temp!=null)
			temp.close();

	}

}