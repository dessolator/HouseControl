package com.example.houseremote.interfaces;


public interface RoomsActivityHeadlessProvider {

	RoomsActivityHeadlessFragmentInterface getRoomsHeadlessFragment();

	void roomSelected(long roomID);
}
