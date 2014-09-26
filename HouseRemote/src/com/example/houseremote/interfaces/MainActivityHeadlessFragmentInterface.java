package com.example.houseremote.interfaces;

import com.example.houseremote.database.interfaces.ControllersAdapterProvider;
import com.example.houseremote.database.interfaces.DatabaseHandlerProvider;
import com.example.houseremote.database.interfaces.DatabaseOperationCompleteListener;
import com.example.houseremote.database.interfaces.HousesAdapterProvider;
import com.example.houseremote.database.interfaces.RoomsAdapterProvider;
import com.example.houseremote.network.interfaces.NetworkReceiveForward;
import com.example.houseremote.network.interfaces.NetworkSendController;

public interface MainActivityHeadlessFragmentInterface extends NetworkReceiveForward,
NetworkSendController, SelectedHouseProvider, SelectedRoomProvider, RoomsAdapterProvider,
HousesAdapterProvider, ControllersAdapterProvider, HeadlessFragment,DatabaseOperationCompleteListener,DatabaseHandlerProvider{

}
