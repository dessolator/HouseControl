package me.kesler.homecontrol;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;

public class GridAdapter extends BaseAdapter {
	private List<String> objectNames;
	private List<String> imageNames;
	private LayoutInflater inflater;
	private Context myContext;

	public GridAdapter(Context myContext, List<String> objectNames,List<String> imageNames) {
		this.myContext=myContext;
		this.objectNames=objectNames;
		this.imageNames=imageNames;
		this.inflater=LayoutInflater.from(myContext);
		}

	@Override
	public int getCount() {
		return objectNames.size();
	}

	@Override
	public Object getItem(int position) {
		return objectNames.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View temp=convertView;
		temp=inflater.inflate(R.layout.grid_view_icon, null);
		((ImageView)temp.findViewById(R.id.gridItemImage)).setImageResource(myContext.getResources().getIdentifier("drawable/"+imageNames.get(position), null, myContext.getPackageName()));
		((TextView)temp.findViewById(R.id.gridItemText)).setText(objectNames.get(position));		
		return temp;
	}

}
