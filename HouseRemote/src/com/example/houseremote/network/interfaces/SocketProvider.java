package com.example.houseremote.network.interfaces;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public interface SocketProvider{
	public Socket acquireSocket() throws   UnknownHostException, IOException;
}