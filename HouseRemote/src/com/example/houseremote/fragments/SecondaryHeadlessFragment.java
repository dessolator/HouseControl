package com.example.houseremote.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.ListAdapter;

import com.example.houseremote.R;
import com.example.houseremote.network.dataclasses.ServerInfo;
import com.example.houseremote.network.interfaces.BroadCastListener;
import com.example.houseremote.network.threads.BroadcastAsyncTask;

public class SecondaryHeadlessFragment extends Fragment implements BroadCastListener,AdapterProvider{

	private BroadcastAsyncTask mRefreshAsyncTask;
	private ServerListAdapter mAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		mRefreshAsyncTask = new BroadcastAsyncTask(this);
		mAdapter = new ServerListAdapter(getActivity());
		mRefreshAsyncTask.execute((Void[])null);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			mRefreshAsyncTask.resend();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void serverFound(ServerInfo serverInfo) {
		mAdapter.newServerFound(serverInfo);
		
	}

	@Override
	public Context getContext() {
		return getActivity();
	}


	@Override
	public ListAdapter getListAdapter() {
		return mAdapter;
	}


	@Override
	public void serverSelected(ServerInfo item) {
		// TODO 
		
	}
	
}
