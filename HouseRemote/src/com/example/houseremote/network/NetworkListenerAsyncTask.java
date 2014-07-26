package com.example.houseremote.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import android.os.AsyncTask;

import com.example.houseremote.interfaces.SocketProvider;
import com.example.houseremote.interfaces.SwitchStateListener;


public class NetworkListenerAsyncTask extends AsyncTask<Void,NetData,Void>{
	
	SocketProvider mSocketProvider;
	SwitchStateListener mListener;
	boolean kill;
	boolean change;
	Socket mSocket;
	DataInputStream mInputStream;
	public static final int SERVER_PORT=55000; 
	
	public NetworkListenerAsyncTask(SocketProvider socketProvider, SwitchStateListener switchStateListener){
		mListener=switchStateListener;
		mSocketProvider=socketProvider;
	}

	public void registerChange(){
		change=true;
	}
	public void registerKill(){
		kill=true;
	}
	

	@Override
	protected Void doInBackground(Void... params) {
		while(!kill){
				mSocket=mSocketProvider.acquireSocket();
			try {
				mInputStream=new DataInputStream(mSocket.getInputStream());
				change=false;
			} catch (IOException e) {
				e.printStackTrace();
			}
			while(!change){
				try {
					String data=mInputStream.readUTF();
					String[] parsedData=data.split("_");
					NetData mNetPacket=new NetData(Integer.parseInt(parsedData[1]),Boolean.parseBoolean(parsedData[2]));
					publishProgress(mNetPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	

	@Override
	protected void onProgressUpdate(NetData... values) {
		super.onProgressUpdate(values);
		mListener.postValueChange(values[0]);
	}

}
