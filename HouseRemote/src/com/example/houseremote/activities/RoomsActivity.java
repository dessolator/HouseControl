package com.example.houseremote.activities;

import static com.example.houseremote.activities.MainActivity.HEADLESS;
import static com.example.houseremote.activities.MainActivity.ROOMS;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.R;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.fragments.RoomsActivityHeadlessFragment;
import com.example.houseremote.fragments.RoomsFragment;
import com.example.houseremote.fragments.interfaces.RoomsActivityHeadlessFragmentInterface;
import com.example.houseremote.fragments.interfaces.RoomsActivityHeadlessProvider;
import com.example.houseremote.interfaces.RoomSelectionListener;

public class RoomsActivity extends ActionBarActivity implements RoomsActivityHeadlessProvider, RoomSelectionListener{
	
	private RoomsActivityHeadlessFragment mHeadlessFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_main);

		mHeadlessFragment = acquireHeadlessFragment();
		acquireRoomsFragmentToList();

	}

	private RoomsFragment acquireRoomsFragmentToList() {
		RoomsFragment temp = (RoomsFragment) getSupportFragmentManager().findFragmentByTag(ROOMS);
		if(temp==null){
			temp= new RoomsFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.list, temp, ROOMS).commit();
		}
		return temp;
	}
	private RoomsActivityHeadlessFragment acquireHeadlessFragment(){
		RoomsActivityHeadlessFragment temp = (RoomsActivityHeadlessFragment) getSupportFragmentManager().findFragmentByTag(HEADLESS);
		if (temp == null){
			temp= new RoomsActivityHeadlessFragment();
			temp.setSelectedHouseID(getIntent().getIntExtra(DBHandler.HOUSE_ID, -1));
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
	public void roomSelected(long roomID) {
		
		Intent i = new Intent(this, ControllersActivity.class);
		i.putExtra(DBHandler.ROOM_ID, roomID);
		startActivity(i);
		return;
	}
	
	@Override
	public boolean onNavigateUp() {
		super.onBackPressed();
		return true;
	}


	@Override
	public RoomsActivityHeadlessFragmentInterface getRoomsHeadlessFragment() {
		return mHeadlessFragment;
	}
}
