package com.example.houseremote.activities;

import static com.example.houseremote.activities.MainActivity.CONTROLLERS;
import static com.example.houseremote.activities.MainActivity.HEADLESS;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.R;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.fragments.ControllersFragment;
import com.example.houseremote.fragments.PrimaryHeadlessFragment;
import com.example.houseremote.interfaces.HeadlessFragment;
import com.example.houseremote.interfaces.HeadlessProvider;

/**
 * Activity displaying the elements on a controller and controlling them.
 * 
 * @author Ivan Kesler
 *
 */
public class ControllersActivity extends ActionBarActivity implements HeadlessProvider{
	/*
	 * Fragments
	 */
	private PrimaryHeadlessFragment mHeadlessFragment;

	/**
	 * Acquire Headless and Controllers fragment.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_main);
		mHeadlessFragment = acquireHeadlessFragment();
		acquireControllersFragment();
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
	 * Acquires the headless fragment, if it can be recovered, it is recovered,
	 * else a new one is created.
	 */
	private PrimaryHeadlessFragment acquireHeadlessFragment() {
		PrimaryHeadlessFragment temp = (PrimaryHeadlessFragment) getSupportFragmentManager()
				.findFragmentByTag(HEADLESS);
		if (temp == null) {
			temp = new PrimaryHeadlessFragment();
			temp.setSelectedRoomID(getIntent().getLongExtra(DBHandler.ROOM_ID, -1));
			getSupportFragmentManager().beginTransaction().add(temp, HEADLESS).commit();
		}
		return temp;
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

	@Override
	public HeadlessFragment getHeadlessFragment() {
		return mHeadlessFragment;
	}
	
	
	
}
