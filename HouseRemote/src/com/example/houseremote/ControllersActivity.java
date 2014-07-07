package com.example.houseremote;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.database.DBHandler;
import com.example.houseremote.fragments.ControllersFragment;

public class ControllersActivity extends ActionBarActivity {
	String currentlySelectedHouse;
	String currentlySelectedRoom;
	String currentlySelectedRoomIp;
	ControllersFragment myControllersFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		currentlySelectedHouse=getIntent().getStringExtra(DBHandler.HOUSE_NAME);
		currentlySelectedRoom=getIntent().getStringExtra(DBHandler.ROOM_NAME);
		currentlySelectedRoomIp=getIntent().getStringExtra(DBHandler.CONTROLLER_IP);

		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			myControllersFragment= new ControllersFragment();
			Bundle  b = new Bundle();
			b.putString(DBHandler.HOUSE_NAME, currentlySelectedHouse);
			b.putString(DBHandler.ROOM_NAME, currentlySelectedRoom);
			b.putString(DBHandler.CONTROLLER_IP, currentlySelectedRoomIp);
			myControllersFragment.setArguments(b);
			ft.add(R.id.list, myControllersFragment);
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
}
