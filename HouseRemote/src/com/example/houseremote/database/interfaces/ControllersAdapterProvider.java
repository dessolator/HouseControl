package com.example.houseremote.database.interfaces;

import com.example.houseremote.database.adapters.GridAdapter;

public interface ControllersAdapterProvider {
	GridAdapter getControllersAdapter();
	public boolean isInitialControllerDataLoaded();
	public void setInitialControllerDataLoaded(boolean initialControllerDataLoaded);

}