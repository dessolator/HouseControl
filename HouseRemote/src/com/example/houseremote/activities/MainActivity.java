package com.example.houseremote.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.R;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DataBaseAsyncQueryHandler;
import com.example.houseremote.database.adapters.GridAdapter;
import com.example.houseremote.database.adapters.ListAdapter;
import com.example.houseremote.database.interfaces.ControllersAdapterProvider;
import com.example.houseremote.database.interfaces.HousesAdapterProvider;
import com.example.houseremote.database.interfaces.QueryManagerProvider;
import com.example.houseremote.database.interfaces.ReplyListener;
import com.example.houseremote.database.interfaces.RoomsAdapterProvider;
import com.example.houseremote.fragments.ControllersFragment;
import com.example.houseremote.fragments.PrimaryHeadlessFragment;
import com.example.houseremote.fragments.HousesFragment;
import com.example.houseremote.fragments.RoomsFragment;
import com.example.houseremote.interfaces.HouseSelectionListener;
import com.example.houseremote.interfaces.RoomSelectionListener;
import com.example.houseremote.interfaces.SelectedHouseProvider;
import com.example.houseremote.interfaces.SelectedRoomProvider;
import com.example.houseremote.network.interfaces.NetworkCommandListener;
import com.example.houseremote.network.interfaces.Sendable;

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
	 * Fragments
	 */
	private PrimaryHeadlessFragment myHeadlessFragment;
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
				
				if (myHeadlessFragment.getSelectedRoomID() >0) {//if on tablet and should display rooms and houses fragment
					myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
					myRoomsFragment = acquireRoomsFragmentToList();					
					myControllersFragment = acquireControllersFragmentToExpanded();
					
				} else {
					if (myHeadlessFragment.getSelectedHouseID() >0) {//if on tablet and should display houses and rooms fragment
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
	private PrimaryHeadlessFragment acquireHeadlessFragment(){
		PrimaryHeadlessFragment temp = (PrimaryHeadlessFragment) getSupportFragmentManager().findFragmentByTag(HEADLESS);
		if (temp == null){
			temp= new PrimaryHeadlessFragment();
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
			Intent i = new Intent(this, AutoSearchActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void roomSelected(long roomID) {
		
		if (myHeadlessFragment.getSelectedRoomID()<=0) {//if no room was selected
			
			myHeadlessFragment.setSelectedRoomID(roomID);
			
			getSupportFragmentManager().beginTransaction().remove(myRoomsFragment).commit();
			myControllersFragment = acquireControllersFragmentToExpanded();
			getSupportFragmentManager().executePendingTransactions();
			getSupportFragmentManager().beginTransaction().replace(R.id.list, myRoomsFragment,ROOMS).commit();
			return;
		}
		if (myHeadlessFragment.getSelectedRoomID() != roomID) {//if a different room was selected
			
			changeSelectedRoom(roomID);
			return;
		}
	}


	private void changeSelectedRoom(long roomID) {
		myHeadlessFragment.setSelectedRoomID(roomID);
		myControllersFragment.replaceData(roomID);
		myHeadlessFragment.reloadControllerData();
	}

	@Override
	public void houseSelected(long houseID) {

		if (findViewById(R.id.expanded) == null) {//if on phone start new activity
			
			Intent i = new Intent(this, RoomsActivity.class);
			i.putExtra(DBHandler.HOUSE_ID, houseID);
			startActivity(i);
			return;
		}
		if (myHeadlessFragment.getSelectedHouseID() <=0) {//if no house was selected previously

			myHeadlessFragment.setSelectedHouseID(houseID);
			myRoomsFragment = new RoomsFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.expanded, myRoomsFragment,ROOMS).commit();
			return;
		}
		
		if (myHeadlessFragment.getSelectedHouseID() != houseID) {//if a different house was selected previously

			changeSelectedHouse(houseID);
			return;
		}

	}

	@Override
	public void onBackPressed() {
		if (findViewById(R.id.expanded) == null) {
			super.onBackPressed();
			return;
		}
		if(myHeadlessFragment.getSelectedRoomID()>0){
			myHeadlessFragment.setSelectedRoomID(0);
			myHeadlessFragment.setInitialControllerDataLoaded(false);
			getSupportFragmentManager().beginTransaction().remove(myControllersFragment).commit();
			getSupportFragmentManager().beginTransaction().remove(myRoomsFragment).commit();
			if(myHousesFragment==null){myHousesFragment = new HousesFragment();}
			getSupportFragmentManager().beginTransaction().add(R.id.list, myHousesFragment, HOUSES).commit();
			getSupportFragmentManager().executePendingTransactions();
			getSupportFragmentManager().beginTransaction().replace(R.id.expanded, myRoomsFragment,ROOMS).commit();
			return;
		}
		if(myHeadlessFragment.getSelectedHouseID()>0){
			myHeadlessFragment.setSelectedHouseID(0);
			myHeadlessFragment.setInitialRoomDataLoaded(false);
			getSupportFragmentManager().beginTransaction().remove(myRoomsFragment).commit();
			return;
		}
		super.onBackPressed();
		

	}
	
	
	
	
	
	private void changeSelectedHouse(long houseID) {
		myHeadlessFragment.setSelectedHouseID(houseID);
		myRoomsFragment.replaceData(houseID);
		myHeadlessFragment.reloadRoomData();
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
	public long getSelectedRoomID() {
		return myHeadlessFragment.getSelectedRoomID();
	}

	@Override
	public long getSelectedHouseID() {
		return myHeadlessFragment.getSelectedHouseID();
	}

	/*
	 * Database Interface methods
	 */
	
	@Override
	public DataBaseAsyncQueryHandler getQueryManager() {
		return myHeadlessFragment.getQueryManager();
	}

	@Override
	public void replaceCursor(Cursor cursor, Object cookie) {
		myHeadlessFragment.replaceCursor(cursor, cookie);

	}
	
	/*
	 * Network Interface methods
	 */
	
	@Override
	public void addToNetworkSender(String senderIp, Sendable switchPacket) {
		myHeadlessFragment.addToNetworkSender(senderIp, switchPacket);
		
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
	public void reloadControllerData() {
		myHeadlessFragment.reloadControllerData();
		
	}


	@Override
	public void reloadHouseData() {
		myHeadlessFragment.reloadHouseData();
		
	}


	@Override
	public void reloadRoomData() {
		myHeadlessFragment.reloadRoomData();
		
	}



}
