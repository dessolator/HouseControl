package com.example.houseremote.adapters;

import com.example.houseremote.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RoomListAdapter extends CursorAdapter {

	LayoutInflater li;

	public RoomListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		li = LayoutInflater.from(context);

	}

	@Override
	public void bindView(View temp, Context context, Cursor cursor) {
		if (cursor != null) {
			((ImageView) temp.findViewById(R.id.gridItemImage))
					.setImageResource(context.getResources().getIdentifier(
							"drawable/" + cursor.getString(2), null,
							context.getPackageName()));
			((TextView) temp.findViewById(R.id.gridItemText)).setText(cursor
					.getString(1));
		}

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		return li.inflate(R.layout.list_view_icon, null);
	}

}
