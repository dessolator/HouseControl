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
import com.example.houseremote.EditHouseActivity;
import com.example.houseremote.R;
import com.example.houseremote.adapters.HouseListAdapter;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;

/**
 *MAJOR TODOS
 *	TODO FINISH SWITCH TO CONTENT PROVDERS?!?!?!
 *	TODO IMPLEMENT ADD A ROOM?!?!?!
 *	TODO IMPLEMENT CONTROLLER LOGIC?!?!?!
 *	TODO WHAT IF  CURRENT HOUSE IS DELETED, RESET THE ROOM FRAGMENT AND CALLBACK WITH NULL
 *	TODO WHAT IF CURRENT HOUSE IS EDITED, RESET ROOM FRAGMENT AND CALLBACK WITH NULL
 *	TODO MOVE THE INSERT AND DELETE OPERATIONS TO ASYNCTASK SOMEHOW
 *	TODO NAVIGATION MANAGEMENT
 *MINOR TODOS
 *	TODO ANIMATE THE FRAGMENT TRANSITIONS
 *	TODO HAVE THE DATASET AUTOMATICALLY NOTIFIED VIA OBSERVERS/BROADCAST RECEIVERS?
 *	TODO HAVE NEW HOUSE NAME AUTOINCREMENT
 */

public class HousesFragment extends Fragment implements LoaderCallbacks<Cursor> {

	private ListView mList;
	private HouseListAdapter mAdapter;
	private HouseSelectionListener mCallback;

	public HousesFragment() {
	}

	/**
	 * Initialize the adapter
	 * Start the background loading into the adapter
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mAdapter=new HouseListAdapter(getActivity(), null, 0);
		getLoaderManager().initLoader(0, null, this);
		setHasOptionsMenu(true);
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
	 * The ViewHierarchy is now available
	 * Register callback activity
	 * Bind ListView
	 * Attach a Listener to ListView
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		mCallback = (HouseSelectionListener) getActivity();
		mList = (ListView) getActivity().findViewById(R.id.houseList);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				mCallback.houseSelected((((TextView) (v
						.findViewById(R.id.gridItemText))).getText().toString()));
			}
		});
		registerForContextMenu(mList);
		super.onActivityCreated(savedInstanceState);
	}
	/**
	 * Gets called when returning from another activity e.g. editing a house
	 * Refresh DataSet
	 */
	@Override
	public void onStart() {
		super.onStart();
		getLoaderManager().restartLoader(0, null, this);

	}
	/**
	 * Inflate the context menu
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(
				R.menu.house_fragment_context_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);

	}
	/**
	 * If edit was selected call the edit activity
	 * If delete was selected delete the row and refresh the data
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		String selectedHouseName = ((Cursor)((ListView) getActivity()
				.findViewById(R.id.houseList)).getAdapter().getItem(info.position)).getString(1);
		
		if (item.getItemId() == R.id.action_edit_house) {
			Intent i = new Intent(getActivity(), EditHouseActivity.class);
			i.putExtra("houseName", selectedHouseName);
			startActivity(i);
		}
		if (item.getItemId() == R.id.action_delete_house) {
			SQLiteDatabase db = new DBHandler(getActivity())
					.getWritableDatabase();
			db.execSQL("DELETE FROM house WHERE house_name='"
					+ selectedHouseName + "'");
			db.close();
			getLoaderManager().restartLoader(0, null, this);
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * Inflate the options menu
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.house_fragment_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	/**
	 * If "Add a house" was selected add a row to the table
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_add_a_house) {
			SQLiteDatabase db = new DBHandler(getActivity())
					.getWritableDatabase();
			db.execSQL("INSERT INTO house(house_name,house_wifi_name,house_wifi_type,house_wifi_pass,house_image_name) "
					+ "VALUES('"
					+ getString(R.string.newHouseName)
					+ "','kesler','WPA','12345678','house')");
			db.close();
			
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
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { DBHandler.HOUSE_ID, DBHandler.HOUSE_NAME,
				DBHandler.HOUSE_IMAGE_NAME};
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				DBProvider.HOUSES_URI, projection, null, null, null);
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