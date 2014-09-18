package com.example.houseremote.network.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import com.example.houseremote.network.dataclasses.ServerInfo;
import com.example.houseremote.network.exceptions.ThreadAlreadyPausedException;
import com.example.houseremote.network.exceptions.ThreadIsDeadException;
import com.example.houseremote.network.exceptions.ThreadNotPausedException;
import com.example.houseremote.network.exceptions.ThreadNotStartedException;
import com.example.houseremote.network.interfaces.BroadCastListener;
import com.example.houseremote.network.interfaces.ControlledThread;

public class BroadcastAsyncTask extends AsyncTask<Void, ServerInfo, Void> implements ControlledThread {

	private static final String SENDDATA = "SERVER_SEARCH";
//	private static final String RECEIVEDATA = "SERVER_SEARCH_REPLY_";
	private static final int SENDPORT = 55001;
	private static final int RECEIVEPORT = 55001;
	private DatagramSocket mSocket;
	private BroadCastListener mCallback;
	private volatile boolean kill;
	private volatile boolean pause;
	private boolean started;
	private boolean paused;
	private boolean dead;

	public BroadcastAsyncTask(BroadCastListener mCallback) {
		this.mCallback = mCallback;

	}

	private InetAddress getBroadcastAddress() throws IOException {

		WifiManager wifi = (WifiManager) mCallback.getContext().getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		// handle null somehow

		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

	@Override
	protected Void doInBackground(Void... params) {
		started = true;
		int iters = 0;
		while (!kill) {
			if (iters <= 0) {
				pause = true;
			}
			keepPaused();
			iters = 10;
			while (!kill && !pause && iters > 0) {
				try {
					iters--;
					mSocket = new DatagramSocket(SENDPORT);
					mSocket.setBroadcast(true);
					DatagramPacket packet = new DatagramPacket(SENDDATA.getBytes(), SENDDATA.length(),
							getBroadcastAddress(), RECEIVEPORT);
					mSocket.send(packet);
					listenForReplyWithTimeout(20);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		dead = true;
		return null;
	}

	private void keepPaused() {
		while (pause && !kill) {
			paused = true;
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			paused = false;
		}
	}

	private void listenForReplyWithTimeout(int seconds) throws IOException {
		long initTime = System.nanoTime();
		mSocket.setSoTimeout(seconds * 1000);
		while ((System.nanoTime() - initTime <= ((long) seconds) * 1000000000l) && !pause && !kill) {
			DatagramPacket packet;
			byte[] buf = new byte[1024];
			packet = new DatagramPacket(buf, buf.length);
			mSocket.receive(packet);
			String data = new String(buf);
			String[] splitData = data.split("_");
			boolean cond = (splitData[0].equals("SERVER") && splitData[1].equals("SEARCH") && splitData[2]
					.equals("REPLY"));
			if (cond) {
				publishProgress(new ServerInfo(packet.getAddress().toString(), Integer.parseInt(splitData[3])));
			}
		}
	}

	@Override
	protected void onProgressUpdate(ServerInfo... values) {
		mCallback.serverFound(values[0]);
		super.onProgressUpdate(values);
	}

	public void resend() {
		resumeThread();

	}

	@Override
	public boolean isThreadPaused() {
		return paused;
	}

	@Override
	public boolean isThreadAlive() {
		return ((!dead) && started);
	}

	public void unpause() {
		if (pause) {
			pause = false;
			synchronized (this) {
				notify();
			}
		}
	}

	@Override
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

	public void registerPause() {
		pause = true;
		mSocket.close();

	}

	@Override
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

	@Override
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
	public void registerKill() {
		if (getStatus() != AsyncTask.Status.RUNNING)
			return;
		kill = true;
		if(mSocket!=null){
			mSocket.close();
		}
		synchronized (this) {
			notify();
		}

	}

}
