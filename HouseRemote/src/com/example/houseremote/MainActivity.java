package com.example.houseremote;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
	
	/*
	 * Logging variables
	 */
//	private static final String TAG = "com.example.houseremote.MainActivity";
//	public static final boolean LOGGING = true;
	
	/*
	 * Fragments
	 */
	private HeadlessFragment myHeadlessFragment;
	private HousesFragment myHousesFragment;
	private RoomsFragment myRoomsFragment;
	private ControllersFragment myControllersFragment;

	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		if (savedInstanceState == null) {//if starting for first time
			myHeadlessFragment = acquireHeadlessFragment();
			myHousesFragment = acquireHousesFragmentToList();
		
		} else {
			
			myHeadlessFragment = acquireHeadlessFragment();
			
			if (findViewById(R.id.expanded) == null) {//if on phone and not for first time
				myHousesFragment = acquireHousesFragmentToList();
			} else {
				
				if (myHeadlessFragment.getSelectedRoom() != null) {//if on tablet and should display rooms and houses fragment
					myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
					myRoomsFragment = acquireRoomsFragmentToList();					
					myControllersFragment = acquireControllersFragmentToExpanded();
					
				} else {
					if (myHeadlessFragment.getSelectedHouse() != null) {//if on tablet and should display houses and rooms fragment
						myHousesFragment = acquireHousesFragmentToList();
						myRoomsFragment = acquireRoomsFragmentToExpanded();						
					} else {//if on tablet and should display only houses fragment
						myHousesFragment = acquireHousesFragmentToList();
					}
				}
			}
		}
	}


	private ControllersFragment acquireControllersFragmentToExpanded() {
		ControllersFragment temp = (ControllersFragment) getSupportFragmentManager().findFragmentByTag(CONTROLLERS);
		if (temp == null) {
			temp = new ControllersFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.expanded, temp, CONTROLLERS).commit();
			return temp;
		}		
		return temp;
	}


	private RoomsFragment acquireRoomsFragmentToExpanded() {
		RoomsFragment temp = (RoomsFragment) getSupportFragmentManager().findFragmentByTag(ROOMS);
		if (temp == null) {
			temp = new RoomsFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.expanded, temp, ROOMS).commit();
		}
		return temp;
	}
	private RoomsFragment acquireRoomsFragmentToList() {
		RoomsFragment temp = (RoomsFragment) getSupportFragmentManager().findFragmentByTag(ROOMS);
		if (temp == null) {
			temp = new RoomsFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.list, temp, ROOMS).commit();
		}
		return temp;
	}
	private HousesFragment acquireHousesFragmentToList() {
		HousesFragment temp = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
		if(temp==null){
			temp= new HousesFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.list, temp, HOUSES).commit();
		}
		return temp;
	}
	private HeadlessFragment acquireHeadlessFragment(){
		HeadlessFragment temp = (HeadlessFragment) getSupportFragmentManager().findFragmentByTag(HEADLESS);
		if (temp == null){
			temp= new HeadlessFragment();
			getSupportFragmentManager().beginTransaction().add(temp, HEADLESS).commit();
		}
		return temp;
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
		
		if (myHeadlessFragment.getSelectedRoom() == null) {//if no room was selected
			
			myHeadlessFragment.setSelectedRoomWithIp(roomName, roomIp);
			
			getSupportFragmentManager().beginTransaction().remove(myRoomsFragment).commit();
			myControllersFragment = acquireControllersFragmentToExpanded();
			getSupportFragmentManager().executePendingTransactions();
			getSupportFragmentManager().beginTransaction().replace(R.id.list, myRoomsFragment,ROOMS).commit();
			return;
		}
		if (myHeadlessFragment.getSelectedRoom() != roomName) {//if a different room was selected
			
			myHeadlessFragment.setSelectedRoomWithIp(roomName, roomIp);
			myControllersFragment.replaceData(myHeadlessFragment.getSelectedHouse(), roomName, roomIp);
			myHeadlessFragment.dataSetChanged(2, myHeadlessFragment.getControllersAdapter());
			return;
		}
	}

	@Override
	public void houseSelected(String houseName) {

		if (findViewById(R.id.expanded) == null) {//if on phone start new activity
			
			Intent i = new Intent(this, RoomsActivity.class);
			i.putExtra(DBHandler.HOUSE_NAME, houseName);
			startActivity(i);
			return;
		}
		if (myHeadlessFragment.getSelectedHouse() == null) {//if no house was selected previously

			myHeadlessFragment.setSelectedHouse(houseName);
			myRoomsFragment = new RoomsFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.expanded, myRoomsFragment,ROOMS).commit();
			return;
		}
		
		if (myHeadlessFragment.getSelectedHouse() != houseName) {//if a different house was selected previously

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
		myHeadlessFragment.addToNetworkSender(switchPacket);
		
	}


	@Override
	public boolean isInitialControllerDataLoaded() {
		return myHeadlessFragment.isInitialControllerDataLoaded();
	}


	@Override
	public void setInitialControllerDataLoaded(boolean initialControllerDataLoaded) {
		myHeadlessFragment.setInitialControllerDataLoaded(initialControllerDataLoaded);
		
	}


	@Override
	public boolean isInitialHouseDataLoaded() {
		return myHeadlessFragment.isInitialHouseDataLoaded();
	}


	@Override
	public void setInitialHouseDataLoaded(boolean b) {
		myHeadlessFragment.setInitialHouseDataLoaded(b);
	}


	@Override
	public boolean isInitialRoomDataLoaded() {
		return myHeadlessFragment.isInitialRoomDataLoaded();
	}


	@Override
	public void setInitialRoomDataLoaded(boolean b) {
		myHeadlessFragment.setInitialRoomDataLoaded(b);
		
	}
	@Override
	public void onBackPressed() {
		if (findViewById(R.id.expanded) == null) {
			super.onBackPressed();
			return;
		}
		if(myHeadlessFragment.getSelectedRoom()!=null){
			myHeadlessFragment.setSelectedRoomWithIp(null, null);
			myHeadlessFragment.setInitialControllerDataLoaded(false);
			getSupportFragmentManager().beginTransaction().remove(myControllersFragment).commit();
			getSupportFragmentManager().beginTransaction().remove(myRoomsFragment).commit();
			if(myHousesFragment==null){myHousesFragment = new HousesFragment();}
			getSupportFragmentManager().beginTransaction().add(R.id.list, myHousesFragment, HOUSES).commit();
			getSupportFragmentManager().executePendingTransactions();
			getSupportFragmentManager().beginTransaction().replace(R.id.expanded, myRoomsFragment,ROOMS).commit();
			return;
		}
		if(myHeadlessFragment.getSelectedHouse()!=null){
			myHeadlessFragment.setSelectedHouse(null);
			myHeadlessFragment.setInitialRoomDataLoaded(false);
			getSupportFragmentManager().beginTransaction().remove(myRoomsFragment).commit();
			return;
		}
		super.onBackPressed();
		

	}



}
