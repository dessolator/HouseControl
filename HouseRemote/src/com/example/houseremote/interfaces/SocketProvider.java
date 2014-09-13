package com.example.houseremote.interfaces;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public interface SocketProvider{
	public Socket acquireSocket(int port) throws   UnknownHostException, IOException;
}