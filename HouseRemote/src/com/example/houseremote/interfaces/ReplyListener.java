package com.example.houseremote.interfaces;

import android.database.Cursor;

public interface ReplyListener {

	void dataSetChanged(int token, Object cookie);

	void replaceCursor(Cursor cursor, Object o);

}