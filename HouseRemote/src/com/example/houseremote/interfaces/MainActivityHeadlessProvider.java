package com.example.houseremote.interfaces;


public interface MainActivityHeadlessProvider {

	MainActivityHeadlessFragmentInterface getMainHeadlessFragment();
	void houseSelected(long houseID);
}
