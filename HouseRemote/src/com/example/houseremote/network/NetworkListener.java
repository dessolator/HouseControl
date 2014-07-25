package com.example.houseremote.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;

//TODO need to get the IP somehow
public class NetworkListener extends AsyncTask<Void,NetData,Void>{
	
	public interface SwitchStateListener{
		public void postValueChange(NetData newData);
	}
	public interface SocketProvider{
		public Socket getSocket();

		public void setSocket(Socket temp);

		public String getIp();
	}
	
	SocketProvider mSocketProvider;
	SwitchStateListener mListener;
	boolean kill;
	boolean change;
	String mRoomIp;
	Socket mSocket;
	DataInputStream mInputStream;
	public static final int SERVER_PORT=55000; 
	
	public NetworkListener(SocketProvider socketProvider, SwitchStateListener switchStateListener){
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
				mRoomIp=mSocketProvider.getIp();
				mSocket=acquireSocket();
			try {
				mInputStream=new DataInputStream(mSocket.getInputStream());
				change=false;
			} catch (IOException e) {
				// problem getting inputstream
				e.printStackTrace();
			}
			while(!change){
				try {
					String data=mInputStream.readUTF();
					String[] parsedData=data.split("_");
					NetData mNetPacket=new NetData(Integer.parseInt(parsedData[1]),Boolean.parseBoolean(parsedData[2]));
					publishProgress(mNetPacket);
				} catch (IOException e) {
						//problem reading string
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	
	
	private Socket acquireSocket() {
		Socket temp= mSocketProvider.getSocket();
		if(temp==null){
			try {
				temp=new Socket(InetAddress.getByName(mRoomIp),55000);
				mSocketProvider.setSocket(temp);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return temp;
	}

	@Override
	protected void onProgressUpdate(NetData... values) {
		super.onProgressUpdate(values);
		mListener.postValueChange(values[0]);
	}

	public void notifyOfChange() {
		// TODO Auto-generated method stub
		
	}
	

}
