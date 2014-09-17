package com.example.houseremote.network.dataclasses;

import com.example.houseremote.network.interfaces.Sendable;

public class PinFlipPacket implements Sendable {

	private int pin;

	public PinFlipPacket(int pin) {
		this.pin = pin;
	}

	@Override
	public String getSendData() {
		return ("FLIP_" + pin);
	}

}
