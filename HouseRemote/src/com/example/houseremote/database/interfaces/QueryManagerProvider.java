package com.example.houseremote.database.interfaces;

import com.example.houseremote.database.DataBaseQueryManager;

public interface QueryManagerProvider{
		DataBaseQueryManager getQueryManager();

//		void restartFullStateRead();
	}