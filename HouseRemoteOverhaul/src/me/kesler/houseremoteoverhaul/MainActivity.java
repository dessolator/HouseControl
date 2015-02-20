package me.kesler.houseremoteoverhaul;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity implements OnItemClickListener {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private Toolbar mToolbar;

	private HeadlessFragment mHeadlessFragment;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		mHeadlessFragment = getHeadlessFragment();
		initActionBar();
		initDrawer();
//		restorePreviousState();// TODO might not be necessary

	}


	private void initDrawer() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.drawer);
		mDrawerLayout.setDrawerListener(createDrawerToggle());
		ListAdapter adapter = (ListAdapter) (new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getResources()
						.getStringArray(R.array.nav_items)));
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mDrawerLayout.closeDrawer(mDrawerList);
		switch(position){
		case 0:
			showLightsFragment();
			break;
		case 1:
			showSwitchesFragment();
			break;
		case 2:
			showMediaFragment();
			break;
		case 3:
			showAppliancesFragment();
			break;
		case 4:
			showRoomsFragment();
			break;
		case 5:
			//TODO SETTINGS ACTIVITY
			break;
		case 6:
			//TODO ABOUT ACTIVITY
			break;
		default:
			break;
		}
	
	}


	private void showRoomsFragment() {
		if(mHeadlessFragment.getCurrentFragment() == FragmentType.ROOMS) return;
		LightsFragment temp = (LightsFragment) getSupportFragmentManager().findFragmentByTag("rooms");
		if(temp==null){
			temp= new RoomsFragment();
		}
		FragmentManager fragmentManager= getSupportFragmentManager();
		FragmentTransaction ftx = fragmentManager.beginTransaction();
		ftx.replace(R.id.main_content, temp);
		ftx.commit();
		mHeadlessFragment.fragmentChanged(FragmentType.ROOMS);
	}


	private void showAppliancesFragment() {
		if(mHeadlessFragment.getCurrentFragment() == FragmentType.APPLIANCES) return;
		LightsFragment temp = (LightsFragment) getSupportFragmentManager().findFragmentByTag("appliances");
		if(temp==null){
			temp= new AppliancesFragment();
		}
		FragmentManager fragmentManager= getSupportFragmentManager();
		FragmentTransaction ftx= fragmentManager.beginTransaction();
		ftx.replace(R.id.main_content, temp);
		ftx.commit();
		mHeadlessFragment.fragmentChanged(FragmentType.APPLIANCES);
	}


	private void showMediaFragment() {
		if(mHeadlessFragment.getCurrentFragment() == FragmentType.MEDIA) return;
		LightsFragment temp = (LightsFragment) getSupportFragmentManager().findFragmentByTag("media");
		if(temp==null){
			temp= new MediaFragment();
		}
		FragmentManager fragmentManager= getSupportFragmentManager();
		FragmentTransaction ftx= fragmentManager.beginTransaction();
		ftx.replace(R.id.main_content, temp);
		ftx.commit();
		mHeadlessFragment.fragmentChanged(FragmentType.MEDIA);
	}


	private void showSwitchesFragment() {
		if(mHeadlessFragment.getCurrentFragment() == FragmentType.SWITCHES) return;
		LightsFragment temp = (LightsFragment) getSupportFragmentManager().findFragmentByTag("switches");
		if(temp==null){
			temp= new SwitchesFragment();
		}
		FragmentManager fragmentManager= getSupportFragmentManager();
		FragmentTransaction ftx= fragmentManager.beginTransaction();
		ftx.replace(R.id.main_content, temp);
		ftx.commit();
		mHeadlessFragment.fragmentChanged(FragmentType.SWITCHES);
	}


	private void showLightsFragment() {
		if(mHeadlessFragment.getCurrentFragment() == FragmentType.LIGHTS) return;
		LightsFragment temp = (LightsFragment) getSupportFragmentManager().findFragmentByTag("lights");
		if(temp==null){
			temp= new LightsFragment();
		}
		FragmentManager fragmentManager= getSupportFragmentManager();
		FragmentTransaction ftx= fragmentManager.beginTransaction();
		ftx.replace(R.id.main_content, temp);
		ftx.commit();
		mHeadlessFragment.fragmentChanged(FragmentType.LIGHTS);
	}

	private DrawerListener createDrawerToggle() {
		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				mToolbar,
				R.string.drawer_open, R.string.drawer_close) {

			@Override
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}

			@Override
			public void onDrawerStateChanged(int state) {
			}
		};
		mDrawerToggle.syncState();
		return mDrawerToggle;
	}

	private void initActionBar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
	}

	private HeadlessFragment getHeadlessFragment() {
		HeadlessFragment temp = (HeadlessFragment) getSupportFragmentManager()
				.findFragmentByTag("headless");
		if (temp == null) {
			temp = new HeadlessFragment();
			getSupportFragmentManager().beginTransaction().add(temp, "headless").commit();
		}
		return temp;
	}

}
