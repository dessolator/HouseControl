package com.example.houseremote.fragments;

import android.support.v4.app.Fragment;

public interface ActivityWithAttachedHeadlessFragment {

	void setMyHousesFragment(HousesFragment findFragmentByTag);

	void setMyRoomsFragment(RoomsFragment acquireRoomsFragmentToList);

	void setMyControllersFragment(
			ControllersFragment acquireControllersFragmentToExpanded);

	boolean isOnPhone();

	Fragment getFragment(String rooms);

	void addToExpanded(Fragment temp, String rooms);

	void addToList(Fragment temp, String rooms);

}
