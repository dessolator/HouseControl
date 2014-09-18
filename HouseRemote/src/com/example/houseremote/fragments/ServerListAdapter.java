package com.example.houseremote.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.houseremote.R;
import com.example.houseremote.network.dataclasses.ServerInfo;

public class ServerListAdapter extends BaseAdapter{
	private ArrayList<ServerInfo> mArray;
	private Context mContext;
	
	
	public ServerListAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		this.mArray = new ArrayList<ServerInfo>();
	}

	@Override
	public int getCount() {
		return mArray.size();
	}

	@Override
	public ServerInfo getItem(int position) {
		return mArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
            LayoutInflater vi =  (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.server_lookup_icon, parent);
        }
//		convertView.findViewById(R.id.serverNameView).setText(mArray.get(position).getName());//TODO add server names to server info
		((TextView)convertView.findViewById(R.id.serverNameView)).setText((mArray.get(position).getIp()+":"+mArray.get(position).getPort()));
		return convertView;
	}

	public void newServerFound(ServerInfo serverInfo) {
		if(!mArray.contains(serverInfo)){
			mArray.add(serverInfo);
		}
		notifyDataSetChanged();
		
	}

}
