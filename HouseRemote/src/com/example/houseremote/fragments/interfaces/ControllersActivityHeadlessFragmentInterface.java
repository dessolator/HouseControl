package com.example.houseremote.fragments.interfaces;

import com.example.houseremote.database.interfaces.ControllersAdapterProvider;
import com.example.houseremote.database.interfaces.DatabaseHandlerProvider;
import com.example.houseremote.database.interfaces.DatabaseOperationCompleteListener;
import com.example.houseremote.interfaces.SelectedRoomProvider;
import com.example.houseremote.network.interfaces.NetworkReceiveForward;
import com.example.houseremote.network.interfaces.NetworkSendController;

public interface ControllersActivityHeadlessFragmentInterface extends NetworkReceiveForward,
		NetworkSendController, ControllersAdapterProvider, HeadlessFragment,
		DatabaseOperationCompleteListener, SelectedRoomProvider, DatabaseHandlerProvider {

}
