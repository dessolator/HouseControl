//package com.example.houseremote.network;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.net.UnknownHostException;
//
//import com.example.houseremote.fragments.HeadlessFragment;
//import com.example.houseremote.interfaces.SocketProvider;
//
//public class NetworkSet  implements SocketProvider{
//	NetworkListenerAsyncTask mNetworkListener;
//	NetworkSenderThread mNetworkSender;
//	String ip;
//	Socket mSocket;
//
//	public NetworkSet(HeadlessFragment hf, String ip) {
//		super();
//		this.ip=ip;
//		mNetworkListener= new NetworkListenerAsyncTask(this, hf, hf);
//		mNetworkSender= new NetworkSenderThread(this);
//	}
//	
//	public void init() throws UnknownHostException, IOException{
//		mSocket = new Socket(InetAddress.getByName(ip),55000);
//	}
//	
//	
//	public NetworkListenerAsyncTask getNetworkListener() {
//		return mNetworkListener;
//	}
//
//	public NetworkSenderThread getNetworkSender() {
//		return mNetworkSender;
//	}
//
//	public Socket getSocket() {
//		return mSocket;
//	}
//
////	@Override
////	public Socket acquireSocket() {
////		return mSocket;
////	}
//
//
//}
