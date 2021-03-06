package com.example.houseremote.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.MenuItem;
import android.widget.ListAdapter;

import com.example.houseremote.R;
import com.example.houseremote.database.DataBaseAsyncQueryHandler;
import com.example.houseremote.database.adapters.ServerListAdapter;
import com.example.houseremote.database.interfaces.ServerListAdapterProvider;
import com.example.houseremote.fragments.interfaces.HeadlessFragment;
import com.example.houseremote.interfaces.RunnableOnUIThread;
import com.example.houseremote.network.NetworkSet;
import com.example.houseremote.network.dataclasses.LayoutQueryPacket;
import com.example.houseremote.network.dataclasses.PinStatus;
import com.example.houseremote.network.dataclasses.PinStatusSet;
import com.example.houseremote.network.dataclasses.ServerInfo;
import com.example.houseremote.network.interfaces.BroadCastListener;
import com.example.houseremote.network.interfaces.Sendable;
import com.example.houseremote.network.threads.BroadcastAsyncTask;

public class SecondaryHeadlessFragment extends Fragment implements HeadlessFragment, BroadCastListener, ServerListAdapterProvider{

	private BroadcastAsyncTask mRefresher;
	private DataBaseAsyncQueryHandler mHandler;
	private ServerListAdapter mAdapter;
	private NetworkSet mNetSet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		setRetainInstance(true);
		mHandler = new DataBaseAsyncQueryHandler(getActivity().getContentResolver(), this);
		mNetSet = new NetworkSet(this, null, 0);
		mNetSet.init();
		mRefresher = new BroadcastAsyncTask(this);
		mRefresher.execute((Void[])null);//TODO version check
		mAdapter = new ServerListAdapter(getActivity());
		mRefresher.execute((Void[])null);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			mRefresher.resend();//TODO cleanup
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
		mNetSet.registerChange(item.getIp(), item.getPort()); 
		mNetSet.addToSenderQueue(new LayoutQueryPacket());
		mRefresher.pauseThread();
		
		
	}


	@Override
	public void execRequiredFunction(RunnableOnUIThread uiReadable) {
		uiReadable.runOnUIThread(this);
		
	}


	@Override
	public void onInsertFinished(long parseId, int token) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public DataBaseAsyncQueryHandler getQueryManager() {
		return mHandler;
	}


	@Override
	public void addToNetworkSender(String senderIp, Sendable switchPacket) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onQueryFinished(Cursor cursor, CursorAdapter cookie) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void postValueChange(PinStatus pinStatus) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void postLookupValues(PinStatusSet pinStatusSet) {
		// TODO Auto-generated method stub
		
	}
	
}
