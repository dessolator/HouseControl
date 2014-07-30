package com.example.houseremote.interfaces;

import com.example.houseremote.network.PinStatusSet;

public interface UILockupListener {

	void postLookupValues(PinStatusSet pinStatusSet);

}
