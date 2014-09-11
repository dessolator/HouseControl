package com.example.houseremote.interfaces;

import com.example.houseremote.adapters.ListAdapter;

public interface RoomsAdapterProvider{
	ListAdapter  getRoomsAdapter();

	boolean isInitialRoomDataLoaded();

	void setInitialRoomDataLoaded(boolean b);
}