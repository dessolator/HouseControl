package com.example.houseremote.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.houseremote.interfaces.SocketProvider;

public class NetworkSenderThread extends Thread {
	private ConcurrentLinkedQueue<SwitchPacket> mQueue=new ConcurrentLinkedQueue<SwitchPacket>();
	private boolean kill;
	private boolean change;
	private Socket mSocket;
	private DataOutputStream mOutputStream;
	SocketProvider mSocketProvider;
	
	public NetworkSenderThread(SocketProvider socketProvider){
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
			mSocket=mSocketProvider.acquireSocket();
		try {
			mOutputStream=new DataOutputStream(mSocket.getOutputStream());
			change=false;
		} catch (IOException e) {
			e.printStackTrace();
		}
			while(!change){
				while(mQueue.isEmpty()){try {
					synchronized(this){wait();}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}}
				SwitchPacket mData=mQueue.poll();
				try {
					mOutputStream.writeUTF("FLIP_"+mData.getPin());
					mOutputStream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}



	public void addToQueue(SwitchPacket switchPacket) {
		mQueue.add(switchPacket);
		
	}

}
