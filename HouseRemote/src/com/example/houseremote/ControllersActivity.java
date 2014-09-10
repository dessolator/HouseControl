package com.example.houseremote;

import static com.example.houseremote.MainActivity.CONTROLLERS;
import static com.example.houseremote.MainActivity.HEADLESS;
import static com.example.houseremote.MainActivity.LOGGING;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.adapters.GridAdapter;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DataBaseQueryManager;
import com.example.houseremote.fragments.ControllersFragment;
import com.example.houseremote.fragments.HeadlessFragment;
import com.example.houseremote.interfaces.ControllersAdapterProvider;
import com.example.houseremote.interfaces.NetworkCommandListener;
import com.example.houseremote.interfaces.QueryManagerProvider;
import com.example.houseremote.interfaces.ReplyListener;
import com.example.houseremote.interfaces.SelectedHouseProvider;
import com.example.houseremote.interfaces.SelectedRoomProvider;
import com.example.houseremote.network.SwitchPacket;

public class ControllersActivity extends ActionBarActivity implements ReplyListener, SelectedHouseProvider, SelectedRoomProvider,ControllersAdapterProvider,QueryManagerProvider,NetworkCommandListener{
	private static final String TAG = "com.example.houseremote.ControllersActivity";
	private ControllersFragment myControllersFragment;
	private HeadlessFragment myHeadlessFragment;
	
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
//			myRoomsFragment = new RoomsFragment();
			myHeadlessFragment = new HeadlessFragment();
			myHeadlessFragment.setSelectedHouse(getIntent().getStringExtra(DBHandler.HOUSE_NAME));
			myHeadlessFragment.setSelectedRoomWithIp(getIntent().getStringExtra(DBHandler.ROOM_NAME),getIntent().getStringExtra(DBHandler.CONTROLLER_IP));
			myControllersFragment = new ControllersFragment();
			if(LOGGING){
				Log.v(TAG, "adding house and headless fragments");
			}
			getSupportFragmentManager().beginTransaction().add(myHeadlessFragment, HEADLESS).add(R.id.list,myControllersFragment,CONTROLLERS).commit();
			
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
					
				myControllersFragment = (ControllersFragment) getSupportFragmentManager().findFragmentByTag(CONTROLLERS);
				if (myControllersFragment == null) {
					if(LOGGING){
						Log.v(TAG, "room fragment recovery failed, creating new room fragment");
						Log.v(TAG, "adding room fragment");
					}
					myControllersFragment = new ControllersFragment();
					getSupportFragmentManager().beginTransaction().add(R.id.list, myControllersFragment, CONTROLLERS).commit();
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

	@Override
	public void dataSetChanged(int token, Object cookie) {
		myHeadlessFragment.dataSetChanged(token, cookie);
		
	}

	@Override
	public void replaceCursor(Cursor cursor, Object o) {
		myHeadlessFragment.replaceCursor(cursor, o);
		
	}

	@Override
	public String getSelectedHouse() {
		return myHeadlessFragment.getSelectedHouse();
	}

	@Override
	public String getSelectedRoom() {
		return myHeadlessFragment.getSelectedRoom();
	}

	@Override
	public String getSelectedRoomIp() {
		return myHeadlessFragment.getSelectedRoomIp();
	}

	@Override
	public GridAdapter getControllersAdapter() {
		return myHeadlessFragment.getControllersAdapter();
	}

	@Override
	public DataBaseQueryManager getQueryManager() {
		return myHeadlessFragment.getQueryManager();
	}

	@Override
	public void addToNetworkSender(SwitchPacket switchPacket) {
		myHeadlessFragment.addToNetworkSender(switchPacket);
	}
}
