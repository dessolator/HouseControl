package com.example.houseremote.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.houseremote.R;
import com.example.houseremote.network.dataclasses.ServerInfo;
import com.example.houseremote.network.interfaces.BroadCastListener;
import com.example.houseremote.network.threads.BroadcastAsyncTask;

public class RefreshFragment extends Fragment implements BroadCastListener {
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		mRefreshAsyncTask = new BroadcastAsyncTask(this);
		mAdapter = new ServerListAdapter(getContext());
		mRefreshAsyncTask.execute((Void[])null);
		mListView = (ListView)getActivity().findViewById(R.id.autoSearchListview);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				
			}
		});
		super.onActivityCreated(savedInstanceState);
	}

	private BroadcastAsyncTask mRefreshAsyncTask;
	private ListView mListView;
	private ServerListAdapter mAdapter;
	
	
	public RefreshFragment() {		
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.auto_search, menu);
		super.onCreateOptionsMenu(menu, inflater);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_auto_search, container, false);
		
		return rootView;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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
//		mListView.invalidate();
		
	}

	@Override
	public Context getContext() {
		return getActivity();
	}
}
