package com.example.houseremote.database.interfaces;

import com.example.houseremote.database.DataBaseAsyncQueryHandler;

import android.database.Cursor;


public interface DatabaseOperationCompleteListener {
	
	void onQueryFinished(Cursor cursor, Object cookie);
	
	void onControllerDataChanged();

	void onHouseDataChanged();

	void onRoomDataChanged();

	void onInsertFinished(long parseId);
	
	DataBaseAsyncQueryHandler getQueryManager();

}