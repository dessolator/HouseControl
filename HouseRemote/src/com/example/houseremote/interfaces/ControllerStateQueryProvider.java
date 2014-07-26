package com.example.houseremote.interfaces;

import com.example.houseremote.network.ControllerStateQuery;


public interface ControllerStateQueryProvider{
	public ControllerStateQuery getStateQuery();
	void restartFullStateRead(String ip, ControllerStateQueryListener l);
}