package com.example.houseremote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBHandler extends SQLiteOpenHelper {

	/*
	 * TABLE NAMES
	 */
	public static final String HOUSE_TABLE_NAME = "house";
	public static final String ROOM_TABLE_NAME = "room";
	public static final String CONTROLLER_INTERFACE_TABLE_NAME = "controller_interface";
	
	/*
	 * HOUSE TABLE COLUMNS
	 */
	public static final String HOUSE_ID = BaseColumns._ID;
	public static final String HOUSE_NAME = "house_name";
	public static final String HOUSE_WIFI_NAME = "house_wifi_name";
	public static final String HOUSE_WIFI_TYPE = "house_wifi_type";
	public static final String HOUSE_IMAGE_NAME = "house_image_name";
	public static final String HOUSE_WIFI_PASS = "house_wifi_pass";
	/*
	 * ROOM TABLE COLUMNS
	 */
	public static final String ROOM_ID = BaseColumns._ID;
	public static final String ROOM_NAME = "room_name";
	public static final String ROOM_IMAGE_NAME = "room_image_name";
	public static final String CONTROLLER_IP = "controller_ip";
	/*
	 * CONTROLER TABLE COLUMNS
	 */
	public static final String CONTROLLER_ID = BaseColumns._ID;;
	public static final String CONTROLLER_INTERFACE_NAME = "controller_interface_name";
	public static final String CONTROLLER_IMAGE_NAME = "controller_image_name";
	public static final String CONTROLLER_TYPE = "controller_type";
	public static final String CONTROL_PIN1_NUMBER = "control_pin1_number";
	/*
	 * SQL CREATION STRINGS
	 */
	private static final String CREATE_HOUSE_TABLE = "CREATE TABLE "+ HOUSE_TABLE_NAME + "(" 
			+ HOUSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ HOUSE_NAME + " STRING UNIQUE NOT NULL , " 
			+ HOUSE_WIFI_NAME + " STRING NOT NULL, " 
			+ HOUSE_WIFI_TYPE + " STRING NOT NULL, "
			+ HOUSE_IMAGE_NAME + " STRING NOT NULL, "
			+ HOUSE_WIFI_PASS + " STRING NOT NULL" + ")";
	
	private static final String CREATE_ROOM_TABLE = "CREATE TABLE "
			+ ROOM_TABLE_NAME
			+ "("
			+ ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ ROOM_NAME	+ " STRING NOT NULL, "
			+ CONTROLLER_IP + " STRING NOT NULL, "
			+ HOUSE_NAME + " STRING NOT NULL, "
			+ ROOM_IMAGE_NAME + " STRING NOT NULL, "
			+ " FOREIGN KEY(" + HOUSE_NAME + ") REFERENCES " + HOUSE_TABLE_NAME	+ "(" + HOUSE_NAME + ") ON DELETE CASCADE ON UPDATE CASCADE,"
			+ "UNIQUE("+HOUSE_NAME+","+ROOM_NAME +")"
			+ ")";
	
	private static final String CREATE_CONTROLLER_TABLE = "CREATE TABLE " + CONTROLLER_INTERFACE_TABLE_NAME + "("
			+ CONTROLLER_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ CONTROLLER_INTERFACE_NAME	+ " STRING NOT NULL, "
			+ CONTROLLER_IP	+ " STRING NOT NULL, "
			+ CONTROL_PIN1_NUMBER + " INTEGER NOT NULL, "
			+ HOUSE_NAME + " STRING NOT NULL, "
			+ CONTROLLER_TYPE + " STRING NOT NULL, "
			+ CONTROLLER_IMAGE_NAME	+ " STRING NOT NULL, "
			+ ROOM_NAME	+ " STRING NOT NULL, "
			+ " UNIQUE(" + CONTROLLER_IP + "," + CONTROL_PIN1_NUMBER + "),"
			+ " FOREIGN KEY(" + ROOM_NAME + ","	+ HOUSE_NAME + ") REFERENCES " + ROOM_TABLE_NAME + "("	+ ROOM_NAME	+ "," + HOUSE_NAME + ") ON DELETE CASCADE ON UPDATE CASCADE,"
			+ " UNIQUE(" + CONTROLLER_INTERFACE_NAME + "," + ROOM_NAME + "," + HOUSE_NAME + ")"
			+ ")";
	
	
	

	public DBHandler(Context context) {
		super(context, "HomeRemote", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_HOUSE_TABLE);
		db.execSQL(CREATE_ROOM_TABLE);
		db.execSQL(CREATE_CONTROLLER_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CONTROLLER_INTERFACE_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ROOM_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + HOUSE_TABLE_NAME);
		onCreate(db);

	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

}
