package com.example.houseremote.network;

public class SwitchPacket {

	int pinNumber;
	PacketType mType;
	public SwitchPacket(int pin, boolean b){
		pinNumber=pin;
		if(b)
			mType=PacketType.FULLSTATEREAD;
		else
			mType=PacketType.FLIP;
	}
	public int getPin() {
		return pinNumber;
	}
	public PacketType getType(){
		return mType;
	}

}
