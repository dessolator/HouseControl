package com.example.houseremote.adapters;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.houseremote.R;

public class GridAdapter extends BaseAdapter{
	
	private List<Listable> objects;
	private LayoutInflater inflater;
	private Context myContext;

	public GridAdapter(Context myContext, List<Listable> objects) {
		this.myContext=myContext;
		this.objects=objects;
		this.inflater=LayoutInflater.from(myContext);
		}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Object getItem(int position) {
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View temp=convertView;
		temp=inflater.inflate(R.layout.grid_view_icon, null);
		((ImageView)temp.findViewById(R.id.gridItemImage)).setImageResource(myContext.getResources().getIdentifier("drawable/"+objects.get(position).getImageName(), null, myContext.getPackageName()));
		((TextView)temp.findViewById(R.id.gridItemText)).setText(objects.get(position).getName());		
		return temp;
	}

}
