package com.example.houseremote.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import com.example.houseremote.network.dataclasses.ServerInfo;
import com.example.houseremote.network.interfaces.BroadCastListener;

public class BroadcastAsyncTask extends AsyncTask<Void, ServerInfo, Void> {

	private static final String SENDDATA = "SERVER_SEARCH";
	private static final String RECEIVEDATA = "SERVER_SEARCH_REPLY_";
	private static final int SENDPORT = 55001;
	private static final int RECEIVEPORT = 55001;
	private DatagramSocket mSocket;
	private BroadCastListener mCallback;
	private boolean kill;

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
		int iters = 10;
		while (!kill) {
			if(iters<=0){
				synchronized (this) {
					wait();
				}
			}
			 iters = 10;
			while (!kill || iters <= 0) {// TODO
				try {
					iters--;
					mSocket = new DatagramSocket(SENDPORT);
					mSocket.setBroadcast(true);
					DatagramPacket packet = new DatagramPacket(SENDDATA.getBytes(), SENDDATA.length(),
							getBroadcastAddress(), RECEIVEPORT);
					mSocket.send(packet);
					long initTime = System.nanoTime();
					mSocket.setSoTimeout(20000);
					while (System.nanoTime() - initTime <= 20000000000l) {
						byte[] buf = new byte[1024];
						packet = new DatagramPacket(buf, buf.length);
						mSocket.receive(packet);
						String data = new String(buf);
						String[] splitData = data.split("_");
						boolean cond = (splitData[0].equals("SERVER") && splitData[1].equals("SEARCH") && splitData[2]
								.equals("REPLY"));
						if (cond) {
							publishProgress(new ServerInfo(packet.getAddress().toString(),
									Integer.parseInt(splitData[3])));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(ServerInfo... values) {
		mCallback.serverFound(values[0]);
		super.onProgressUpdate(values);
	}

	public void resend() {
		// TODO readd the iteration counter
		// and unpause if paused

	}

}
