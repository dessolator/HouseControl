package com.example.houseremote.interfaces;

import com.example.houseremote.database.AsyncQueryManager;

public interface QueryManagerProvider{
		AsyncQueryManager getQueryManager();

//		void restartFullStateRead();
	}