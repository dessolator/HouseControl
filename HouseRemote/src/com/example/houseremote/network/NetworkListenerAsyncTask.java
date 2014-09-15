package com.example.houseremote.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

import com.example.houseremote.network.dataclasses.PinStatusSet;
import com.example.houseremote.network.exceptions.ListenerAlreadyPausedException;
import com.example.houseremote.network.exceptions.ListenerIsDeadException;
import com.example.houseremote.network.exceptions.ListenerNotPausedException;
import com.example.houseremote.network.exceptions.ListenerNotStartedException;
import com.example.houseremote.network.interfaces.SocketProvider;
import com.example.houseremote.network.interfaces.SwitchStateListener;
import com.example.houseremote.network.interfaces.UILockupListener;

public class NetworkListenerAsyncTask extends AsyncTask<Void, PinStatusSet, Void> {

	private SwitchStateListener mSwitchStateListener;
	private UILockupListener mLockupListener;
	private Socket mSocket;
	private SocketProvider mSocketProvider;
	private DataInputStream mInputStream;
	/*
	 * Thread management flags.
	 */
	private volatile boolean kill = false;
	private volatile boolean change = false;
	private volatile boolean pause = false;
	private boolean paused = false;
	private boolean dead = false;
	private boolean started = false;

	/**
	 * Constructor for Network Listener.
	 * 
	 * @param mSocketProvider
	 *            The entity providing the socket for the network listener.
	 * @param mSwitchStateListener
	 *            The entity listening to individual pin changes.
	 * @param mLockupListener
	 *            The entity listening to pin status lookups.
	 */
	public NetworkListenerAsyncTask(SocketProvider mSocketProvider, SwitchStateListener mSwitchStateListener,
			UILockupListener mLockupListener) {
		super();
		this.mSocketProvider = mSocketProvider;
		this.mLockupListener = mLockupListener;
		this.mSwitchStateListener = mSwitchStateListener;
	}

	/**
	 * Listens to the server for any changes to the PinSet.
	 */
	@Override
	protected Void doInBackground(Void... params) {
		started = true;
		while (!kill) {
			while (pause) {
				if (kill)
					break;
				synchronized (this) {
					try {
						paused = true;
						wait();
						paused = false;
					} catch (InterruptedException e) {
					}
				}
			}
			while (!(kill || pause)) {
				change = false;
				try {
					mSocket = mSocketProvider.acquireSocket(55000);
				} catch (UnknownHostException e2) {
					e2.printStackTrace();
					return null;
				} catch (IOException e3) {
					return null;

				}
				try {
					mInputStream = new DataInputStream(mSocket.getInputStream());
				} catch (IOException e1) {
				}
				while (!(change || kill || pause)) {
					try {
						operateOnData();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			dead = true;
		}

		return null;
	}

	/**
	 * Posts the values to the UI.
	 * 
	 * @param values
	 *            The PinStatusSet to parse.
	 */
	@Override
	protected void onProgressUpdate(PinStatusSet... values) {
		super.onProgressUpdate(values);
		if (values[0].size() > 1) {
			mLockupListener.postLookupValues(values[0]);
		} else {
			mSwitchStateListener.postValueChange(values[0].get(0));
		}
	}

//	@Override
//	protected void onPostExecute(Void result) {
//		mLockupListener.reportFailiureToConnectToServer();
//		super.onPostExecute(result);
//	}

	/**
	 * Listens to server and publishes changes.
	 * 
	 * @throws IOException
	 *             The exception thrown if the AsyncTask is paused or killed
	 */
	private void operateOnData() throws IOException {
		Log.d("MOO", "LISTENING");
		String mData = mInputStream.readUTF();// close socket interrupts...
		String[] mSplitData = mData.split("_");
		if (mSplitData[0].trim().equals("FULLSTATUSREPLY")) {
			Log.d("MOO", "FULL STATE QUERY RECEIVED");
			PinStatusSet mStatusSet = new PinStatusSet();
			for (int i = 1; i < mSplitData.length; i++) {
				String[] temp = mSplitData[i].split(",");
				mStatusSet.add(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));

			}
			publishProgress(mStatusSet);
		}
		if (mSplitData[0].trim().equals("FLIPPED")) {
			Log.d("MOOOO", "Received flip log");
			PinStatusSet mStatusSet = new PinStatusSet();
			if (mSplitData[2].equals("LOW")) {
				mStatusSet.add(Integer.parseInt(mSplitData[1]), 0);
			} else {
				mStatusSet.add(Integer.parseInt(mSplitData[1]), 1);
			}
			publishProgress(mStatusSet);

		}
	}

	/**
	 * Registers the change in the server the thread should listen to.
	 */
	public void registerChange() {
		if (getStatus() != AsyncTask.Status.RUNNING)
			return;
		change = true;

	}

	/**
	 * Registers the kill signal to the AsyncTask.
	 */
	public void registerKill() {
		if (getStatus() != AsyncTask.Status.RUNNING)
			return;
		kill = true;
		if(mSocket!=null){
		try {
			mSocket.close();
		} catch (IOException e) {
		}
		}
		synchronized (this) {
			notify();
		}

	}

	/**
	 * Registers the pause signal to the AsyncTask.
	 */
	public void registerPause() {
		if (getStatus() != AsyncTask.Status.RUNNING)
			return;
		pause = true;
		try {
			mSocket.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Unpauses the AsyncTask if it is both started and paused.
	 */
	public void unpause() {
		if (getStatus() != AsyncTask.Status.RUNNING)
			return;
		if (pause) {
			pause = false;
			synchronized (this) {
				notify();
			}
		}
	}

	public boolean isListenerPaused() {
		return paused;
	}

	public boolean isListenerAlive() {
		return ((!dead) && started);
	}

	public void resumeListener() {
		try {
			if (dead)
				throw new ListenerIsDeadException();
			if (!started)
				throw new ListenerNotStartedException();
			if (!paused)
				throw new ListenerNotPausedException();
		} catch (ListenerIsDeadException e) {
			e.printStackTrace();
		} catch (ListenerNotStartedException e) {
			e.printStackTrace();
		} catch (ListenerNotPausedException e) {
			e.printStackTrace();
		}
		unpause();
	}

	public void pauseListener() {
		try {
			if (dead)
				throw new ListenerIsDeadException();
			if (!started)
				throw new ListenerNotStartedException();
			if (paused)
				throw new ListenerAlreadyPausedException();
		} catch (ListenerIsDeadException e) {
			e.printStackTrace();
		} catch (ListenerNotStartedException e) {
			e.printStackTrace();
		} catch (ListenerAlreadyPausedException e) {
			e.printStackTrace();
		}
		registerPause();

	}

	public void killListener() {
		try {
			if (dead)
				throw new ListenerIsDeadException();
			if (!started)
				throw new ListenerNotStartedException();
			if (paused)
				unpause();
		} catch (ListenerIsDeadException e) {
			e.printStackTrace();
		} catch (ListenerNotStartedException e) {
			e.printStackTrace();
		}
		registerKill();

	}

}
