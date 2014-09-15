package com.example.houseremote.network.interfaces;



public interface NetworkCommandListener {
//	public void startNetworkListener();
//	public void stopNetworkListener();
//	public void startNetworkSender();
//	public void stopNetworkSender();
	void addToNetworkSender(String senderIp, Sendable switchPacket);
}