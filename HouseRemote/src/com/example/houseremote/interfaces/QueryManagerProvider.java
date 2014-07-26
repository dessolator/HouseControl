package com.example.houseremote.interfaces;

import com.example.houseremote.database.DataBaseQueryManager;

public interface QueryManagerProvider{
		DataBaseQueryManager getQueryManager();

//		void restartFullStateRead();
	}