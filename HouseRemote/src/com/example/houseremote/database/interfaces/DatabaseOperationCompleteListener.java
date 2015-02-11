package com.example.houseremote.database.interfaces;

import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;


/**
 * Interface describing an entity that listens to database operation completions.
 * 
 * @author Ivan Kesler
 *
 */
public interface DatabaseOperationCompleteListener {
	
	void onQueryFinished(Cursor cursor, CursorAdapter cookie);
	
//	void onControllerDataChanged();
//
//	void onHouseDataChanged();
//
//	void onRoomDataChanged();

	void onInsertFinished(long parseId, int token);

}