package com.example.houseremote.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.example.houseremote.fragments.HeadlessFragment;
import com.example.houseremote.interfaces.Sendable;
import com.example.houseremote.interfaces.SocketProvider;

public class NetworkSet implements SocketProvider{
	NetworkListenerAsyncTask mNetworkListener;
	NetworkSenderThread mNetworkSender;
	String ip;
	Socket mSocket;

	public NetworkSet(HeadlessFragment hf, String ip) {
		super();
		this.ip=ip;
		mNetworkListener= new NetworkListenerAsyncTask(this, hf, hf);
		mNetworkSender= new NetworkSenderThread(this);
	}
	
	@SuppressLint("NewApi")
	public void init() throws UnknownHostException, IOException{
		mSocket = new Socket(InetAddress.getByName(ip),55000);
		
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			if (!mNetworkListener.getStatus().equals(AsyncTask.Status.RUNNING))
				mNetworkListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);

		} else {
			if (!mNetworkListener.getStatus().equals(AsyncTask.Status.RUNNING))
				mNetworkListener.execute((Void[]) null);

		}
		
		if (!mNetworkSender.isAlive())
			mNetworkSender.start();

	}
	

	public void resume() {
		if(mNetworkListener.isListenerPaused())
			mNetworkListener.resumeListener();
		if(mNetworkSender.isSenderPaused())
			mNetworkSender.resmeSender();
		
	}

	public void pause() {
		if(mNetworkListener.isListenerAlive() && !mNetworkListener.isListenerPaused())
			mNetworkListener.pauseListener();
		if(mNetworkSender.isSenderAlive() && !mNetworkSender.isSenderPaused())
			mNetworkSender.pauseSender();
	}

	public void kill() {
		if(mNetworkListener.isListenerAlive())
			mNetworkListener.killListener();
		if(mNetworkSender.isSenderAlive())
			mNetworkSender.killSender();
		
	}

	public void addToSenderQueue(Sendable data) {
		mNetworkSender.addToQueue(data);
		
	}

	@Override
	public Socket acquireSocket(int port) throws UnknownHostException, IOException {
		return mSocket;//TODO port number is getting ignored
	}

	public void registerChange(String ip) throws UnknownHostException, IOException {
		mNetworkListener.registerChange();
		mNetworkSender.registerChange();
		Socket temp = mSocket;
		mSocket = new Socket(InetAddress.getByName(ip),55000);
		temp.close();
		this.ip=ip;
	}

}
