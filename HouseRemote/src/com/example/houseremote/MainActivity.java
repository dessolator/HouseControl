package com.example.houseremote;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.adapters.GridAdapter;
import com.example.houseremote.adapters.ListAdapter;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DataBaseQueryManager;
import com.example.houseremote.fragments.ControllersFragment;
import com.example.houseremote.fragments.HeadlessFragment;
import com.example.houseremote.fragments.HousesFragment;
import com.example.houseremote.fragments.RoomsFragment;
import com.example.houseremote.interfaces.ControllersAdapterProvider;
import com.example.houseremote.interfaces.HouseSelectionListener;
import com.example.houseremote.interfaces.HousesAdapterProvider;
import com.example.houseremote.interfaces.NetworkCommandListener;
import com.example.houseremote.interfaces.QueryManagerProvider;
import com.example.houseremote.interfaces.ReplyListener;
import com.example.houseremote.interfaces.RoomSelectionListener;
import com.example.houseremote.interfaces.RoomsAdapterProvider;
import com.example.houseremote.interfaces.SelectedHouseProvider;
import com.example.houseremote.interfaces.SelectedRoomProvider;
import com.example.houseremote.network.SwitchPacket;

public class MainActivity extends ActionBarActivity implements ReplyListener, RoomSelectionListener,
		HouseSelectionListener, ControllersAdapterProvider, RoomsAdapterProvider, HousesAdapterProvider,
		QueryManagerProvider, SelectedHouseProvider, SelectedRoomProvider, NetworkCommandListener{

	/*
	 * Fragment identification strings
	 */
	public static final String HEADLESS = "headless";
	private static final String HOUSES = "houses";
	public static final String ROOMS = "rooms";
	public static final String CONTROLLERS = "controllers";
	
	
	private static final String TAG = "com.example.houseremote.MainActivity";
	public static final boolean LOGGING = true;
	
	
	private HeadlessFragment myHeadlessFragment;
	private HousesFragment myHousesFragment;
	private RoomsFragment myRoomsFragment;
	private ControllersFragment myControllersFragment;

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
			myHousesFragment = new HousesFragment();
			if(LOGGING){
				Log.v(TAG, "adding house and headless fragments");
			}
			getSupportFragmentManager().beginTransaction().add(myHeadlessFragment, HEADLESS)
					.add(R.id.list, myHousesFragment, HOUSES).commit();
			
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
					
				myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
				if (myHousesFragment == null) {
					if(LOGGING){
						Log.v(TAG, "recovery failed, creating new house fragment");
						Log.v(TAG, "adding house fragment");
					}					
					myHousesFragment = new HousesFragment();
					getSupportFragmentManager().beginTransaction().add(R.id.list, myHousesFragment, HOUSES).commit();
				}
				else{
					if(LOGGING){
						Log.v(TAG, "recovery succeded");
					}
				}

			} else {
				/*
				 * if on tablet, check to see what state the activity was in e.g.
				 * showing only houses fragment, showing both houses and rooms, showing rooms and controllers
				 * attempt to recover the appropriate fragments if not make new ones
				 */
				if(LOGGING){
					Log.v(TAG, "on tablet");
					Log.v(TAG, "checking to see if a room was selected");
				}
				if (myHeadlessFragment.getSelectedRoom() != null) {
					if(LOGGING){
						Log.v(TAG, "a room is selected");
						Log.v(TAG, "attempting to recover house and room fragments");
					}
					myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
					myRoomsFragment = (RoomsFragment) getSupportFragmentManager().findFragmentByTag(ROOMS);
					if (myRoomsFragment == null) {
						if(LOGGING){
							Log.v(TAG, "room fragment recovery failed, creating new room fragment");
							Log.v(TAG, "adding room fragment");
						}
						myRoomsFragment = new RoomsFragment();
						getSupportFragmentManager().beginTransaction().add(R.id.list, myRoomsFragment, ROOMS).commit();
					}else{
						if(LOGGING){
							Log.v(TAG, "room fragment recovery succeded");
						}
					}
					if(LOGGING){
						Log.v(TAG, "attempting to recover controller fragment");
					}
					myControllersFragment = (ControllersFragment) getSupportFragmentManager().findFragmentByTag(CONTROLLERS);
					if (myControllersFragment == null) {
						if(LOGGING){
							Log.v(TAG, "controller fragment recovery failded, creating new controller fragment");
							Log.v(TAG, "adding controller fragment");
						}
						myControllersFragment = new ControllersFragment();
						getSupportFragmentManager().beginTransaction().add(R.id.expanded, myControllersFragment, CONTROLLERS).commit();
					}else{
						if(LOGGING){
							Log.v(TAG, "controller fragment recovery succeded");
						}
					}
					
				} else {
					if(LOGGING){
						Log.v(TAG, "room was not selected");
						Log.v(TAG, "checking if house was selected");
					}
					if (myHeadlessFragment.getSelectedHouse() != null) {
						if(LOGGING){
							Log.v(TAG, "a house is selected");
							Log.v(TAG, "attempting to recover houses fragment");
						}
						myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
						if (myHousesFragment == null) {
							if(LOGGING){
								Log.v(TAG, "house fragment recovery failed, creating new house fragment");
								Log.v(TAG, "adding house fragment");
							}
							myHousesFragment = new HousesFragment();
							getSupportFragmentManager().beginTransaction().add(R.id.list, myHousesFragment, HOUSES).commit();
						} 
						else {
							if(LOGGING){
								Log.v(TAG, "house fragment recovery succeded");
							}
						}
						if(LOGGING){
							Log.v(TAG, "attempting to recover rooms fragment");
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
						
					} else {
						if(LOGGING){
							Log.v(TAG, "nothing was selected");
							Log.v(TAG, "attempting to recover house fragment");
						}
						
						myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
						if (myHousesFragment == null) {
							if(LOGGING){
								Log.v(TAG, "house fragment recovery failed, creating new house fragment");
								Log.v(TAG, "adding house fragment");
							}
							myHousesFragment = new HousesFragment();
							getSupportFragmentManager().beginTransaction().add(R.id.list, myHousesFragment, HOUSES).commit();
						}
						else{
							if(LOGGING){
								Log.v(TAG, "house fragment recovery succeded");
							}
						}
					}
				}
			}
		}
	}


	@Override
	protected void onDestroy() {
		if(LOGGING){
			Log.v(TAG, "onDestroyMainActivity");
		}
		super.onDestroy();
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
	public void roomSelected(String roomName, String roomIp) {
		if(LOGGING){
			Log.v(TAG, "checking if room was selected");
		}
		if (myControllersFragment == null) {
			if(LOGGING){
				Log.v(TAG, "room selected first time");
			}
			myHeadlessFragment.setSelectedRoomWithIp(roomName, roomIp);
			if(LOGGING){
				Log.v(TAG, "attempting to recover controller fragment");
			}
			myControllersFragment = (ControllersFragment) getSupportFragmentManager().findFragmentByTag(CONTROLLERS);
			if (myControllersFragment == null) {
				if(LOGGING){
					Log.v(TAG, "controller fragment recivery failed, creating new controller fragment");
				}
				myControllersFragment = new ControllersFragment();
			}
			if(LOGGING){
				Log.v(TAG, "adding controllers fragment and moving rooms fragment to the left side");
			}
			getSupportFragmentManager().beginTransaction().replace(R.id.expanded, myControllersFragment, CONTROLLERS).commit();
			getSupportFragmentManager().executePendingTransactions();
			getSupportFragmentManager().beginTransaction().replace(R.id.list, myRoomsFragment,ROOMS).commit();
			return;
		}
		if(LOGGING){
			Log.v(TAG, "room selected not first time");
			Log.v(TAG, "checking if a different room was selected");
		}
		if (myHeadlessFragment.getSelectedRoom() != roomName) {
			if(LOGGING){
				Log.v(TAG, "different room selected");
				Log.v(TAG, "updating data");
			}
			myHeadlessFragment.setSelectedRoomWithIp(roomName, roomIp);
			myControllersFragment.replaceData(myHeadlessFragment.getSelectedHouse(), roomName, roomIp);
			myHeadlessFragment.dataSetChanged(2, myHeadlessFragment.getControllersAdapter());
			return;
		}
		if(LOGGING){
			Log.v(TAG, "old room selected");
		}
	}

	@Override
	public void houseSelected(String houseName) {
		if(LOGGING){
			Log.v(TAG, "house selected");
			Log.v(TAG, "check if on phone or tablet");
		}
		/*
		 * if on phone start a new activity
		 */
		if (findViewById(R.id.expanded) == null) {
			if(LOGGING){
				Log.v(TAG, "on phone");
			}
			Intent i = new Intent(this, RoomsActivity.class);
			i.putExtra(DBHandler.HOUSE_NAME, houseName);
			startActivity(i);
			return;
		}
		if(LOGGING){
			Log.v(TAG, "on tablet");
		}
		/*
		 * if on tablet and this the first time a house is selected
		 */
		if (myRoomsFragment == null) {
			if(LOGGING){
				Log.v(TAG, "houseSelected first time");
				Log.v(TAG, "creating new room fragment");
			}
			myHeadlessFragment.setSelectedHouse(houseName);
			myRoomsFragment = new RoomsFragment();
			if(LOGGING){
				Log.v(TAG, "added room fragment");
			}
			getSupportFragmentManager().beginTransaction().add(R.id.expanded, myRoomsFragment,ROOMS).commit();
			return;
		}
		if(LOGGING){
			Log.v(TAG, "houseSelected not first time");
		}
		/*
		 * if on tablet and a different house from the current one was selected
		 */
		if (myHeadlessFragment.getSelectedHouse() != houseName) {
			if(LOGGING){
				Log.v(TAG, "adjusting data");
			}
			myHeadlessFragment.setSelectedHouse(houseName);
			myRoomsFragment.replaceData(houseName);
			myHeadlessFragment.dataSetChanged(1, myHeadlessFragment.getRoomsAdapter());
			return;
		}

	}

	/*
	 * Data retrieval methods  
	 */
	
	@Override
	public GridAdapter getControllersAdapter() {
		return myHeadlessFragment.getControllersAdapter();
	}

	@Override
	public ListAdapter getHousesAdapter() {
		return myHeadlessFragment.getHousesAdapter();
	}

	@Override
	public ListAdapter getRoomsAdapter() {
		return myHeadlessFragment.getRoomsAdapter();
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
	public String getSelectedHouse() {
		return myHeadlessFragment.getSelectedHouse();
	}

	/*
	 * Database Interface methods
	 */
	
	@Override
	public DataBaseQueryManager getQueryManager() {
		return myHeadlessFragment.getQueryManager();
	}

	@Override
	public void dataSetChanged(int token, Object cookie) {
		if(LOGGING){
			Log.v(TAG, "dataSetChanged called"+token);
		}
		myHeadlessFragment.dataSetChanged(token, cookie);

	}

	@Override
	public void replaceCursor(Cursor cursor, Object cookie) {
		myHeadlessFragment.replaceCursor(cursor, cookie);

	}
	
	/*
	 * Network Interface methods
	 */
	
	@Override
	public void addToNetworkSender(SwitchPacket switchPacket) {
		if(LOGGING){
			Log.v(TAG, "adding packet to sender");
		}
		myHeadlessFragment.addToNetworkSender(switchPacket);
		
	}



}
