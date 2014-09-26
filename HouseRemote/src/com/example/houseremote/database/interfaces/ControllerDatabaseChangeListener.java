package com.example.houseremote.database.interfaces;

/**
 * An interface describing the entity listening for changes on the controller
 * database via ControllerObserver.
 * 
 * @author Ivan Kesler
 */
public interface ControllerDatabaseChangeListener {

	/*
	 * NOTE: Implemented by MainActivityHeadlessFragment and
	 * ControllersActivityHeadlessFragment.
	 */
	void controllerDatabaseChanged();

}
