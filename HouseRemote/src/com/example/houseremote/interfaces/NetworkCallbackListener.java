package com.example.houseremote.interfaces;

import com.example.houseremote.network.PinStatusSet;

public interface NetworkCallbackListener {

	
	public void pinStateQueryComplete(PinStatusSet result);
}
