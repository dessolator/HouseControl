package com.example.houseremote.network.interfaces;

public interface ControlledThread {

	public boolean isThreadPaused();

	public boolean isThreadAlive();

	public void resumeThread();

	public void pauseThread();

	public void killThread();
}
