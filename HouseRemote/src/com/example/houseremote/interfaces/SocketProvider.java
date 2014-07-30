package com.example.houseremote.interfaces;

import java.net.Socket;

public interface SocketProvider{
	public Socket acquireSocket(int port);
}