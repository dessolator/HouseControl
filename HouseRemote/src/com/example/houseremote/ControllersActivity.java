package com.example.houseremote;

import static com.example.houseremote.MainActivity.CONTROLLERS;
import static com.example.houseremote.MainActivity.HEADLESS;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.adapters.GridAdapter;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DataBaseQueryManager;
import com.example.houseremote.fragments.ControllersFragment;
import com.example.houseremote.fragments.HeadlessFragment;
import com.example.houseremote.interfaces.ControllersAdapterProvider;
import com.example.houseremote.interfaces.NetworkCommandListener;
import com.example.houseremote.interfaces.QueryManagerProvider;
import com.example.houseremote.interfaces.ReplyListener;
import com.example.houseremote.interfaces.SelectedHouseProvider;
import com.example.houseremote.interfaces.SelectedRoomProvider;
import com.example.houseremote.network.PinFlipPacket;

/**
 * Activity displaying the elements on a controller and controlling them.
 * @author Ivan Kesler
 *
 */
public class ControllersActivity extends ActionBarActivity implements ReplyListener, SelectedHouseProvider,
		SelectedRoomProvider, ControllersAdapterProvider, QueryManagerProvider, NetworkCommandListener {

	/*
	 * Fragments
	 */
	@SuppressWarnings("unused")
	private ControllersFragment myControllersFragment;
	private HeadlessFragment myHeadlessFragment;

	/**
	 * Acquire Headless and Controllers fragment.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_main);
		myHeadlessFragment = acquireHeadlessFragment();
		myControllersFragment = acquireControllersFragment();
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
			getSupportFragmentManager().beginTransaction().add(R.id.list, temp, CONTROLLERS)
					.commit();
		}
		return temp;
	}

	/**
	 * Acquires the headless fragment, if it can be recovered, it is recovered,
	 * else a new one is created.
	 */
	private HeadlessFragment acquireHeadlessFragment() {
		HeadlessFragment temp = (HeadlessFragment) getSupportFragmentManager().findFragmentByTag(HEADLESS);
		if (temp == null) {
			temp = new HeadlessFragment();
//			temp.setSelectedHouseID(getIntent().getIntExtra(DBHandler.HOUSE_ID,-1));
			temp.setSelectedRoomID(getIntent().getLongExtra(DBHandler.ROOM_ID,-1));
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

	/*
	 * Passalong functions, getters and setters.
	 */
	@Override
	public void dataSetChanged(int token, Object cookie) {
		myHeadlessFragment.dataSetChanged(token, cookie);
	}

	@Override
	public void replaceCursor(Cursor cursor, Object o) {
		myHeadlessFragment.replaceCursor(cursor, o);

	}

	@Override
	public long getSelectedHouseID() {
		return myHeadlessFragment.getSelectedHouseID();
	}

	@Override
	public long getSelectedRoomID() {
		return myHeadlessFragment.getSelectedRoomID();
	}

//	@Override
//	public String getSelectedRoomIp() {
//		return myHeadlessFragment.getSelectedRoomIp();
//	}

	@Override
	public GridAdapter getControllersAdapter() {
		return myHeadlessFragment.getControllersAdapter();
	}

	@Override
	public DataBaseQueryManager getQueryManager() {
		return myHeadlessFragment.getQueryManager();
	}

	@Override
	public void addToNetworkSender(PinFlipPacket switchPacket) {
		myHeadlessFragment.addToNetworkSender(switchPacket);
	}

	@Override
	public boolean isInitialControllerDataLoaded() {
		return myHeadlessFragment.isInitialControllerDataLoaded();
	}

	@Override
	public void setInitialControllerDataLoaded(boolean initialControllerDataLoaded) {
		myHeadlessFragment.setInitialControllerDataLoaded(initialControllerDataLoaded);

	}
	
	@Override
	public boolean onNavigateUp() {
		super.onBackPressed();
		return true;
	}
}
