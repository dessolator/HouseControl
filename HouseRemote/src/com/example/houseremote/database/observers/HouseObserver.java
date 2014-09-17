package com.example.houseremote.database.observers;

import com.example.houseremote.database.interfaces.HouseDatabaseChangeListener;

import android.annotation.SuppressLint;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class HouseObserver extends ContentObserver {
	
	private HouseDatabaseChangeListener mCallback;

	public HouseObserver(Handler handler,HouseDatabaseChangeListener mCallback) {
		super(handler);
		this.mCallback=mCallback;
	}

	@Override
	public void onChange(boolean selfChange) {
		mCallback.houseDatabaseChanged();
		mCallback.roomDatabaseChanged();
		mCallback.controllerDatabaseChanged();
	}

	@SuppressLint("NewApi")
	@Override
	public void onChange(boolean selfChange, Uri uri) {
		mCallback.houseDatabaseChanged();
		mCallback.roomDatabaseChanged();
		mCallback.controllerDatabaseChanged();
	}


}
