package com.example.houseremote.database.interfaces;

import com.example.houseremote.database.DataBaseAsyncQueryHandler;

public interface DatabaseHandlerProvider {

	DataBaseAsyncQueryHandler getQueryManager();
	
}
