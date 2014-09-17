package com.example.houseremote.network.dataclasses;

public class PinStatus {
	private int pinNumber;
	private int pinState;

	public PinStatus(int pinNumber, int pinState) {
		super();
		this.pinNumber = pinNumber;
		this.pinState = pinState;
	}

	public int getPinNumber() {
		return pinNumber;
	}

	public int getPinState() {
		return pinState;
	}

}
