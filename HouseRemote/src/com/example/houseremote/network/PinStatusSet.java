package com.example.houseremote.network;

import java.util.ArrayList;

public class PinStatusSet {
	ArrayList<PinStatus> mPins;

	
	public PinStatusSet(){
		mPins=new ArrayList<PinStatus>();
	}
	
	public void add(int pinNumber,int pinState) {
		mPins.add(new PinStatus(pinNumber, pinState));		
	}

	public int size() {
		return mPins.size();
	}

	public PinStatus get(int i) {
		return mPins.get(i);
	}

}
