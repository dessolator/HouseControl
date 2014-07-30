package com.example.houseremote.interfaces;

import com.example.houseremote.network.PinStatus;

public interface SwitchStateListener{
	public void postValueChange(PinStatus newData);
}