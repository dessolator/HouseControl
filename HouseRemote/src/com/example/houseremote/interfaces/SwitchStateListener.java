package com.example.houseremote.interfaces;

import com.example.houseremote.network.NetData;

public interface SwitchStateListener{
	public void postValueChange(NetData newData);
}