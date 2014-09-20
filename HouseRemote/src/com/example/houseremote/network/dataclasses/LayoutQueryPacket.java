package com.example.houseremote.network.dataclasses;

import com.example.houseremote.network.interfaces.Sendable;

public class LayoutQueryPacket implements Sendable {

	@Override
	public String getSendData() {
		return "LAYOUT_QUERY";
	}

}
