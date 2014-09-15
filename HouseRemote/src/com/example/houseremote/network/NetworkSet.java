package com.example.houseremote.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.example.houseremote.fragments.HeadlessFragment;
import com.example.houseremote.network.interfaces.Sendable;
import com.example.houseremote.network.interfaces.SocketProvider;

public class NetworkSet implements SocketProvider {
	NetworkListenerAsyncTask mNetworkListener;
	NetworkSenderThread mNetworkSender;
	String ip;
	Socket mSocket;
	HeadlessFragment hf;
	boolean kill;

	public NetworkSet(HeadlessFragment hf, String ip) {
		super();
		this.ip = ip;
		this.hf = hf;
		mNetworkListener = new NetworkListenerAsyncTask(this, hf, hf);
		mNetworkSender = new NetworkSenderThread(this);
	}

	@SuppressLint("NewApi")
	public void init() {
		if(kill){
			revive();
		}

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

	private void revive() {
		mNetworkListener = new NetworkListenerAsyncTask(this, hf, hf);
		mNetworkSender = new NetworkSenderThread(this);
		kill=false;
		
	}

	public void resume() {
		if(kill){
			init();
		}
		if (mNetworkListener.isListenerPaused())
			mNetworkListener.resumeListener();
		if (mNetworkSender.isSenderPaused())
			mNetworkSender.resmeSender();

	}

	public void pause() {
		if (mNetworkListener.isListenerAlive() && !mNetworkListener.isListenerPaused())
			mNetworkListener.pauseListener();
		if (mNetworkSender.isSenderAlive() && !mNetworkSender.isSenderPaused())
			mNetworkSender.pauseSender();
	}

	public void kill() {
		kill=true;
		if (mNetworkListener.isListenerAlive())
			mNetworkListener.killListener();
		if (mNetworkSender.isSenderAlive())
			mNetworkSender.killSender();

	}

	public void addToSenderQueue(Sendable data) {
		mNetworkSender.addToQueue(data);

	}

	@Override
	synchronized public Socket acquireSocket(int port) throws UnknownHostException, IOException {
		if(kill) return null;
		if(mSocket==null || mSocket.isClosed())
			mSocket = new Socket(InetAddress.getByName(ip),55000);
		return mSocket;// TODO port number is getting ignored
	}

	public void registerChange(String ip){
		if(kill){
			init();
		}
		mNetworkListener.registerChange();
		mNetworkSender.registerChange();
		if(mSocket!=null){
			try {
			mSocket.close();
		} catch (IOException e) {
		}
	}
		this.ip = ip;
	}

}
