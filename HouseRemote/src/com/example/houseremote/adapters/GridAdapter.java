package com.example.houseremote.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.houseremote.R;

public class GridAdapter extends CursorAdapter{
	
	private LayoutInflater li;

	public GridAdapter(Context myContext, Cursor c, int flags) {
		super(myContext, c , flags);
		this.li=LayoutInflater.from(myContext);
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

	@SuppressLint("InflateParams")
	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return li.inflate(R.layout.grid_view_icon, null,false);//TODO placing parrent instead of null limits the onClick detector
	}


}
