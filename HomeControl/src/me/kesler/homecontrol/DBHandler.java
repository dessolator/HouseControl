package me.kesler.homecontrol;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

	public DBHandler(Context context){
		super(context, "HomeControl", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		
		arg0.execSQL("CREATE TABLE house("
				+ "house_name STRING PRIMARY KEY,"
				+ " house_wifi_name STRING NOT NULL,"
				+ " house_wifi_type STRING NOT NULL,"
				+ " house_wifi_pass STRING NOT NULL"
				+ ")");

		arg0.execSQL("CREATE TABLE room("
				+ "room_name STRING NOT NULL,"
				+ " controler_ip STRING NOT NULL,"
				+ " house_name STRING NOT NULL,"
				+ " FOREIGN KEY(house_name) REFERENCES house(house_name) ON DELETE CASCADE ON UPDATE CASCADE,"
				+ " PRIMARY KEY (house_name,room_name)"
				+ ")");

		
		arg0.execSQL("CREATE TABLE controler_interface("
				+ "controler_interface_name STRING NOT NULL,"
				+ " controler_ip STRING NOT NULL,"
				+ " control_pin1_number INTEGER NOT NULL,"
				+ " house_name STRING NOT NULL,"
				+ " controler_type STRING NOT NULL,"
				+ " room_name STRING NOT NULL,"
				+ " UNIQUE(controler_ip,control_pin1_number),"
				+ " FOREIGN KEY(room_name,house_name) REFERENCES room(room_name,house_name) ON DELETE CASCADE ON UPDATE CASCADE,"
				+ " PRIMARY KEY(controler_interface_name,room_name,house_name)"
				+ ")");		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS controler_interface");
        db.execSQL("DROP TABLE IF EXISTS room");
        db.execSQL("DROP TABLE IF EXISTS house");
        onCreate(db);

	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
	    super.onOpen(db);
	    if (!db.isReadOnly()) {
	        // Enable foreign key constraints
	        db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}

}
