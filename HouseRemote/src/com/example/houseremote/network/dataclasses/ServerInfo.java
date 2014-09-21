package com.example.houseremote.network.dataclasses;


public class ServerInfo {

	int port;
	String ip;
	String name;
	
	
	public int getPort() {
		return port;
	}
	public String getIp() {
		return ip;
	}
	
	public ServerInfo(String name, String ip,int port) {
		super();
		this.name = name;
		this.port = port;
		this.ip = ip;
	}
}
