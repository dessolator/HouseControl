package com.example.houseremote.activities.phone;

import static com.example.houseremote.activities.MainActivity.CONTROLLERS;
import static com.example.houseremote.activities.MainActivity.HEADLESS;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.R;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.fragments.ControllersActivityHeadlessFragment;
import com.example.houseremote.fragments.ControllersFragment;
import com.example.houseremote.fragments.interfaces.ControllersActivityHeadlessFragmentInterface;
import com.example.houseremote.fragments.interfaces.ControllersActivityHeadlessProvider;

/**
 * Activity displaying the elements on a controller and controlling them.
 * 
 * @author Ivan Kesler
 *
 */
public class ControllersActivity extends ActionBarActivity implements ControllersActivityHeadlessProvider{
	/*
	 * Fragments
	 */
	private ControllersActivityHeadlessFragment mHeadlessFragment;
	@SuppressWarnings("unused")
	private ControllersFragment mControllersFragment;

	/**
	 * Acquire Headless and Controllers fragment.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_main);
		mHeadlessFragment = acquireHeadlessFragment();
		mControllersFragment = acquireControllersFragment();
	}

	
	/**
	 * Standard menu inflating.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Only listen to presses of the settings button.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			// TODO Open Settings?
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Acquires the controllers fragment, if it can be recovered it is
	 * recovered, else a new one is created.
	 */
	private ControllersFragment acquireControllersFragment() {
		ControllersFragment temp = (ControllersFragment) getSupportFragmentManager().findFragmentByTag(
				CONTROLLERS);
		if (temp == null) {
			temp = new ControllersFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.list, temp, CONTROLLERS).commit();
		}
		return temp;
	}

	/** 
	 * Used to return the headless fragment and forward it to any other fragments that may need it.
	 */
	@Override
	public ControllersActivityHeadlessFragmentInterface getControllersHeadlessFragment() {
		return mHeadlessFragment;
	}

	/**
	 * Acquires the headless fragment, if it can be recovered, it is recovered,
	 * else a new one is created.
	 */
	private ControllersActivityHeadlessFragment acquireHeadlessFragment() {
		ControllersActivityHeadlessFragment temp = (ControllersActivityHeadlessFragment) getSupportFragmentManager()
				.findFragmentByTag(HEADLESS);
		if (temp == null) {
			temp = new ControllersActivityHeadlessFragment();
			temp.setSelectedRoomID(getIntent().getLongExtra(DBHandler.ROOM_ID, -1));
			getSupportFragmentManager().beginTransaction().add(temp, HEADLESS).commit();
		}
		return temp;
	}

	
	
	
	
}
