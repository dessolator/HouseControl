package com.example.houseremote.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.houseremote.R;
import com.example.houseremote.network.PinStatusSet;

public class GridAdapter extends CursorAdapter {

	private LayoutInflater li;

	public GridAdapter(Context myContext, Cursor c, int flags) {
		super(myContext, c, flags);
		this.li = LayoutInflater.from(myContext);
	}

	@Override
	public void bindView(View temp, Context context, Cursor cursor) {
		if (cursor != null) {
			((TextView) temp.findViewById(R.id.gridItemText)).setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(context.getResources()
					.getIdentifier("drawable/" + cursor.getString(2), null, context.getPackageName())), null, null);
			((TextView) temp.findViewById(R.id.gridItemText)).setText(cursor.getString(1));
		}

	}


	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return li.inflate(R.layout.grid_view_icon, arg2, false);
	}

	public void addStatusSet(PinStatusSet ps) {
		// TODO Auto-generated method stub
		
	}

}
