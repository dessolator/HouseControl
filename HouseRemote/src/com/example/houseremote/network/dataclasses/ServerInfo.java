package com.example.houseremote.network.dataclasses;


public class ServerInfo {

	int port;
	String ip;
	
	
	public int getPort() {
		return port;
	}
	public String getIp() {
		return ip;
	}
	
	public ServerInfo( String ip,int port) {
		super();
		this.port = port;
		this.ip = ip;
	}
}
