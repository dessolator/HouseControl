package com.example.houseremote.database.interfaces;

import android.database.Cursor;


/**
 * Interface describing an entity that listens to database operation completions.
 * 
 * @author Ivan Kesler
 *
 */
public interface DatabaseOperationCompleteListener {
	
	void onQueryFinished(Cursor cursor, Object cookie);
	
//	void onControllerDataChanged();
//
//	void onHouseDataChanged();
//
//	void onRoomDataChanged();

	void onInsertFinished(long parseId);

}