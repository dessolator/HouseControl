package com.example.houseremote.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.houseremote.network.NetworkListener.SocketProvider;

public class NetworkSender extends Thread {
	private ConcurrentLinkedQueue<SwitchPacket> mQueue=new ConcurrentLinkedQueue<SwitchPacket>();
	private boolean kill;
	private boolean change;
	private String mRoomIp;
	private Socket mSocket;
	private DataOutputStream mOutputStream;
	SocketProvider mSocketProvider;
	
	public NetworkSender(SocketProvider socketProvider){
		mSocketProvider=socketProvider;
	}
	
	
	
	public void registerChange(){
		change=true;
	}
	public void registerKill(){
		kill=true;
	}
	
	
	
	@Override
	public void run(){
		while(!kill){
			mRoomIp=mSocketProvider.getIp();
			mSocket=acquireSocket();
		try {
			mOutputStream=new DataOutputStream(mSocket.getOutputStream());
			change=false;
		} catch (IOException e) {
			// problem getting inputstream
			e.printStackTrace();
		}
			while(!change){
				while(mQueue.isEmpty()){try {
					synchronized(this){wait();}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
				SwitchPacket mData=mQueue.poll();
				try {
					mOutputStream.writeUTF("FLIP_"+mData.getPin());
					mOutputStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//TODO send packet off the top
			}
		}
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



	public void addToQueue(SwitchPacket switchPacket) {
		mQueue.add(switchPacket);
		
	}

}
