package com.example.houseremote.fragments;

import java.lang.ref.WeakReference;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class AsyncQueryManager extends AsyncQueryHandler {
	
	
	WeakReference<ReplyListener> mListener;
	
	public interface ReplyListener{
		
		void dataSetChanged();
		
		void replaceCursor(Cursor cursor);}
	
	public AsyncQueryManager(ContentResolver cr, ReplyListener rl) {
		super(cr);
		mListener=new WeakReference<AsyncQueryManager.ReplyListener>(rl);
	}

	@Override
	protected void onDeleteComplete(int token, Object cookie, int result) {
		mListener.get().dataSetChanged();//TODO BRUTAL hardcoding quite possibly Broadcast receivers might be a more elegant solution
		super.onDeleteComplete(token, cookie, result);
	}
	
	@Override
	protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
		if(mListener.get()!=null)
			mListener.get().replaceCursor(cursor);
		super.onQueryComplete(token, cookie, cursor);
	}
	
	@Override
	protected void onInsertComplete(int token, Object cookie, Uri uri) {
		mListener.get().dataSetChanged();//TODO BRUTAL hardcoding quite possibly Broadcast receivers might be a more elegant solution
		super.onInsertComplete(token, cookie, uri);
	}
	
	@Override
	protected void onUpdateComplete(int token, Object cookie, int result) {
		mListener.get().dataSetChanged();//TODO BRUTAL hardcoding quite possibly Broadcast receivers might be a more elegant solution
		super.onUpdateComplete(token, cookie, result);
	}

}
