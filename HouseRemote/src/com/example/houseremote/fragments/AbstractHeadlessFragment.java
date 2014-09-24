package com.example.houseremote.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.houseremote.database.DataBaseAsyncQueryHandler;
import com.example.houseremote.database.interfaces.DatabaseOperationCompleteListener;

public abstract class AbstractHeadlessFragment extends Fragment  implements DatabaseOperationCompleteListener{

	protected DataBaseAsyncQueryHandler queryManager;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		queryManager = new DataBaseAsyncQueryHandler(getActivity().getContentResolver(), this);
		setRetainInstance(true);

	}


	public DataBaseAsyncQueryHandler getQueryManager() {
		return queryManager;
	}


}
