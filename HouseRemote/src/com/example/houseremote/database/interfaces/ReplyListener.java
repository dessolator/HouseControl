package com.example.houseremote.database.interfaces;

import android.database.Cursor;


public interface ReplyListener {
	
	void replaceCursor(Cursor cursor, Object cookie);
	
	void reloadControllerData();

	void reloadHouseData();

	void reloadRoomData();

}