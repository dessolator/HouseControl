package com.example.houseremote.database.interfaces;

import com.example.houseremote.database.adapters.ListAdapter;

public interface RoomsAdapterProvider{
	ListAdapter  getRoomsAdapter();

	boolean isInitialRoomDataLoaded();

	void setInitialRoomDataLoaded(boolean b);
}