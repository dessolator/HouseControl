package com.example.houseremote;

import static com.example.houseremote.MainActivity.HEADLESS;
import static com.example.houseremote.MainActivity.ROOMS;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.adapters.ListAdapter;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DataBaseQueryManager;
import com.example.houseremote.fragments.HeadlessFragment;
import com.example.houseremote.fragments.RoomsFragment;
import com.example.houseremote.interfaces.QueryManagerProvider;
import com.example.houseremote.interfaces.ReplyListener;
import com.example.houseremote.interfaces.RoomSelectionListener;
import com.example.houseremote.interfaces.RoomsAdapterProvider;
import com.example.houseremote.interfaces.SelectedHouseProvider;

public class RoomsActivity extends ActionBarActivity implements RoomSelectionListener, SelectedHouseProvider, RoomsAdapterProvider, QueryManagerProvider,ReplyListener{
	
	private HeadlessFragment myHeadlessFragment;
	@SuppressWarnings("unused")
	private RoomsFragment myRoomsFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_main);

		myHeadlessFragment = acquireHeadlessFragment();
		myRoomsFragment = acquireRoomsFragmentToList();

	}

	private RoomsFragment acquireRoomsFragmentToList() {
		RoomsFragment temp = (RoomsFragment) getSupportFragmentManager().findFragmentByTag(ROOMS);
		if(temp==null){
			temp= new RoomsFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.list, temp, ROOMS).commit();
		}
		return temp;
	}
	private HeadlessFragment acquireHeadlessFragment(){
		HeadlessFragment temp = (HeadlessFragment) getSupportFragmentManager().findFragmentByTag(HEADLESS);
		if (temp == null){
			temp= new HeadlessFragment();
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
	
	
	public void roomSelected(long roomID) {
		
		Intent i = new Intent(this, ControllersActivity.class);
//		i.putExtra(DBHandler.HOUSE_ID, myHeadlessFragment.getSelectedHouse());
		i.putExtra(DBHandler.ROOM_ID, roomID);
		startActivity(i);
		return;
	}

	@Override
	public long getSelectedHouseID() {
		return myHeadlessFragment.getSelectedHouseID();
	}

	@Override
	public ListAdapter getRoomsAdapter() {
		return myHeadlessFragment.getRoomsAdapter();
	}

	@Override
	public DataBaseQueryManager getQueryManager() {
		return myHeadlessFragment.getQueryManager();
	}

	@Override
	public void dataSetChanged(int token, Object cookie) {
		myHeadlessFragment.dataSetChanged(token, cookie);
		
	}

	@Override
	public void replaceCursor(Cursor cursor, Object o) {
		myHeadlessFragment.replaceCursor(cursor, o);
		
	}

	@Override
	public boolean isInitialRoomDataLoaded() {
		return myHeadlessFragment.isInitialRoomDataLoaded();
	}

	@Override
	public void setInitialRoomDataLoaded(boolean b) {
		myHeadlessFragment.setInitialRoomDataLoaded(b);
		
	}
	
	@Override
	public boolean onNavigateUp() {
		super.onBackPressed();
		return true;
	}
}
