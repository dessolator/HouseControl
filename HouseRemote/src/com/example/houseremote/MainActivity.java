package com.example.houseremote;

import android.content.Intent;
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
	
		if (findViewById(R.id.expanded) == null){	//if on phone start the new activity passing roomName and houseName as args
			Intent i= new Intent(this, ControllersActivity.class);
			i.putExtra(DBHandler.HOUSE_NAME, currentlySelectedHouse);
			i.putExtra(DBHandler.ROOM_NAME, roomName);
			i.putExtra(DBHandler.CONTROLLER_IP, roomIp);
			startActivity(i);
			return;
		}

		if (myControllersFragment==null) {		//check to see if the controller fragment is even loaded if not make a new one and pass it args
			currentlySelectedRoom = roomName;
			currentlySelectedRoomIp = roomIp;
			myControllersFragment = new ControllersFragment();

			Bundle b = new Bundle();
			b.putString(DBHandler.HOUSE_NAME, currentlySelectedHouse);
			b.putString(DBHandler.ROOM_NAME, currentlySelectedRoom);
			b.putString(DBHandler.CONTROLLER_IP, currentlySelectedRoomIp);
			myControllersFragment.setArguments(b);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.expanded, myControllersFragment);			//replace room and controller fragment
			ft.commit();
			getSupportFragmentManager().executePendingTransactions();	//not the most elegant thing ever... but no need to create new fragment
			ft= getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.list, myRoomsFragment);			//replace house and room fragment
			ft.commit();
			return;
		}		

		if(currentlySelectedRoom!=roomName){		//if a new room is selected change data in the controllers fragment
			currentlySelectedRoom=roomName;
			currentlySelectedRoomIp=roomIp;
			myControllersFragment.replaceData(currentlySelectedHouse, roomName, roomIp);
			myControllersFragment.dataSetChanged();
			return;
		}
	}

	@Override
	public void houseSelected(String houseName) {
		if (findViewById(R.id.expanded) == null){	//if on phone start the new activity passing roomName and houseName as args
			Intent i= new Intent(this, RoomsActivity.class);
			i.putExtra(DBHandler.HOUSE_NAME, houseName);
			startActivity(i);
			return;
		}
		if(myRoomsFragment==null){
			currentlySelectedHouse=houseName;
			myRoomsFragment=new RoomsFragment();
			Bundle b= new Bundle();
			b.putString("house_name", currentlySelectedHouse);
			myRoomsFragment.setArguments(b);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.expanded, myRoomsFragment);
			ft.commit();
			return;
		}
		if(currentlySelectedHouse!=houseName){
			currentlySelectedHouse=houseName;
			myRoomsFragment.replaceData(houseName);
			myRoomsFragment.dataSetChanged();
			return;
		}
		
	}

}
