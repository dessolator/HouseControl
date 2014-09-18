package com.example.houseremote.network.threads;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

import com.example.houseremote.network.exceptions.ThreadAlreadyPausedException;
import com.example.houseremote.network.exceptions.ThreadIsDeadException;
import com.example.houseremote.network.exceptions.ThreadNotPausedException;
import com.example.houseremote.network.exceptions.ThreadNotStartedException;
import com.example.houseremote.network.interfaces.ControlledThread;
import com.example.houseremote.network.interfaces.Sendable;
import com.example.houseremote.network.interfaces.SocketProvider;

public class NetworkSenderThread extends Thread implements ControlledThread{

	private Socket mSocket;
	private DataOutputStream mOutputStream;
	SocketProvider mSocketProvider;
	/*
	 * Concurrent Queue used to manage messages to be passed to the server.
	 */
	private ConcurrentLinkedQueue<Sendable> mQueue;

	/*
	 * Thread management flags.
	 */
	private volatile boolean kill;
	private volatile boolean change;
	private volatile boolean pause;
	private boolean killOnBatchDone=false;
	/*
	 * Thread state flags.
	 */
	private boolean paused = false;
	private boolean dead = false;
	private boolean started = false;

	/**
	 * Constructor for Network Sender.
	 * 
	 * @param socketProvider
	 *            The entity providing the socket for the network sender.
	 */
	public NetworkSenderThread(SocketProvider socketProvider) {
		mQueue = new ConcurrentLinkedQueue<Sendable>();
		mSocketProvider = socketProvider;
	}

	/**
	 * Polls the message queue and sends requests to the UI.
	 */
	@Override
	public void run() {
		started=true;
		while (!kill) {
			while (pause) {
				if (kill)
					break;
				synchronized (this) {
					try {
						paused=true;
						wait();
						paused=false;
					} catch (InterruptedException e) {
					}
				}
			}
			while (!(kill || pause)) {
				change = false;
				try {
					mSocket = mSocketProvider.acquireSocket();
				} catch (UnknownHostException e2) {
					e2.printStackTrace();
					return;
				} catch (IOException e3) {
					e3.printStackTrace();
					return;
				}
				try {
					if(mSocket==null){
						continue;
					}
					mOutputStream = new DataOutputStream(mSocket.getOutputStream());
				} catch (IOException e1) {
				}
				while (!(change || kill || pause)) {
					try {
						sendPacket();
					} catch (InterruptedException e) {
					} catch (IOException e) {
					}
				}
			}
		}
		dead=true;

	}

	/**
	 * Polls the message queue and sends requests to the UI.
	 * 
	 * @throws InterruptedException
	 *             Thrown if the Thread is paused or killed.
	 * @throws IOException
	 *             Thrown if the Thread is paused or killed.
	 */
	private void sendPacket() throws InterruptedException, IOException {
		while (mQueue.isEmpty()) {
			if(killOnBatchDone){
				kill=true;
				mSocket.close();
				return;
			}
			synchronized (this) {
				wait();
			}
		}
		mOutputStream.writeUTF(mQueue.poll().getSendData());
		mOutputStream.flush();
	}

	/**
	 * Adds a switch request to the send queue.
	 * 
	 * @param switchPacket
	 *            The SwitchPacket to be added.
	 */
	public void addToQueue(Sendable sendData) {
		Log.d("MOO", "adding packet to send");
		mQueue.add(sendData);
		synchronized (this) {
			notify();
		}

	}

	/**
	 * Registers the change in the server the thread is sending to.
	 */
	public void registerChange() {
		if (!isThreadAlive())
			return;
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
		if (!isThreadAlive())
			return;
		kill = true;
		interrupt();
	}

	/**
	 * Registers the pause signal to the THread.
	 */
	public void registerPause() {
		if (!isThreadAlive())
			return;
		pause = true;
		interrupt();
	}

	/**
	 * Unpauses the Thread if it is both paused and started.
	 */
	public void unpause() {
		if (!isThreadAlive())
			return;
		if (pause) {
			pause = false;
			synchronized (this) {
				notify();
			}
		}
	}

	public void setKillOnBatchDone(boolean b) {
		if(!started){
			killOnBatchDone=b;//TODO investigate potential use instead of kill
		}
		
	}

	public boolean isThreadPaused() {
		return paused;
	}

	public boolean isThreadAlive() {
		return ((!dead) && started);
	}
	
	public void resumeThread() {
		try {
			if (dead)
				throw new ThreadIsDeadException();
			if (!started)
				throw new ThreadNotStartedException();
			if (!paused)
				throw new ThreadNotPausedException();
		} catch (ThreadIsDeadException e) {
			e.printStackTrace();
		} catch (ThreadNotStartedException e) {
			e.printStackTrace();
		} catch (ThreadNotPausedException e) {
			e.printStackTrace();
		}
		unpause();
		
	}

	public void pauseThread() {
		try {
			if (dead)
				throw new ThreadIsDeadException();
			if (!started)
				throw new ThreadNotStartedException();
			if (paused)
				throw new ThreadAlreadyPausedException();
		} catch (ThreadIsDeadException e) {
			e.printStackTrace();
		} catch (ThreadNotStartedException e) {
			e.printStackTrace();
		} catch (ThreadAlreadyPausedException e) {
			e.printStackTrace();
		}
		registerPause();
		
	}

	public void killThread() {
		try {
			if (dead)
				throw new ThreadIsDeadException();
			if (!started)
				throw new ThreadNotStartedException();
			if (paused)
				unpause();
		} catch (ThreadIsDeadException e) {
			e.printStackTrace();
		} catch (ThreadNotStartedException e) {
			e.printStackTrace();
		}
		registerKill();
		
	}

}
