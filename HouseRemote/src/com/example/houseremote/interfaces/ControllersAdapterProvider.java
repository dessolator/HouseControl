package com.example.houseremote.interfaces;

import com.example.houseremote.adapters.GridAdapter;

public interface ControllersAdapterProvider {
	GridAdapter getControllersAdapter();
	public boolean isInitialControllerDataLoaded();
	public void setInitialControllerDataLoaded(boolean initialControllerDataLoaded);

}