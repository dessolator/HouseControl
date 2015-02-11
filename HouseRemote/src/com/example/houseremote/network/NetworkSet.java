package com.example.houseremote.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.example.houseremote.network.interfaces.Sendable;
import com.example.houseremote.network.interfaces.SocketProvider;
import com.example.houseremote.network.interfaces.NetworkReceiveForward;
import com.example.houseremote.network.threads.NetworkListenerAsyncTask;
import com.example.houseremote.network.threads.NetworkSenderThread;

public class NetworkSet implements SocketProvider {
	private NetworkListenerAsyncTask mNetworkListener;
	private NetworkSenderThread mNetworkSender;
	private String ip;
	private Socket mSocket;
	private NetworkReceiveForward hf;
	private boolean kill;
	private int port;

	public NetworkSet(NetworkReceiveForward hf, String ip, int port) {
		super();
		this.port = port;
		this.ip = ip;
		this.hf = hf;
		mNetworkListener = new NetworkListenerAsyncTask(this, hf);
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

		if (!mNetworkSender.isThreadAlive())
			mNetworkSender.start();

	}

	private void revive() {
		mNetworkListener = new NetworkListenerAsyncTask(this, hf);
		mNetworkSender = new NetworkSenderThread(this);
		kill=false;
		
	}

	public void resume() {
		if(kill){
			init();
		}
		if (mNetworkListener.isThreadPaused())
			mNetworkListener.resumeThread();
		if (mNetworkSender.isThreadPaused())
			mNetworkSender.resumeThread();

	}

	public void pause() {
		if (mNetworkListener.isThreadAlive() && !mNetworkListener.isThreadPaused())
			mNetworkListener.pauseThread();
		if (mNetworkSender.isThreadAlive() && !mNetworkSender.isThreadPaused())
			mNetworkSender.pauseThread();
	}

	public void kill() {
		kill=true;
		if (mNetworkListener.isThreadAlive())
			mNetworkListener.killThread();
		if (mNetworkSender.isThreadAlive())
			mNetworkSender.killThread();

	}

	public void addToSenderQueue(Sendable data) {
		mNetworkSender.addToQueue(data);

	}

	@Override
	synchronized public Socket acquireSocket() throws UnknownHostException, IOException {
		if(kill) return null;
		if(mSocket==null || mSocket.isClosed())
			mSocket = new Socket(InetAddress.getByName(ip),port);
		return mSocket;
	}

	public void registerChange(String ip, int port){
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
		this.port = port;
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

}
