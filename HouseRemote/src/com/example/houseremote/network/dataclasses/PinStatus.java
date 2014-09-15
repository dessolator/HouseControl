package com.example.houseremote.network.dataclasses;

public class PinStatus {
	public int pinNumber;
	public int pinState;
	
	public PinStatus(int pinNumber, int pinState) {
		super();
		this.pinNumber = pinNumber;
		this.pinState = pinState;
	}

}
