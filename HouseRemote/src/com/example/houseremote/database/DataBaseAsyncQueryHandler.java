package com.example.houseremote.database;

import java.lang.ref.WeakReference;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;

import com.example.houseremote.database.interfaces.DatabaseOperationCompleteListener;

public class DataBaseAsyncQueryHandler extends AsyncQueryHandler {

	private WeakReference<DatabaseOperationCompleteListener> mListener;

	/**
	 * Constructor for the DataBaseAsyncQueryHandler class
	 * @param cr	ContentResolver to which the DataBaseAsyncQueryHandler is connected.
	 * @param rl	The entity listening to underlying database changes.
	 */
	public DataBaseAsyncQueryHandler(ContentResolver cr, DatabaseOperationCompleteListener rl) {
		super(cr);
		mListener = new WeakReference<DatabaseOperationCompleteListener>(rl);
	}


	@Override
	protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
		super.onQueryComplete(token, cookie, cursor);
		if (mListener.get() != null)
			mListener.get().onQueryFinished(cursor,(CursorAdapter)cookie);
		else
			cursor.close();
	}

	@Override
	protected void onInsertComplete(int token, Object cookie, Uri uri) {
		if (mListener.get() != null)
				mListener.get().onInsertFinished(ContentUris.parseId(uri), token);//TODO
		super.onInsertComplete(token, cookie, uri);
	}


}
