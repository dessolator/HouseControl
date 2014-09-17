package com.example.houseremote.database;

import java.lang.ref.WeakReference;

import com.example.houseremote.database.interfaces.DBInsertResponder;
import com.example.houseremote.database.interfaces.ReplyListener;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;

public class DataBaseQueryManager extends AsyncQueryHandler {

	private WeakReference<ReplyListener> mListener;

	public DataBaseQueryManager(ContentResolver cr, ReplyListener rl) {
		super(cr);
		mListener = new WeakReference<ReplyListener>(rl);
	}

	@Override
	protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
		super.onQueryComplete(token, cookie, cursor);
		if (mListener.get() != null)
			mListener.get().replaceCursor(cursor,cookie);
		else
			cursor.close();
	}

	@Override
	protected void onInsertComplete(int token, Object cookie, Uri uri) {
		if (mListener.get() != null)
			if(cookie !=null){
				((DBInsertResponder)cookie).uponInsertFinished(ContentUris.parseId(uri));
				}
		super.onInsertComplete(token, cookie, uri);
	}


}
