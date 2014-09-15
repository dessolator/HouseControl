package com.example.houseremote.network.interfaces;

import com.example.houseremote.network.dataclasses.PinStatusSet;

public interface UILockupListener {

	void postLookupValues(PinStatusSet pinStatusSet);


}
