package com.example.houseremote.network.interfaces;



public interface NetworkSendController {
	void addToNetworkSender(String senderIp, Sendable switchPacket);
}