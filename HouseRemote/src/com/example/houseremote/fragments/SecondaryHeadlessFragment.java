package com.example.houseremote.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.ListAdapter;

import com.example.houseremote.R;
import com.example.houseremote.database.DataBaseAsyncQueryHandler;
import com.example.houseremote.database.adapters.ServerListAdapter;
import com.example.houseremote.database.interfaces.ReplyListener;
import com.example.houseremote.interfaces.HeadlessFragmentUI;
import com.example.houseremote.interfaces.UIReadable;
import com.example.houseremote.network.NetworkSet;
import com.example.houseremote.network.dataclasses.LayoutQueryPacket;
import com.example.houseremote.network.dataclasses.PinStatus;
import com.example.houseremote.network.dataclasses.PinStatusSet;
import com.example.houseremote.network.dataclasses.ServerInfo;
import com.example.houseremote.network.interfaces.BroadCastListener;
import com.example.houseremote.network.interfaces.NetworkDataListener;
import com.example.houseremote.network.threads.BroadcastAsyncTask;

public class SecondaryHeadlessFragment extends Fragment implements BroadCastListener,AdapterProvider,NetworkDataListener, ReplyListener, HeadlessFragmentUI{

	private BroadcastAsyncTask mRefresher;
	private DataBaseAsyncQueryHandler mHandler;
	private ServerListAdapter mAdapter;
	private NetworkSet mNetSet;
	//TODO SETRETAINSTATE

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		mHandler = new DataBaseAsyncQueryHandler(getActivity().getContentResolver(), this);
		mNetSet = new NetworkSet(this, null, 0);
		mRefresher = new BroadcastAsyncTask(this);
		mAdapter = new ServerListAdapter(getActivity());
		mRefresher.execute((Void[])null);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			mRefresher.resend();
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
	public void execRequiredFunction(UIReadable uiReadable) {
		uiReadable.executeNeededCode(this);
		
	}


	@Override
	public void replaceCursor(Cursor cursor, Object cookie) {
	}


	@Override
	public void onControllerDataChanged() {
	}


	@Override
	public void onHouseDataChanged() {
	}


	@Override
	public void onRoomDataChanged() {		
	}


	@Override
	public void postValueChange(PinStatus pinStatus) {
		
	}


	@Override
	public void postLookupValues(PinStatusSet pinStatusSet) {
		
	}
	
}
