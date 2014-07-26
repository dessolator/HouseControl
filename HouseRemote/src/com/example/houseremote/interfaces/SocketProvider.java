package com.example.houseremote.interfaces;

import java.net.Socket;

public interface SocketProvider{
	public Socket getSocket();
	public void setSocket(Socket temp);
	public String getIp();
}