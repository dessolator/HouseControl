package com.example.houseremote;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.houseremote.adapters.GridAdapter;
import com.example.houseremote.adapters.ListAdapter;
import com.example.houseremote.database.AsyncQueryManager;
import com.example.houseremote.database.AsyncQueryManager.ReplyListener;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.fragments.ControllersFragment;
import com.example.houseremote.fragments.ControllersFragment.ControllersAdapterProvider;
import com.example.houseremote.fragments.ControllersFragment.SelectedRoomProvider;
import com.example.houseremote.fragments.HeadlessFragment;
import com.example.houseremote.fragments.HousesFragment;
import com.example.houseremote.fragments.HousesFragment.HouseSelectionListener;
import com.example.houseremote.fragments.HousesFragment.HousesAdapterProvider;
import com.example.houseremote.fragments.RoomsFragment;
import com.example.houseremote.fragments.RoomsFragment.QueryManagerProvider;
import com.example.houseremote.fragments.RoomsFragment.RoomSelectionListener;
import com.example.houseremote.fragments.RoomsFragment.RoomsAdapterProvider;
import com.example.houseremote.fragments.RoomsFragment.SelectedHouseProvider;

public class MainActivity extends ActionBarActivity implements ReplyListener, RoomSelectionListener, HouseSelectionListener, ControllersAdapterProvider, RoomsAdapterProvider,HousesAdapterProvider, QueryManagerProvider, SelectedHouseProvider,SelectedRoomProvider{

	private static final String HEADLESS = "headless";
	private static final String HOUSES = "houses";
	private static final String ROOMS = "rooms";
	private static final String CONTROLLERS = "controllers";
	HeadlessFragment myHeadlessFragment;
	HousesFragment myHousesFragment;
	RoomsFragment myRoomsFragment;
	ControllersFragment myControllersFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myHeadlessFragment=(HeadlessFragment)getSupportFragmentManager().findFragmentByTag(HEADLESS);
		if(myHeadlessFragment==null){
			myHeadlessFragment=new HeadlessFragment();
		}
		
		getSupportFragmentManager().beginTransaction().add(myHeadlessFragment, HEADLESS).commit();
		
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			myHousesFragment = (HousesFragment)getSupportFragmentManager().findFragmentByTag(HOUSES);
			if(myHousesFragment==null){
				myHousesFragment=new HousesFragment();
			}
			
			ft.add(R.id.list, myHousesFragment);

			if (findViewById(R.id.expanded) != null) {//if on tablet
				if (myHeadlessFragment.getSelectedHouse() != null) {
					
					RoomsFragment myRoomsFragment =(RoomsFragment) getSupportFragmentManager().findFragmentByTag(ROOMS);
					if(myRoomsFragment==null){
						myRoomsFragment= new RoomsFragment();
					}
					Bundle b = new Bundle();//TODO might be unnecessary
					b.putString(DBHandler.HOUSE_NAME, myHeadlessFragment.getSelectedHouse());
					myRoomsFragment.setArguments(b);
					ft.add(R.id.expanded, myRoomsFragment);
					}
			}
			else{//if on phone
				
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void roomSelected(String roomName, String roomIp) {
	
		if (findViewById(R.id.expanded) == null){	//if on phone start the new activity passing roomName and houseName as args
			Intent i= new Intent(this, ControllersActivity.class);
			i.putExtra(DBHandler.HOUSE_NAME, myHeadlessFragment.getSelectedHouse());
			i.putExtra(DBHandler.ROOM_NAME, roomName);
			i.putExtra(DBHandler.CONTROLLER_IP, roomIp);
			startActivity(i);
			return;
		}

		if (myControllersFragment==null) {		//check to see if the controller fragment is even loaded if not make a new one and pass it args
			myHeadlessFragment.setSelectedRoomWithIp(roomName, roomIp);

			myControllersFragment =(ControllersFragment) getSupportFragmentManager().findFragmentByTag(CONTROLLERS);
			if(myControllersFragment==null){
				myControllersFragment= new ControllersFragment();
			}
	

			Bundle b = new Bundle();
			b.putString(DBHandler.HOUSE_NAME, myHeadlessFragment.getSelectedHouse());
			b.putString(DBHandler.ROOM_NAME, myHeadlessFragment.getSelectedRoom());
			b.putString(DBHandler.CONTROLLER_IP, myHeadlessFragment.getSelectedRoomIp());
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

		if(myHeadlessFragment.getSelectedRoom()!=roomName){		//if a new room is selected change data in the controllers fragment
			myHeadlessFragment.setSelectedRoomWithIp(roomName, roomIp);
			myControllersFragment.replaceData(myHeadlessFragment.getSelectedHouse(), roomName, roomIp);
			myHeadlessFragment.dataSetChanged(2,myHeadlessFragment.getControllersAdapter());
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
			myHeadlessFragment.setSelectedHouse(houseName);
			myRoomsFragment=new RoomsFragment();
			Bundle b= new Bundle();
			b.putString(DBHandler.HOUSE_NAME, myHeadlessFragment.getSelectedHouse());
			myRoomsFragment.setArguments(b);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.expanded, myRoomsFragment);
			ft.commit();
			return;
		}
		if(myHeadlessFragment.getSelectedHouse()!=houseName){
			myHeadlessFragment.setSelectedHouse(houseName);
			myRoomsFragment.replaceData(houseName);
			myHeadlessFragment.dataSetChanged(1,myHeadlessFragment.getRoomsAdapter());
			return;
		}
		
	}

	@Override
	public GridAdapter getControllersAdapter() {
		return myHeadlessFragment.getControllersAdapter();
	}

	@Override
	public ListAdapter getHousesAdapter() {
		return myHeadlessFragment.getHousesAdapter();
	}

	@Override
	public ListAdapter getRoomsAdapter() {
		return myHeadlessFragment.getRoomsAdapter();
	}

	@Override
	public String getSelectedRoom() {
		return myHeadlessFragment.getSelectedRoom();
	}

	@Override
	public String getSelectedRoomIp() {
		return myHeadlessFragment.getSelectedRoomIp();
	}

	@Override
	public String getSelectedHouse() {
		return myHeadlessFragment.getSelectedHouse();
	}

	@Override
	public AsyncQueryManager getQueryManager() {
		return myHeadlessFragment.getQueryManager();
	}

	@Override
	public void dataSetChanged(int token, Object cookie) {
		myHeadlessFragment.dataSetChanged(token, cookie);
		
	}

	@Override
	public void replaceCursor(Cursor cursor, Object cookie) {
		myHeadlessFragment.replaceCursor(cursor, cookie);
		
	}

}
