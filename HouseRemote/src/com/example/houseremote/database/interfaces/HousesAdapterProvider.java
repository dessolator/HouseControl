package com.example.houseremote.database.interfaces;

import com.example.houseremote.database.adapters.ListAdapter;

public interface HousesAdapterProvider{
	ListAdapter  getHousesAdapter();

	boolean isInitialHouseDataLoaded();

	void setInitialHouseDataLoaded(boolean b);
	
}