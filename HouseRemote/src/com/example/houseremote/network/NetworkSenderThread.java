package com.example.houseremote.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

import com.example.houseremote.interfaces.SocketProvider;

public class NetworkSenderThread extends Thread {
	private ConcurrentLinkedQueue<SwitchPacket> mQueue=new ConcurrentLinkedQueue<SwitchPacket>();
	private volatile boolean kill;
	private volatile boolean change;
	private volatile boolean pause;
	private Socket mSocket;
	private DataOutputStream mOutputStream;
	SocketProvider mSocketProvider;
	
	public NetworkSenderThread(SocketProvider socketProvider){
		mSocketProvider=socketProvider;
	}
	
	
	
	public void registerChange(){
		Log.d("MOO", "change registered on sender");
		change=true;
		synchronized(this){notify();}
	}
	public void registerKill(){
		kill=true;
//		try {
//			mOutputStream.close();
//		} catch (IOException e) {
//		}
		synchronized(this){notify();notify();}
	}
	
	public void unpause(){
		pause=false;
		synchronized(this){notify();}
	}

	
	@Override
	public void run(){
		while(!kill){
			while(pause){
				if(kill)break;
				synchronized(this){try {
					wait();
				} catch (InterruptedException e) {
				}}
			}
			while(!(kill||pause)){
				change=false;
				mSocket=mSocketProvider.acquireSocket(55000);
				try {
					mOutputStream=new DataOutputStream(mSocket.getOutputStream());
				} catch (IOException e1) {
				}
				while(!(change||kill|| pause)){
					try {
						operateOnData();
					} catch (InterruptedException e) {
					} catch (IOException e) {
					}
				}
			}
		}
	}



	private void operateOnData() throws InterruptedException, IOException {
		while(mQueue.isEmpty()){
				synchronized(this){
				wait();
				}
		}
		SwitchPacket mData=mQueue.poll();
		switch(mData.getType()){
			case FLIP:
				mOutputStream.writeUTF("FLIP_"+mData.getPin());
				break;
			case FULLSTATEREAD:
				Log.d("MOO", "FULL STATE QUERY");
				mOutputStream.writeUTF("FULLSTATUS_ASDASD");
				break;
		}
		mOutputStream.flush();
	}
				

	public void addToQueue(SwitchPacket switchPacket) {
		Log.d("MOO", "adding packet to send");
		mQueue.add(switchPacket);
		synchronized(this){notify();}
		
	}


	public void registerPause() {
		pause = true;
	}



}
