package com.example.houseremote.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.houseremote.R;
import com.example.houseremote.network.dataclasses.ServerInfo;

public class RefreshFragment extends Fragment  {

	private ListView mListView;
	private AdapterProvider mCallback;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		mCallback = (AdapterProvider)getActivity();
		mListView = (ListView)getActivity().findViewById(R.id.autoSearchListview);
		mListView.setAdapter(mCallback.getListAdapter());
		mListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCallback.serverSelected((ServerInfo)mListView.getAdapter().getItem(position));
//				mDataSetReaderAsyncTask.setData(mAdapter.getItem(position));
				//TODO set loopyLoop thing
				//TODO add a insert complete listener
				
			}
		});
		super.onActivityCreated(savedInstanceState);
	}
	
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
	
}
