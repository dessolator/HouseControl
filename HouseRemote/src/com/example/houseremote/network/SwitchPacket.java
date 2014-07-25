package com.example.houseremote.network;

public class SwitchPacket {

	int pinNumber;
	
	public SwitchPacket(int pin){
		pinNumber=pin;
	}
	public int getPin() {
		return pinNumber;
	}

}
