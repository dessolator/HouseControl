package com.example.houseremote.network.interfaces;

import com.example.houseremote.network.dataclasses.PinStatus;

public interface SwitchStateListener{
	public void postValueChange(PinStatus newData);
}