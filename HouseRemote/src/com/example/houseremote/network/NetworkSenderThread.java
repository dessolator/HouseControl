package com.example.houseremote.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

import com.example.houseremote.interfaces.SocketProvider;

public class NetworkSenderThread extends Thread {
	
	private Socket mSocket;
	private DataOutputStream mOutputStream;
	SocketProvider mSocketProvider;
	/*
	 * Concurrent Queue used to manage messages to be passed to the server.
	 */
	private ConcurrentLinkedQueue<SwitchPacket> mQueue;
	
	/*
	 * Thread management flags.
	 */
	private volatile boolean kill;
	private volatile boolean change;
	private volatile boolean pause;

	/**
	 * Constructor for Network Sender.
	 * @param socketProvider The entity providing the socket for the network sender.
	 */
	public NetworkSenderThread(SocketProvider socketProvider) {
		mQueue = new ConcurrentLinkedQueue<SwitchPacket>();
		mSocketProvider = socketProvider;
	}

	
	

	/**
	 * Polls the message queue and sends requests to the UI.
	 */
	@Override
	public void run() {
		while (!kill) {
			while (pause) {
				if (kill)
					break;
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
					}
				}
			}
			while (!(kill || pause)) {
				change = false;
				mSocket = mSocketProvider.acquireSocket(55000);
				try {
					mOutputStream = new DataOutputStream(mSocket.getOutputStream());
				} catch (IOException e1) {
				}
				while (!(change || kill || pause)) {
					try {
						operateOnData();
					} catch (InterruptedException e) {
					} catch (IOException e) {
					}
				}
			}
		}
	}

	/**
	 * Polls the message queue and sends requests to the UI.
	 * @throws InterruptedException Thrown if the Thread is paused or killed.
	 * @throws IOException Thrown if the Thread is paused or killed.
	 */
	private void operateOnData() throws InterruptedException, IOException {
		while (mQueue.isEmpty()) {
			synchronized (this) {
				wait();
			}
		}
		SwitchPacket mData = mQueue.poll();
		switch (mData.getType()) {
		case FLIP:
			mOutputStream.writeUTF("FLIP_" + mData.getPin());
			break;
		case FULLSTATEREAD:
			Log.d("MOO", "FULL STATE QUERY");
			mOutputStream.writeUTF("FULLSTATUS_ASDASD");
			break;
		}
		mOutputStream.flush();
	}

	/**
	 * Adds a switch request to the send queue.
	 * @param switchPacket The SwitchPacket to be added.
	 */
	public void addToQueue(SwitchPacket switchPacket) {
		Log.d("MOO", "adding packet to send");
		mQueue.add(switchPacket);
		synchronized (this) {
			notify();
		}

	}
	
	/**
	 * Registers the change in the server the thread is sending to. 
	 */
	public void registerChange() {
		if(!isAlive()) return;
		Log.d("MOO", "change registered on sender");
		change = true;
		synchronized (this) {
			notify();
		}
	}

	/**
	 * Registers the kill signal to the Thread.
	 */
	public void registerKill() {
		if(!isAlive()) return;
		kill = true;
		interrupt();
	}

	

	/**
	 * Registers the pause signal to the THread.
	 */
	public void registerPause() {
		if(!isAlive()) return;
		pause = true;
		interrupt();
	}

	/**
	 * Unpauses the Thread if it is both paused and started.
	 */
	public void unpause() {
		if(!isAlive()) return;
		if(pause){
			pause = false;
			synchronized (this) {
				notify();
			}
		}
	}
	
}
