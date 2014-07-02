package com.example.houseremote;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.fragments.ControllersFragment;
import com.example.houseremote.fragments.HousesFragment;
import com.example.houseremote.fragments.HousesFragment.HouseSelectionListener;
import com.example.houseremote.fragments.RoomsFragment;
import com.example.houseremote.fragments.RoomsFragment.RoomSelectionListener;

public class MainActivity extends ActionBarActivity implements
		RoomSelectionListener, HouseSelectionListener {
	String currentlySelectedHouse;
	String currentlySelectedRoom;
	HousesFragment myHousesFragment;
	RoomsFragment myRoomsFragment;
	ControllersFragment myControllersFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			myHousesFragment=new HousesFragment();
			ft.add(R.id.list, myHousesFragment);
			if (findViewById(R.id.expanded) != null) {
				if (currentlySelectedHouse != null) {
					RoomsFragment f = new RoomsFragment();
					Bundle b = new Bundle();
					b.putString("house_name", "Pera");
					f.setArguments(b);
					ft.add(R.id.expanded, f);
				}
			}
			ft.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			// TODO Open Settings?
			Log.d("MOO", "settings");
			return true;
		}
		// TODO move the ADD A HOUSE BUTTON TO HOUSE LIST FRAGMENT
		// if (id == R.id.action_add_a_house) {
		// Log.d("MOO","house");
		// //TODO Shove a new house into the database and start the edit
		// activity passing that house's name
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void roomSelected(String roomName) {
		//TODO optimize
		if(currentlySelectedRoom==roomName){
			return;
		}
		FragmentTransaction ft = getSupportFragmentManager()
				.beginTransaction();
		if(currentlySelectedRoom!=null){
			ft.remove(myControllersFragment);
		}
		currentlySelectedRoom = roomName;
		getSupportFragmentManager();
		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		myControllersFragment = new ControllersFragment();
		Bundle b = new Bundle();
		b.putString("house_name", currentlySelectedHouse);
		b.putString("room_name", currentlySelectedRoom);
		myControllersFragment.setArguments(b);
		ft.remove(myRoomsFragment);
		ft.remove(myHousesFragment);
		ft.commit();
		getSupportFragmentManager().executePendingTransactions();
		ft= getSupportFragmentManager().beginTransaction();
		ft.add(R.id.list, myRoomsFragment);
		ft.add(R.id.expanded, myControllersFragment);
		ft.commit();
		Log.d("MOOOOOOOO", roomName);

	}

	@Override
	public void houseSelected(String houseName) {
		if(currentlySelectedHouse==houseName){
			return;
		}
		FragmentTransaction ft = getSupportFragmentManager()
				.beginTransaction();
		if(currentlySelectedHouse!=null){
			ft.remove(myRoomsFragment);
		}
		currentlySelectedHouse = houseName;
		
		myRoomsFragment = new RoomsFragment();
		Bundle b = new Bundle();
		b.putString("house_name", currentlySelectedHouse);
		myRoomsFragment.setArguments(b);
		ft.add(R.id.expanded, myRoomsFragment);
		ft.commit();
		Log.d("MOOOOOOOO", houseName);

	}

}
