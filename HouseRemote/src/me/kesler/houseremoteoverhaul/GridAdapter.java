package me.kesler.houseremoteoverhaul;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LongSparseArray;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class GridAdapter extends CursorAdapter {
	private LayoutInflater li;
	private LongSparseArray<Model> models;
	

	public GridAdapter(Context myContext, Cursor c, int flags) {
		super(myContext, c, flags);
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
		return models.get(cursor.getLong(cursor.getColumnIndex(DBContract.CONTROLLER_ID))).getImage(context);
	}


	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return li.inflate(R.layout.grid_view_icon, arg2, false);
	}

	public void loadModels(){
		models = new LongSparseArray<Model>();
		//TODO traverse cursor and for each item add a model
	}

}
