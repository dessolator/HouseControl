package com.example.houseremote;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.adapters.GridAdapter;
import com.example.houseremote.adapters.ListAdapter;
import com.example.houseremote.database.AsyncQueryManager;
import com.example.houseremote.database.AsyncQueryManager.ReplyListener;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.fragments.ControllersFragment;
import com.example.houseremote.fragments.ControllersFragment.ControllersAdapterProvider;
import com.example.houseremote.fragments.ControllersFragment.SelectedRoomProvider;
import com.example.houseremote.fragments.HeadlessFragment;
import com.example.houseremote.fragments.HousesFragment;
import com.example.houseremote.fragments.HousesFragment.HouseSelectionListener;
import com.example.houseremote.fragments.HousesFragment.HousesAdapterProvider;
import com.example.houseremote.fragments.RoomsFragment;
import com.example.houseremote.fragments.RoomsFragment.QueryManagerProvider;
import com.example.houseremote.fragments.RoomsFragment.RoomSelectionListener;
import com.example.houseremote.fragments.RoomsFragment.RoomsAdapterProvider;
import com.example.houseremote.fragments.RoomsFragment.SelectedHouseProvider;

public class MainActivity extends ActionBarActivity implements ReplyListener, RoomSelectionListener,
		HouseSelectionListener, ControllersAdapterProvider, RoomsAdapterProvider, HousesAdapterProvider,
		QueryManagerProvider, SelectedHouseProvider, SelectedRoomProvider {

	private static final String HEADLESS = "headless";
	private static final String HOUSES = "houses";
	private static final String ROOMS = "rooms";
	private static final String CONTROLLERS = "controllers";
	HeadlessFragment myHeadlessFragment;
	HousesFragment myHousesFragment;
	RoomsFragment myRoomsFragment;
	ControllersFragment myControllersFragment;

	// TODO need to restore visible fragments after orientation change or such
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * if newly opened and phone make an new headless fragment and a new
		 * houses fragment and you're done if newly opened and on tab make new
		 * headless fragment and a new houses fragment and you're done if old on
		 * phone grab the headless fragment grab the houses fragment(probably
		 * need to create a new one) if old on tab grab the headless fragment
		 * check if a roomName was selected if so, grab the rooms and
		 * controllers fragments if old on tab and no room was selected, check
		 * to see if a houseName was selected, if so grab the houses and rooms
		 * fragments if old on tab and no house was selected, grab the houses
		 * fragment
		 */

		if (savedInstanceState == null) {// if activity started for the first
											// time
			myHeadlessFragment = new HeadlessFragment();
			Log.d("MOOOO", "new HeadF");
			myHousesFragment = new HousesFragment();
			Log.d("MOOOO", "new HouseF");
			getSupportFragmentManager().beginTransaction().add(myHeadlessFragment, HEADLESS)
					.add(R.id.list, myHousesFragment, HOUSES).commit();
		} else {// if coming back to this activity
			myHeadlessFragment = (HeadlessFragment) getSupportFragmentManager().findFragmentByTag(HEADLESS);
			Log.d("MOOOO", "recovered HeadF");
			if (findViewById(R.id.expanded) == null) {// if on phone
				myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
				if (myHousesFragment == null) {
					Log.d("MOOOO", "new HouseF");
					myHousesFragment = new HousesFragment();
				} else {
					Log.d("MOOOO", "recovered HouseF");
				}
//				getSupportFragmentManager().beginTransaction().add(R.id.list, myHousesFragment, HOUSES)
//						.commit();

			} else {// if on tablet
				if (myHeadlessFragment.getSelectedRoom() != null) {
					// a room is selected hence add the room and controller
					// fragment

					// attempt to grab houses fragment, just in case
					myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
					if (myHousesFragment != null) {
						Log.d("MOOOO", "recovered HouseF");
//						myHousesFragment=new HousesFragment();
					}

					// grab the rooms fragment
					myRoomsFragment = (RoomsFragment) getSupportFragmentManager().findFragmentByTag(ROOMS);
					if (myRoomsFragment == null) {
						Log.d("MOOOO", "new RoomF");
						myRoomsFragment = new RoomsFragment();
						getSupportFragmentManager().beginTransaction().add(R.id.list, myRoomsFragment, ROOMS).commit();
					} else {
						Log.d("MOOOO", "recovered RoomF");
					}

					// grab the controllers fragment
					myControllersFragment = (ControllersFragment) getSupportFragmentManager()
							.findFragmentByTag(CONTROLLERS);
					if (myControllersFragment == null) {
						Log.d("MOOOO", "new ControllerF");
						myControllersFragment = new ControllersFragment();
						getSupportFragmentManager().beginTransaction().add(R.id.expanded, myControllersFragment, CONTROLLERS).commit();
					} else {
						Log.d("MOOOO", "recovered ControllerF");
					}

				} else if (myHeadlessFragment.getSelectedHouse() != null) {
					// a house is selected hence add the house and room fragment

					// grab the houses fragment
					myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
					if (myHousesFragment == null) {
						Log.d("MOOOO", "new HouseF");
						myHousesFragment = new HousesFragment();
						getSupportFragmentManager().beginTransaction().add(R.id.list, myHousesFragment, HOUSES).commit();
					} else {
						Log.d("MOOOO", "recovered HouseF");
					}

					// grab the rooms fragment
					myRoomsFragment = (RoomsFragment) getSupportFragmentManager().findFragmentByTag(ROOMS);
					if (myRoomsFragment == null) {
						Log.d("MOOOO", "new RoomF");
						myRoomsFragment = new RoomsFragment();
						getSupportFragmentManager().beginTransaction().add(R.id.expanded, myRoomsFragment, ROOMS).commit();
					} else {
						Log.d("MOOOO", "recovered RoomF");
					}
					
				} else {
					// nothing was selected add just the house fragment

					// grab the houses fragment
					myHousesFragment = (HousesFragment) getSupportFragmentManager().findFragmentByTag(HOUSES);
					if (myHousesFragment == null) {
						Log.d("MOOOO", "new HouseF");
						myHousesFragment = new HousesFragment();
						getSupportFragmentManager().beginTransaction().add(R.id.list, myHousesFragment, HOUSES).commit();
					} else {
						Log.d("MOOOO", "recovered HouseF");
					}
				}
			}
		}
	}


	@Override
	protected void onDestroy() {
		myControllersFragment = null;
		myHeadlessFragment = null;
		myHousesFragment = null;
		myRoomsFragment = null;
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

	// TODO don't need to create new controllers fragment if one is already
	// selected
	@Override
	public void roomSelected(String roomName, String roomIp) {

		if (findViewById(R.id.expanded) == null) {	// if on phone start the new
													// activity passing roomName
													// and houseName as args
			Intent i = new Intent(this, ControllersActivity.class);
			i.putExtra(DBHandler.HOUSE_NAME, myHeadlessFragment.getSelectedHouse());
			i.putExtra(DBHandler.ROOM_NAME, roomName);
			i.putExtra(DBHandler.CONTROLLER_IP, roomIp);
			startActivity(i);
			return;
		}

		if (myControllersFragment == null) {		// check to see if the controller
			// fragment is even loaded if
			// not make a new one and pass
			// it args
			myHeadlessFragment.setSelectedRoomWithIp(roomName, roomIp);

			myControllersFragment = (ControllersFragment) getSupportFragmentManager().findFragmentByTag(
					CONTROLLERS);
			if (myControllersFragment == null) {
				myControllersFragment = new ControllersFragment();
				Log.d("MOOOO", "new HF");
			}

			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.expanded, myControllersFragment, CONTROLLERS);			// replace room
			// and
			// controller
			// fragment
			ft.commit();
			getSupportFragmentManager().executePendingTransactions();	// not the
																		// most
																		// elegant
																		// thing
																		// ever...
																		// but
																		// no
																		// need
																		// to
																		// create
																		// new
																		// fragment
			ft = getSupportFragmentManager().beginTransaction();
//			getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			ft.replace(R.id.list, myRoomsFragment,ROOMS);			// replace house and room
			// fragment
			ft.commit();
			return;
		}

		if (myHeadlessFragment.getSelectedRoom() != roomName) {		// if a new room
			// is selected
			// change data
			// in the
			// controllers
			// fragment
			myHeadlessFragment.setSelectedRoomWithIp(roomName, roomIp);
			myControllersFragment.replaceData(myHeadlessFragment.getSelectedHouse(), roomName, roomIp);
			myHeadlessFragment.dataSetChanged(2, myHeadlessFragment.getControllersAdapter());
			return;
		}
	}

	@Override
	public void houseSelected(String houseName) {
		if (findViewById(R.id.expanded) == null) {	// if on phone start the new
													// activity passing roomName
													// and houseName as args
			Intent i = new Intent(this, RoomsActivity.class);
			i.putExtra(DBHandler.HOUSE_NAME, houseName);
			startActivity(i);
			return;
		}
		if (myRoomsFragment == null) {
			myHeadlessFragment.setSelectedHouse(houseName);
			myRoomsFragment = new RoomsFragment();
			Log.d("MOOOO", "new RF");
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.expanded, myRoomsFragment,ROOMS);
			ft.commit();
			return;
		}
		if (myHeadlessFragment.getSelectedHouse() != houseName) {
			myHeadlessFragment.setSelectedHouse(houseName);
			myRoomsFragment.replaceData(houseName);
			myHeadlessFragment.dataSetChanged(1, myHeadlessFragment.getRoomsAdapter());
			return;
		}

	}

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

}
