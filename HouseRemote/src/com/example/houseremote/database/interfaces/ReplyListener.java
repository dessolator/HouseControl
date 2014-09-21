package com.example.houseremote.database.interfaces;

import android.database.Cursor;


public interface ReplyListener {
	
	void replaceCursor(Cursor cursor, Object cookie);
	
	void onControllerDataChanged();

	void onHouseDataChanged();

	void onRoomDataChanged();

}