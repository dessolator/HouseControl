package com.example.houseremote.fragments;

import com.example.houseremote.network.dataclasses.ServerInfo;

import android.widget.ListAdapter;

public interface AdapterProvider {

	ListAdapter getListAdapter();

	void serverSelected(ServerInfo item);

}
