package com.example.houseremote.database.interfaces;

import com.example.houseremote.database.adapters.GridAdapter;

/**
 * An interface describing the entity that can provide a controllers grid
 * adapter.
 * 
 * @author Ivan Kesler
 *
 */
public interface ControllersAdapterProvider {

	/*
	 * NOTE: Implemented by MainActivityHeadlessFragment and
	 * ControllersActivityHeadlessFragment.
	 */

	GridAdapter getControllersAdapter();

	boolean isInitialControllerDataLoaded();

	void setInitialControllerDataLoaded(boolean initialControllerDataLoaded);

}