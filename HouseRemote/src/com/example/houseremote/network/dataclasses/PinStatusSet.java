package com.example.houseremote.network.dataclasses;

import java.util.ArrayList;

public class PinStatusSet {
//	HashMap<Integer,PinStatus> mPins;
	ArrayList<PinStatus> mPins;

	
	public PinStatusSet(){
//		mPins=new HashMap<Integer, PinStatus>();
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

	public int getState(int position) {
		return mPins.get(position).pinState;
	}

	public ArrayList<PinStatus> getArray() {
		return mPins;
	}

}
