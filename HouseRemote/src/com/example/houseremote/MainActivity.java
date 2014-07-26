package com.example.houseremote;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.adapters.GridAdapter;
import com.example.houseremote.adapters.ListAdapter;
import com.example.houseremote.database.AsyncQueryManager;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.fragments.ControllersFragment;
import com.example.houseremote.fragments.HeadlessFragment;
import com.example.houseremote.fragments.HousesFragment;
import com.example.houseremote.fragments.RoomsFragment;
import com.example.houseremote.interfaces.ControllerStateQueryListener;
import com.example.houseremote.interfaces.ControllerStateQueryProvider;
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
import com.example.houseremote.network.ControllerStateQuery;
import com.example.houseremote.network.SwitchPacket;

public class MainActivity extends ActionBarActivity implements ReplyListener, RoomSelectionListener,
		HouseSelectionListener, ControllersAdapterProvider, RoomsAdapterProvider, HousesAdapterProvider,
		QueryManagerProvider, SelectedHouseProvider, SelectedRoomProvider, NetworkCommandListener,ControllerStateQueryProvider{

	private static final String HEADLESS = "headless";
	private static final String HOUSES = "houses";
	private static final String ROOMS = "rooms";
	private static final String CONTROLLERS = "controllers";
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
			myHeadlessFragment = new HeadlessFragment();
			myHousesFragment = new HousesFragment();
			getSupportFragmentManager().beginTransaction().add(myHeadlessFragment, HEADLESS)
					.add(R.id.list, myHousesFragment, HOUSES).commit();
			
		} else {
			/*
			 * if coming back to the activity from somewhere
			 * recover the headless fragment
			 */
			myHeadlessFragment = (HeadlessFragment) getSupportFragmentManager().findFragmentByTag(HEADLESS);
			/*
			 * and if on phone try to recover the house fragment if not make a new one
			 */
			if (findViewById(R.id.expanded) == null) {
				myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
				if (myHousesFragment == null) {
					myHousesFragment = new HousesFragment();
					getSupportFragmentManager().beginTransaction().add(R.id.list, myHousesFragment, HOUSES).commit();
				}

			} else {
				/*
				 * if on tablet, check to see what state the activity was in e.g.
				 * showing only houses fragment, showing both houses and rooms, showing rooms and controllers
				 * attempt to recover the appropriate fragments if not make new ones
				 */
				
				if (myHeadlessFragment.getSelectedRoom() != null) {
					myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
					myRoomsFragment = (RoomsFragment) getSupportFragmentManager().findFragmentByTag(ROOMS);
					if (myRoomsFragment == null) {
						myRoomsFragment = new RoomsFragment();
						getSupportFragmentManager().beginTransaction().add(R.id.list, myRoomsFragment, ROOMS).commit();
					}
					myControllersFragment = (ControllersFragment) getSupportFragmentManager().findFragmentByTag(CONTROLLERS);
					if (myControllersFragment == null) {
						myControllersFragment = new ControllersFragment();
						getSupportFragmentManager().beginTransaction().add(R.id.expanded, myControllersFragment, CONTROLLERS).commit();
					}
					
				} else if (myHeadlessFragment.getSelectedHouse() != null) {
					myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
					if (myHousesFragment == null) {
						myHousesFragment = new HousesFragment();
						getSupportFragmentManager().beginTransaction().add(R.id.list, myHousesFragment, HOUSES).commit();
					} 
					myRoomsFragment = (RoomsFragment) getSupportFragmentManager().findFragmentByTag(ROOMS);
					if (myRoomsFragment == null) {
						myRoomsFragment = new RoomsFragment();
						getSupportFragmentManager().beginTransaction().add(R.id.expanded, myRoomsFragment, ROOMS).commit();
					}
					
				} else {
					myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
					if (myHousesFragment == null) {
						myHousesFragment = new HousesFragment();
						getSupportFragmentManager().beginTransaction().add(R.id.list, myHousesFragment, HOUSES).commit();
					}
				}
			}
		}
	}


	@Override
	protected void onDestroy() {
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

		if (myControllersFragment == null) {
			myHeadlessFragment.setSelectedRoomWithIp(roomName, roomIp);

			myControllersFragment = (ControllersFragment) getSupportFragmentManager().findFragmentByTag(CONTROLLERS);
			if (myControllersFragment == null) {
				myControllersFragment = new ControllersFragment();
			}

			getSupportFragmentManager().beginTransaction().replace(R.id.expanded, myControllersFragment, CONTROLLERS).commit();
			getSupportFragmentManager().executePendingTransactions();
			getSupportFragmentManager().beginTransaction().replace(R.id.list, myRoomsFragment,ROOMS).commit();
			return;
		}

		if (myHeadlessFragment.getSelectedRoom() != roomName) {
			myHeadlessFragment.setSelectedRoomWithIp(roomName, roomIp);
			myControllersFragment.replaceData(myHeadlessFragment.getSelectedHouse(), roomName, roomIp);
			myHeadlessFragment.dataSetChanged(2, myHeadlessFragment.getControllersAdapter());
			return;
		}
	}

	@Override
	public void houseSelected(String houseName) {
		/*
		 * if on phone start a new activity
		 */
		if (findViewById(R.id.expanded) == null) {
			Intent i = new Intent(this, RoomsActivity.class);
			i.putExtra(DBHandler.HOUSE_NAME, houseName);
			startActivity(i);
			return;
		}
		/*
		 * if on tablet and this the first time a house is selected
		 */
		if (myRoomsFragment == null) {
			myHeadlessFragment.setSelectedHouse(houseName);
			myRoomsFragment = new RoomsFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.expanded, myRoomsFragment,ROOMS).commit();
			return;
		}
		/*
		 * if on tablet and a different house from the current one was selected
		 */
		if (myHeadlessFragment.getSelectedHouse() != houseName) {
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
	public AsyncQueryManager getQueryManager() {
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
	 * Network interfaces
	 */

	@Override
	public void startNetworkListener() {
		myHeadlessFragment.startNetworkListener();
		
	}


	@Override
	public void stopNetworkListener() {
		myHeadlessFragment.stopNetworkListener();
		
	}


	@Override
	public void startNetworkSender() {
		myHeadlessFragment.startNetworkSender();
		
	}


	@Override
	public void stopNetworkSender() {
		myHeadlessFragment.stopNetworkSender();
		
	}


	@Override
	public void addToNetworkSender(SwitchPacket switchPacket) {
		myHeadlessFragment.addToNetworkSender(switchPacket);
		
	}


	@Override
	public ControllerStateQuery getStateQuery() {
		return myHeadlessFragment.getStateQuery();
	}


	@Override
	public void restartFullStateRead(String ip, ControllerStateQueryListener ql) {
		myHeadlessFragment.restartFullStateRead(ip,ql);
		
	}



}
