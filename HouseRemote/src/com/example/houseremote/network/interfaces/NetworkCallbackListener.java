package com.example.houseremote.network.interfaces;

import com.example.houseremote.network.dataclasses.PinStatusSet;

public interface NetworkCallbackListener {
	public void pinStateQueryComplete(PinStatusSet result);
}
