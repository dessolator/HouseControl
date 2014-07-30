package com.example.houseremote.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

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
		Log.d("MOO", "change registered on sender");
		change=true;
		synchronized(this){notify();}
	}
	public void registerKill(){
		kill=true;
		synchronized(this){notify();}
	}
	
	
	
	@Override
	public void run(){
		Log.d("MOO", "networkSender STARTED");
		while(!kill){
			change=false;
			mSocket=mSocketProvider.acquireSocket(55000);
			if(mSocket!=null){
				try {
					mOutputStream=new DataOutputStream(mSocket.getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			while(!change){
				
				if(mSocket==null){
					try {
						synchronized(this){
						wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else{
//					try {
//						sleep(10000);
//					} catch (InterruptedException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
					while(mQueue.isEmpty()){
						try {
							synchronized(this){
							wait();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					SwitchPacket mData=mQueue.poll();
					try {
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
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
				

	public void addToQueue(SwitchPacket switchPacket) {
		Log.d("MOO", "adding packet to send");
		mQueue.add(switchPacket);
		synchronized(this){notify();}
		
	}

}
