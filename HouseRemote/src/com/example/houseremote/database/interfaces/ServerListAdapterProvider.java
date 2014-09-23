package com.example.houseremote.database.interfaces;

import com.example.houseremote.network.dataclasses.ServerInfo;

import android.widget.ListAdapter;

public interface ServerListAdapterProvider {

	ListAdapter getListAdapter();

	void serverSelected(ServerInfo item);

}
