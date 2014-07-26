package com.example.houseremote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.database.DBHandler;
import com.example.houseremote.fragments.RoomsFragment;
import com.example.houseremote.interfaces.RoomSelectionListener;

public class RoomsActivity extends ActionBarActivity implements RoomSelectionListener{
	String currentlySelectedHouse;
	RoomsFragment myRoomsFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		currentlySelectedHouse=getIntent().getStringExtra(DBHandler.HOUSE_NAME);

		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			myRoomsFragment=new RoomsFragment();
			Bundle  b = new Bundle();
			b.putString(DBHandler.HOUSE_NAME, currentlySelectedHouse);
			myRoomsFragment.setArguments(b);
			ft.add(R.id.list, myRoomsFragment);
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
	public void roomSelected(String roomName, String roomIp) {
		
		Intent i = new Intent(this, ControllersActivity.class);
		i.putExtra(DBHandler.HOUSE_NAME, currentlySelectedHouse);
		i.putExtra(DBHandler.ROOM_NAME, roomName);
		i.putExtra(DBHandler.CONTROLLER_IP, roomIp);
		startActivity(i);
		return;
	}
}
