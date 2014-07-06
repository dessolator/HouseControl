package com.example.houseremote;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.database.DBHandler;
import com.example.houseremote.fragments.ControllersFragment;
import com.example.houseremote.fragments.HousesFragment;
import com.example.houseremote.fragments.HousesFragment.HouseSelectionListener;
import com.example.houseremote.fragments.RoomsFragment;
import com.example.houseremote.fragments.RoomsFragment.RoomSelectionListener;

public class MainActivity extends ActionBarActivity implements RoomSelectionListener, HouseSelectionListener {
	String currentlySelectedHouse;
	String currentlySelectedRoom;
	String currentlySelectedRoomIp;
	HousesFragment myHousesFragment;
	RoomsFragment myRoomsFragment;
	ControllersFragment myControllersFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			myHousesFragment = new HousesFragment();
			ft.add(R.id.list, myHousesFragment);

			if (findViewById(R.id.expanded) != null) {
				if (currentlySelectedHouse != null) {
					
					RoomsFragment f = new RoomsFragment();
					Bundle b = new Bundle();
					b.putString(DBHandler.HOUSE_NAME, currentlySelectedHouse);
					f.setArguments(b);
					ft.add(R.id.expanded, f);
					}
			}
			ft.commit();
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
	public void roomSelected(String roomName, String roomIp) {
		// TODO optimize
		// boy was I descriptive...
		// TODO add a currentlySelectedRoom==null case

		if (currentlySelectedRoom == roomName) {
			return;
		}
		// else start manipulating fragments
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (currentlySelectedRoom != null) {
			ft.remove(myControllersFragment);// if there was a previously
												// selected room remove it's
												// controllers fragment
		}
		currentlySelectedRoom = roomName;
		currentlySelectedRoomIp = roomIp;
		// TODO move ft init and old fragment removal here
//		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);// TODO
																									// investigate...
		myControllersFragment = new ControllersFragment();

		Bundle b = new Bundle();
		b.putString(DBHandler.HOUSE_NAME, currentlySelectedHouse);
		b.putString(DBHandler.ROOM_NAME, currentlySelectedRoom);
		b.putString(DBHandler.CONTROLLER_IP, currentlySelectedRoomIp);
		myControllersFragment.setArguments(b);

		// TODO if there's a currently selected house remove the fragments
		// else just replace the myControllersFragment
		ft.remove(myRoomsFragment);
		ft.remove(myHousesFragment);
		ft.commit();
		getSupportFragmentManager().executePendingTransactions();
		ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.list, myRoomsFragment);

		ft.add(R.id.expanded, myControllersFragment);
		ft.commit();

	}

	@Override
	public void houseSelected(String houseName) {
		if (currentlySelectedHouse == houseName) {
			return;
		}
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (currentlySelectedHouse != null) {
			ft.remove(myRoomsFragment);
		}
		currentlySelectedHouse = houseName;

		myRoomsFragment = new RoomsFragment();
		Bundle b = new Bundle();
		b.putString("house_name", currentlySelectedHouse);
		myRoomsFragment.setArguments(b);
		ft.add(R.id.expanded, myRoomsFragment);
		ft.commit();

	}

}
