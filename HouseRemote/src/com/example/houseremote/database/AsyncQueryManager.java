package com.example.houseremote.database;

import java.lang.ref.WeakReference;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class AsyncQueryManager extends AsyncQueryHandler {
	
	
	WeakReference<ReplyListener> mListener;
	
	public interface ReplyListener{
		
		void dataSetChanged();
		
		void replaceCursor(Cursor cursor, int token);}
	
	public AsyncQueryManager(ContentResolver cr, ReplyListener rl) {
		super(cr);
		mListener=new WeakReference<AsyncQueryManager.ReplyListener>(rl);
	}

	@Override
	protected void onDeleteComplete(int token, Object cookie, int result) {
		if(mListener.get()!=null)
		mListener.get().dataSetChanged();//TODO BRUTAL hardcoding quite possibly Broadcast receivers might be a more elegant solution
		super.onDeleteComplete(token, cookie, result);
	}
	
	@Override
	protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
		super.onQueryComplete(token, cookie, cursor);
		if(mListener.get()!=null)
			mListener.get().replaceCursor(cursor, token);
		else
			cursor.close();
	}
	
	@Override
	protected void onInsertComplete(int token, Object cookie, Uri uri) {
		if(mListener.get()!=null)
		mListener.get().dataSetChanged();//TODO BRUTAL hardcoding quite possibly Broadcast receivers might be a more elegant solution
		super.onInsertComplete(token, cookie, uri);
	}
	
	@Override
	protected void onUpdateComplete(int token, Object cookie, int result) {
		if(mListener.get()!=null)
		mListener.get().dataSetChanged();//TODO BRUTAL hardcoding quite possibly Broadcast receivers might be a more elegant solution
		super.onUpdateComplete(token, cookie, result);
	}

}
