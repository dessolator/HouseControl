package com.example.houseremote.database.interfaces;

import android.database.Cursor;


public interface DatabaseOperationCompleteListener {
	
	void onQueryFinished(Cursor cursor, Object cookie);
	
	void onControllerDataChanged();

	void onHouseDataChanged();

	void onRoomDataChanged();

	void onInsertFinished(long parseId);

}