package com.example.houseremote.network.dataclasses;

import com.example.houseremote.network.interfaces.Sendable;

public class InitialStateQueryPacket implements Sendable{

	@Override
	public String getSendData() {
		return ("FULLSTATUS_QUERY");
	}

}
