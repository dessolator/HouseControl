package com.example.houseremote.network.dataclasses;

import java.util.ArrayList;

import com.example.houseremote.fragments.PrimaryHeadlessFragment;
import com.example.houseremote.interfaces.UIReadable;

public class PinStatusSet implements UIReadable {
	private ArrayList<PinStatus> mPins;

	
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

	public int getState(int position) {
		return mPins.get(position).getPinState();
	}

	public ArrayList<PinStatus> getArray() {
		return mPins;
	}


	@Override
	public void executeNeededCode(PrimaryHeadlessFragment headlessFragment) {
		headlessFragment.postLookupValues(this);
		
	}

}
