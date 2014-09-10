package com.example.houseremote;

import static com.example.houseremote.MainActivity.HEADLESS;
import static com.example.houseremote.MainActivity.LOGGING;
import static com.example.houseremote.MainActivity.ROOMS;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.adapters.ListAdapter;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DataBaseQueryManager;
import com.example.houseremote.fragments.HeadlessFragment;
import com.example.houseremote.fragments.RoomsFragment;
import com.example.houseremote.interfaces.QueryManagerProvider;
import com.example.houseremote.interfaces.ReplyListener;
import com.example.houseremote.interfaces.RoomSelectionListener;
import com.example.houseremote.interfaces.RoomsAdapterProvider;
import com.example.houseremote.interfaces.SelectedHouseProvider;

public class RoomsActivity extends ActionBarActivity implements RoomSelectionListener, SelectedHouseProvider, RoomsAdapterProvider, QueryManagerProvider,ReplyListener{
	private static final String TAG = "com.example.houseremote.RoomsActivity";
	
	private HeadlessFragment myHeadlessFragment;

	private RoomsFragment myRoomsFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
	
		/*
		 * if the acitvity was created for the first time,
		 * create the headless fragment
		 * and the houses fragment
		 */
		if (savedInstanceState == null) {
			
			if(LOGGING){
				Log.v(TAG, "onCreateMainActivity created first time");
				Log.v(TAG, "creating headless fragment");
				Log.v(TAG, "creating houses fragment");
			}
			myHeadlessFragment = new HeadlessFragment();
			myHeadlessFragment.setSelectedHouse(getIntent().getStringExtra(DBHandler.HOUSE_NAME));
			myRoomsFragment = new RoomsFragment();
			if(LOGGING){
				Log.v(TAG, "adding house and headless fragments");
			}
			getSupportFragmentManager().beginTransaction().add(myHeadlessFragment, HEADLESS).add(R.id.list,myRoomsFragment,ROOMS).commit();
			
		} else {
			if(LOGGING){
				Log.v(TAG, "onCreateMainActivity restored");
			}
			/*
			 * if coming back to the activity from somewhere
			 * recover the headless fragment
			 */
			if(LOGGING){
				Log.v(TAG, "attempting to restore headless fragment");
			}
			myHeadlessFragment = (HeadlessFragment) getSupportFragmentManager().findFragmentByTag(HEADLESS);
			
			
			/*
			 * and if on phone try to recover the house fragment if not make a new one
			 */
			if(LOGGING){
				Log.v(TAG, "checking if on phone or tablet");
			}
			if (findViewById(R.id.expanded) == null) {
				if(LOGGING){
					Log.v(TAG, "on phone");
					Log.v(TAG, "attempting to recover house fragment");
				}
					
				myRoomsFragment = (RoomsFragment) getSupportFragmentManager().findFragmentByTag(ROOMS);
				if (myRoomsFragment == null) {
					if(LOGGING){
						Log.v(TAG, "room fragment recovery failed, creating new room fragment");
						Log.v(TAG, "adding room fragment");
					}
					myRoomsFragment = new RoomsFragment();
					getSupportFragmentManager().beginTransaction().add(R.id.expanded, myRoomsFragment, ROOMS).commit();
				}
				else {
					if(LOGGING){
						Log.v(TAG, "room fragment recovery succeded");
					}
				}
			} 
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			// TODO Open Settings?
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public void roomSelected(String roomName, String roomIp) {
		
		Intent i = new Intent(this, ControllersActivity.class);
		i.putExtra(DBHandler.HOUSE_NAME, myHeadlessFragment.getSelectedHouse());
		i.putExtra(DBHandler.ROOM_NAME, roomName);
		i.putExtra(DBHandler.CONTROLLER_IP, roomIp);
		startActivity(i);
		return;
	}

	@Override
	public String getSelectedHouse() {
		return myHeadlessFragment.getSelectedHouse();
	}

	@Override
	public ListAdapter getRoomsAdapter() {
		return myHeadlessFragment.getRoomsAdapter();
	}

	@Override
	public DataBaseQueryManager getQueryManager() {
		return myHeadlessFragment.getQueryManager();
	}

	@Override
	public void dataSetChanged(int token, Object cookie) {
		myHeadlessFragment.dataSetChanged(token, cookie);
		
	}

	@Override
	public void replaceCursor(Cursor cursor, Object o) {
		myHeadlessFragment.replaceCursor(cursor, o);
		
	}
}
