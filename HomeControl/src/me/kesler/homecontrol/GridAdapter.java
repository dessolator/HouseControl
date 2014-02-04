package me.kesler.homecontrol;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {
//	private Context myContext;
	private List<String> houseNames;
	private LayoutInflater inflater;

	public GridAdapter(Context myContext, List<String> houseNames) {
//		this.myContext=myContext;
		this.houseNames=houseNames;
		this.inflater=LayoutInflater.from(myContext);
		}

	@Override
	public int getCount() {
		return houseNames.size();
	}

	@Override
	public Object getItem(int position) {
		return houseNames.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View temp=convertView;
		temp=inflater.inflate(R.layout.grid_view_icon, null);
		((TextView)temp.findViewById(R.id.gridItemText)).setText(houseNames.get(position));		
		return temp;
	}

}
