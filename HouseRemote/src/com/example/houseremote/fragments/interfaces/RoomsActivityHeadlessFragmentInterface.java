package com.example.houseremote.fragments.interfaces;

import com.example.houseremote.database.interfaces.DatabaseHandlerProvider;
import com.example.houseremote.database.interfaces.DatabaseOperationCompleteListener;
import com.example.houseremote.database.interfaces.RoomsAdapterProvider;
import com.example.houseremote.interfaces.SelectedHouseProvider;

public interface RoomsActivityHeadlessFragmentInterface extends RoomsAdapterProvider,SelectedHouseProvider,DatabaseOperationCompleteListener, DatabaseHandlerProvider{

//	public void onControllerDataChanged();
	public void onRoomDataChanged();
}
