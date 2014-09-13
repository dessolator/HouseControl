package com.example.houseremote.interfaces;

import com.example.houseremote.network.PinFlipPacket;

public interface NetworkCommandListener {
//	public void startNetworkListener();
//	public void stopNetworkListener();
//	public void startNetworkSender();
//	public void stopNetworkSender();
	void addToNetworkSender(PinFlipPacket switchPacket);
}