package com.example.houseremote.network.dataclasses;

import com.example.houseremote.interfaces.HeadlessFragment;
import com.example.houseremote.interfaces.RunnableOnUIThread;

public class PinStatus implements RunnableOnUIThread {
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

	@Override
	public void runOnUIThread(HeadlessFragment headlessFragment) {
		headlessFragment.postValueChange(this);
		
	}

	

}
