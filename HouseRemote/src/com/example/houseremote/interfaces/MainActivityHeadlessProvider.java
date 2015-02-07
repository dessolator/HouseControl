package com.example.houseremote.interfaces;

import com.example.houseremote.fragments.interfaces.MainActivityHeadlessFragmentInterface;


public interface MainActivityHeadlessProvider {

	MainActivityHeadlessFragmentInterface getMainHeadlessFragment();
	void houseSelected(long houseID);
}
