package com.example.houseremote.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;

import android.os.AsyncTask;

import com.example.houseremote.interfaces.NetworkCallbackListener;
import com.example.houseremote.interfaces.SocketProvider;

public class ControllerStateQueryAsyncTask extends AsyncTask<Void, Void, PinStatusSet> {

	

	Socket mSocket;
	DataInputStream mInputStream;
	DataOutputStream mOutputStream;
	private SocketProvider mSocketProvider;
	WeakReference<NetworkCallbackListener> mCallback;
	
	public ControllerStateQueryAsyncTask(NetworkCallbackListener headlessFragment) {
		mCallback=new WeakReference<NetworkCallbackListener>(headlessFragment);
	}

	@Override     
	protected PinStatusSet doInBackground(Void... params) {
		mSocket=mSocketProvider.acquireSocket();
		try {
			mInputStream=new DataInputStream(mSocket.getInputStream());
			mOutputStream=new DataOutputStream(mSocket.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//TODO send call
		//TODO wait for reply
		//TODO create reply packet from response

		return null;
	}
	
	@Override
	protected void onPostExecute(PinStatusSet result) {
		super.onPostExecute(result);
		if(mCallback.get()!=null)
			mCallback.get().pinStateQueryComplete(result);
	}
	

}
