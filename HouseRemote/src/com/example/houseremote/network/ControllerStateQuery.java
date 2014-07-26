package com.example.houseremote.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;

import com.example.houseremote.interfaces.NetworkCallbackListener;
import com.example.houseremote.interfaces.SocketProvider;

public class ControllerStateQuery extends AsyncTask<PinStatusSet, Void, PinStatusSet> {

	

	Socket mSocket;
	DataInputStream mInputStream;
	DataOutputStream mOutputStream;
	private SocketProvider mSocketProvider;
	private String mRoomIp;
	WeakReference<NetworkCallbackListener> mCallback;
	
	public ControllerStateQuery(NetworkCallbackListener headlessFragment) {//TODO need a finishlistener
		mCallback=new WeakReference<NetworkCallbackListener>(headlessFragment);
	}

	@Override     
	protected PinStatusSet doInBackground(PinStatusSet... params) {
		mSocket=acquireSocket();
		try {
			mInputStream=new DataInputStream(mSocket.getInputStream());
			mOutputStream=new DataOutputStream(mSocket.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(PinStatus p: (params[0]).getArray()){
			try {
				mOutputStream.writeUTF("STATUS_"+p.pinNumber);
				mOutputStream.flush();
				p.pinState=mInputStream.readUTF().equals("HIGH")?0:1;//TODO hardcoded
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return params[0];
	}
	
	@Override
	protected void onPostExecute(PinStatusSet result) {
		super.onPostExecute(result);
		if(mCallback.get()!=null)
			mCallback.get().pinStateQueryComplete(result);
	}
	
	private Socket acquireSocket() {
		Socket temp= mSocketProvider.getSocket();
		if(temp==null){
			try {
				temp=new Socket(InetAddress.getByName(mRoomIp),55000);
				mSocketProvider.setSocket(temp);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return temp;
	}

}
