package com.example.houseremote.fragments.interfaces;


public interface RoomsActivityHeadlessProvider {

	RoomsActivityHeadlessFragmentInterface getRoomsHeadlessFragment();

	void roomSelected(long roomID);
}
