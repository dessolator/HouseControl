package com.example.houseremote.network;

import com.example.houseremote.interfaces.Sendable;

public class InitialStateQueryPacket implements Sendable{

	@Override
	public String getSendData() {
		return ("FULLSTATUS_ASDASD");//TODO check in status
	}

}
