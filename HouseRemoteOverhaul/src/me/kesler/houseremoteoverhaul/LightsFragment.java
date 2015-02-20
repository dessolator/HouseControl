package me.kesler.houseremoteoverhaul;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LightsFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_lights, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Read controllers from database
		// TODO Connect to Hosts and ReadInitialStates
		// TODO Show Lights
		super.onActivityCreated(savedInstanceState);
	}
	
	
	
}
