package me.kesler.houseremoteoverhaul;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public class HeadlessFragment extends Fragment {

	private FragmentType currentFragment = FragmentType.LIGHTS;
	private boolean[] loadedInitialData = new boolean[FragmentType.values().length];
	private AsyncDataLoader mAsyncDataLoader;
	private GridAdapter lightsAdapter = new GridAdapter();
	private GridAdapter switchesAdapter = new GridAdapter();
	private GridAdapter mediaAdapter = new GridAdapter();
	private GridAdapter appliancesAdapter = new GridAdapter();
	private GridAdapter roomsAdapter = new GridAdapter();
	private String[] serverIp;
	private AsyncDataLoader mAsyncNetworkLoader;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}
	public FragmentType getCurrentFragment() {
		return currentFragment;
	}

	public void fragmentChanged(FragmentType toFragment) {
		currentFragment = toFragment;
		if(!loadedInitialData [toFragment.getValue()]){
			loadedInitialData[toFragment.getValue()] = true;
			mAsyncDataLoader.startLoadingData(toFragment, serverIp);
			return;
		}
		mAsyncNetworkLoader.startLoadingData(toFragment, serverIp);
		
	}
	
	public void onNetworkLoaded(){
		
	}
	
	public void onDataLoaded(FragmentType toFragment, Cursor newData) {
		Cursor temp;
		switch(toFragment){
		case APPLIANCES:
			temp = appliancesAdapter.swapCursor(newData);
			if (temp != null)
				temp.close();
			break;
		case LIGHTS:
			temp = lightsAdapter.swapCursor(newData);
			if (temp != null)
				temp.close();
			break;
		case MEDIA:
			temp = mediaAdapter.swapCursor(newData);
			if (temp != null)
				temp.close();
			break;
		case ROOMS:
			temp = roomsAdapter.swapCursor(newData);
			if (temp != null)
				temp.close();
			break;
		case SWITCHES:
			temp = switchesAdapter.swapCursor(newData);
			if (temp != null)
				temp.close();
			break;
		default:
			Log.e("YOU DONE FUCKED UP", "YOU DONE FUCKED UP");	
		}
		mAsyncNetworkLoader.startLoadingData(toFragment, serverIp);
		
	}


}
