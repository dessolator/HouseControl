package com.example.houseremote.network;

import com.example.houseremote.interfaces.Sendable;

public class PinFlipPacket implements Sendable {

	int pin;

	public PinFlipPacket(int pin) {
		this.pin = pin;
	}

	@Override
	public String getSendData() {
		return ("FLIP_" + pin);
	}

}
