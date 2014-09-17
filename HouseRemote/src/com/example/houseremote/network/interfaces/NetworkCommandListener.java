package com.example.houseremote.network.interfaces;



public interface NetworkCommandListener {
	void addToNetworkSender(String senderIp, Sendable switchPacket);
}