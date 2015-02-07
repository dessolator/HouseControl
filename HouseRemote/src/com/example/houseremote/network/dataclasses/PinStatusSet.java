package com.example.houseremote.network.dataclasses;

import java.util.ArrayList;

import com.example.houseremote.fragments.interfaces.HeadlessFragment;
import com.example.houseremote.interfaces.RunnableOnUIThread;

public class PinStatusSet implements RunnableOnUIThread {
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
	public void runOnUIThread(HeadlessFragment headlessFragment) {
		headlessFragment.postLookupValues(this);
		
	}

}
