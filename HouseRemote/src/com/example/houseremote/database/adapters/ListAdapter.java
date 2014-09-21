package com.example.houseremote.database.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.houseremote.R;

public class ListAdapter extends CursorAdapter {

	private LayoutInflater li;

	public ListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		li = LayoutInflater.from(context);

	}

	@Override
	public void bindView(View temp, Context context, Cursor cursor) {
		if (cursor != null) {
			((TextView) temp.findViewById(R.id.listItemText)).setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(context.getResources()
					.getIdentifier("drawable/" + cursor.getString(2), null, context.getPackageName())),null, null, null);

			((TextView) temp.findViewById(R.id.listItemText)).setText(cursor.getString(1));
		}

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		return li.inflate(R.layout.list_view_icon, viewGroup, false);
	}

}
