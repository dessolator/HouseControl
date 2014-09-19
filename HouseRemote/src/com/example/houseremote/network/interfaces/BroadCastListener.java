package com.example.houseremote.network.interfaces;

import android.content.Context;

import com.example.houseremote.network.dataclasses.ServerInfo;

public interface BroadCastListener {

	void serverFound(ServerInfo serverInfo);

	Context getContext();
}
