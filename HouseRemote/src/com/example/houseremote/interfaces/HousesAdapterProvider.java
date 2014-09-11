package com.example.houseremote.interfaces;

import com.example.houseremote.adapters.ListAdapter;

public interface HousesAdapterProvider{
	ListAdapter  getHousesAdapter();

	boolean isInitialHouseDataLoaded();

	void setInitialHouseDataLoaded(boolean b);
	
}