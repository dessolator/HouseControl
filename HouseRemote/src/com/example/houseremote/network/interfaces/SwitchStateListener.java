package com.example.houseremote.network.interfaces;

import com.example.houseremote.network.dataclasses.PinStatus;
import com.example.houseremote.network.dataclasses.PinStatusSet;

public interface SwitchStateListener{
	public void postValueChange(PinStatus newData);
	public void postLookupValues(PinStatusSet pinStatusSet);
}