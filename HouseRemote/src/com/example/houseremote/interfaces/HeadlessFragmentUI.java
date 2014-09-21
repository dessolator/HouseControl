package com.example.houseremote.interfaces;

import com.example.houseremote.network.dataclasses.PinStatus;
import com.example.houseremote.network.dataclasses.PinStatusSet;

public interface HeadlessFragmentUI {

	void postValueChange(PinStatus pinStatus);

	void postLookupValues(PinStatusSet pinStatusSet);

}
