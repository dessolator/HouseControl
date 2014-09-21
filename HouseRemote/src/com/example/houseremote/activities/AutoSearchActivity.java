package com.example.houseremote.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;

import com.example.houseremote.R;
import com.example.houseremote.fragments.AdapterProvider;
import com.example.houseremote.fragments.RefreshFragment;
import com.example.houseremote.fragments.SecondaryHeadlessFragment;
import com.example.houseremote.network.dataclasses.ServerInfo;
import com.example.houseremote.network.interfaces.BroadCastListener;

/**
 * Activity used to broadcast a packet on the network looking for Hosts if hosts
 * are found they're listed in the listview. When a server is selected a
 * connection is established to it and the layout is received.
 * 
 * The activity uses two fragments:
 * 
 * The headless fragment used to retain data, sockets etc. on configuration
 * changes.
 * 
 * The view fragment used to display the data.
 * 
 * 
 * 
 * @author Ivan Kesler
 *
 */
public class AutoSearchActivity extends ActionBarActivity implements BroadCastListener, AdapterProvider {

	/*
	 * The fragment containing all the persistent data.
	 */
	private SecondaryHeadlessFragment mHeadlessFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_search);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new RefreshFragment())
					.commit();
			getSupportFragmentManager().executePendingTransactions();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void serverFound(ServerInfo serverInfo) {
		mHeadlessFragment.serverFound(serverInfo);

	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public ListAdapter getListAdapter() {
		return mHeadlessFragment.getListAdapter();
	}

	@Override
	public void serverSelected(ServerInfo item) {
		mHeadlessFragment.serverSelected(item);

	}

}
