package com.example.houseremote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.R;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.fragments.AbstractHeadlessFragment;
import com.example.houseremote.fragments.ControllersFragment;
import com.example.houseremote.fragments.HousesFragment;
import com.example.houseremote.fragments.MainActivityHeadlessFragment;
import com.example.houseremote.fragments.RoomsFragment;
import com.example.houseremote.interfaces.HeadlessProvider;
import com.example.houseremote.interfaces.HouseSelectionListener;
import com.example.houseremote.interfaces.RoomSelectionListener;

public class MainActivity extends ActionBarActivity implements HeadlessProvider, RoomSelectionListener,HouseSelectionListener{

	


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
	private MainActivityHeadlessFragment mHeadlessFragment;
	private HousesFragment myHousesFragment;
	private RoomsFragment myRoomsFragment;
	private ControllersFragment myControllersFragment;

	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		if (savedInstanceState == null) {//if starting for first time
			mHeadlessFragment = acquireHeadlessFragment();
			myHousesFragment = acquireHousesFragmentToList();
		
		} else {
			
			mHeadlessFragment = acquireHeadlessFragment();
			
			if (findViewById(R.id.expanded) == null) {//if on phone and not for first time
				myHousesFragment = acquireHousesFragmentToList();
			} else {
				
				if (mHeadlessFragment.getSelectedRoomID() >0) {//if on tablet and should display rooms and houses fragment
					myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
					myRoomsFragment = acquireRoomsFragmentToList();					
					myControllersFragment = acquireControllersFragmentToExpanded();
					
				} else {
					if (mHeadlessFragment.getSelectedHouseID() >0) {//if on tablet and should display houses and rooms fragment
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
	private MainActivityHeadlessFragment acquireHeadlessFragment(){
		MainActivityHeadlessFragment temp = (MainActivityHeadlessFragment) getSupportFragmentManager().findFragmentByTag(HEADLESS);
		if (temp == null){
			temp= new MainActivityHeadlessFragment();
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
		
		if (mHeadlessFragment.getSelectedRoomID()<=0) {//if no room was selected
			
			mHeadlessFragment.setSelectedRoomID(roomID);
			
			getSupportFragmentManager().beginTransaction().remove(myRoomsFragment).commit();
			myControllersFragment = acquireControllersFragmentToExpanded();
			getSupportFragmentManager().executePendingTransactions();
			getSupportFragmentManager().beginTransaction().replace(R.id.list, myRoomsFragment,ROOMS).commit();
			return;
		}
		if (mHeadlessFragment.getSelectedRoomID() != roomID) {//if a different room was selected
			
			changeSelectedRoom(roomID);
			return;
		}
	}


	private void changeSelectedRoom(long roomID) {
		mHeadlessFragment.setSelectedRoomID(roomID);
		myControllersFragment.replaceData(roomID);
		mHeadlessFragment.onControllerDataChanged();
	}

	@Override
	public void houseSelected(long houseID) {

		if (findViewById(R.id.expanded) == null) {//if on phone start new activity
			
			Intent i = new Intent(this, RoomsActivity.class);
			i.putExtra(DBHandler.HOUSE_ID, houseID);
			startActivity(i);
			return;
		}
		if (mHeadlessFragment.getSelectedHouseID() <=0) {//if no house was selected previously

			mHeadlessFragment.setSelectedHouseID(houseID);
			myRoomsFragment = new RoomsFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.expanded, myRoomsFragment,ROOMS).commit();
			return;
		}
		
		if (mHeadlessFragment.getSelectedHouseID() != houseID) {//if a different house was selected previously

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
		if(mHeadlessFragment.getSelectedRoomID()>0){
			mHeadlessFragment.setSelectedRoomID(0);
			mHeadlessFragment.setInitialControllerDataLoaded(false);
			getSupportFragmentManager().beginTransaction().remove(myControllersFragment).commit();
			getSupportFragmentManager().beginTransaction().remove(myRoomsFragment).commit();
			if(myHousesFragment==null){myHousesFragment = new HousesFragment();}
			getSupportFragmentManager().beginTransaction().add(R.id.list, myHousesFragment, HOUSES).commit();
			getSupportFragmentManager().executePendingTransactions();
			getSupportFragmentManager().beginTransaction().replace(R.id.expanded, myRoomsFragment,ROOMS).commit();
			return;
		}
		if(mHeadlessFragment.getSelectedHouseID()>0){
			mHeadlessFragment.setSelectedHouseID(0);
			mHeadlessFragment.setInitialRoomDataLoaded(false);
			getSupportFragmentManager().beginTransaction().remove(myRoomsFragment).commit();
			return;
		}
		super.onBackPressed();
		

	}
	
	
	
	
	
	private void changeSelectedHouse(long houseID) {
		mHeadlessFragment.setSelectedHouseID(houseID);
		myRoomsFragment.replaceData(houseID);
		mHeadlessFragment.onRoomDataChanged();
	}


	@Override
	public AbstractHeadlessFragment getHeadlessFragment() {
		return mHeadlessFragment;
	}




}
