package com.example.houseremote.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;

import com.example.houseremote.interfaces.SocketProvider;
import com.example.houseremote.interfaces.SwitchStateListener;
import com.example.houseremote.interfaces.UILockupListener;

public class NetworkListenerThread extends AsyncTask<Void,PinStatusSet,Void> {

	
	private SwitchStateListener mSwitchStateListener;
	private UILockupListener mLockupListener;
	private Socket mSocket;
	private SocketProvider mSocketProvider;
	private DataInputStream mInputStream;
	private volatile boolean kill;
	private volatile boolean change;

	public NetworkListenerThread(SocketProvider mSocketProvider, SwitchStateListener mSwitchStateListener,UILockupListener mLockupListener) {
		super();
		this.mSocketProvider = mSocketProvider;
		this.mLockupListener=mLockupListener;
		this.mSwitchStateListener=mSwitchStateListener;
	}


	@Override
	protected Void doInBackground(Void... params) {
		while (!kill) {
			change = false;
			mSocket = mSocketProvider.acquireSocket(55000);
			if (mSocket != null) {
				try {
					mInputStream = new DataInputStream(mSocket.getInputStream());
				} catch (IOException e) {
				}
			}
			while (!change) {
				if (mSocket == null) {
					try {
						synchronized (this) {
							wait();
						}
					} catch (InterruptedException e) {
					}
				} else {
					try {
						operateOnData();
					} catch (IOException e) {

					}
				}
			}
		}
		return null;
	}
	
	@Override
	protected void onProgressUpdate(PinStatusSet... values) {
		super.onProgressUpdate(values);
		if(values[0].size()>1){
			mLockupListener.postLookupValues(values[0]);
		}
		else{
			mSwitchStateListener.postValueChange(values[0].get(0));
		}
	}


	private void operateOnData() throws IOException {
		Log.d("MOO", "LISTENING");
		String mData = mInputStream.readUTF();//close socket interrupts...
		String[] mSplitData = mData.split("_");
		if (mSplitData[0].trim().equals("FULLSTATUSREPLY")) {
			Log.d("MOO", "FULL STATE QUERY RECEIVED");
			PinStatusSet mStatusSet = new PinStatusSet();
			for (int i = 1; i < mSplitData.length; i++) {
				String[] temp = mSplitData[i].split(",");
				mStatusSet.add(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));

			}
			publishProgress(mStatusSet);
		}
		if (mSplitData[0].trim().equals("FLIPPED")) {
			Log.d("MOOOO", "Received flip log");
			PinStatusSet mStatusSet=new PinStatusSet();
			if (mSplitData[2].equals("LOW")) {
				mStatusSet.add(Integer.parseInt(mSplitData[1]),0);
			} else{
				mStatusSet.add(Integer.parseInt(mSplitData[1]),1);
			}
			publishProgress(mStatusSet);

		}
	}

	public void registerChange() {
		Log.d("MOO", "change registered in listener ");
		change = true;
		synchronized (this) {
			notify();
		}

	}

	public void registerKill() {
		kill = true;
		synchronized (this) {
			notify();
		}

	}


}
