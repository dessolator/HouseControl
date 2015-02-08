package com.example.houseremote.fragments.interfaces;



public interface MainActivityHeadlessProvider {

	MainActivityHeadlessFragmentInterface getMainHeadlessFragment();
	void houseSelected(long houseID);
}
