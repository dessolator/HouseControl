package com.example.houseremote.database.observers;

import android.annotation.SuppressLint;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.example.houseremote.database.interfaces.ControllerDatabaseChangeListener;

public class ControllerObserver extends ContentObserver {
	
	private ControllerDatabaseChangeListener mCallback;

	public ControllerObserver(Handler handler,ControllerDatabaseChangeListener mCallback) {
		super(handler);
		this.mCallback=mCallback;
	}

	@Override
	public void onChange(boolean selfChange) {
		mCallback.controllerDatabaseChanged();
	}

	@SuppressLint("NewApi")
	@Override
	public void onChange(boolean selfChange, Uri uri) {
		mCallback.controllerDatabaseChanged();
	}


}
