package com.example.houseremote.interfaces;

import com.example.houseremote.network.PinStatusSet;

public interface ControllerStateQueryListener{
	void onStateLoadFinished(PinStatusSet ps);
}