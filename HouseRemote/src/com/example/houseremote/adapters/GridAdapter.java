package com.example.houseremote.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.houseremote.R;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.network.PinStatus;
import com.example.houseremote.network.PinStatusSet;

public class GridAdapter extends CursorAdapter {
	private SparseArray<Integer> mStats;

	private LayoutInflater li;
	

	@SuppressLint("UseSparseArrays")
	public GridAdapter(Context myContext, Cursor c, int flags) {
		super(myContext, c, flags);
		mStats=new SparseArray<Integer>();
		this.li = LayoutInflater.from(myContext);
	}

	@Override
	public void bindView(View temp, Context context, Cursor cursor) {
		if (cursor != null) {
			((TextView) temp.findViewById(R.id.gridItemText)).setCompoundDrawablesWithIntrinsicBounds(null, getImage(context,cursor), null, null);
			((TextView) temp.findViewById(R.id.gridItemText)).setText(cursor.getString(1));
		}

	}
	
	public Drawable getImage(Context context,Cursor cursor){
		Drawable retVal;
		if(mStats.get(cursor.getInt(cursor.getColumnIndex(DBHandler.CONTROL_PIN_NUMBER)))==null){
		retVal= context.getResources().getDrawable(context.getResources()
				.getIdentifier("drawable/" + cursor.getString(2), null, context.getPackageName()));
		}
		else{
			String mStateString=mStats.get(cursor.getInt(cursor.getColumnIndex(DBHandler.CONTROL_PIN_NUMBER)))==0?"_on":"_off";
			retVal= context.getResources().getDrawable(context.getResources()
					.getIdentifier("drawable/" + cursor.getString(2)+mStateString, null, context.getPackageName()));
		}
		return retVal;
	}


	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return li.inflate(R.layout.grid_view_icon, arg2, false);
	}

	public void addStatusSet(PinStatusSet ps) {
		for(PinStatus p: ps.getArray()){
			mStats.put(p.pinNumber, p.pinState);
		}
		notifyDataSetChanged();
		
	}

	public void addToStatusSet(PinStatus newData) {
		mStats.put(newData.pinNumber, newData.pinState);
		notifyDataSetChanged();
		
	}

}
