package com.example.houseremote.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class DBProvider extends ContentProvider {
	
	private static final String AUTHORITY = "com.example.houseremote.database.dbprovider";
	private static final int HOUSES = 1;
	private static final int HOUSE = 2;
	private static final int ROOMS = 3;
	private static final int ROOM = 4;
	private static final int CONTROLLERS = 5;
	private static final int CONTROLLER = 6;
    
    public static final Uri HOUSES_URI=Uri.parse("content://" + AUTHORITY + "/"+ DBHandler.HOUSE_TABLE_NAME);
    public static final Uri ROOMS_URI=Uri.parse("content://" + AUTHORITY + "/"+ DBHandler.ROOM_TABLE_NAME);
    public static final Uri CONTROLLERS_URI=Uri.parse("content://" + AUTHORITY + "/"+ DBHandler.CONTROLLER_INTERFACE_TABLE_NAME);
    
    
    
    private DBHandler mDBHandler;

	private static final UriMatcher mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
	 
	static
	    {
	        mUriMatcher.addURI(AUTHORITY, DBHandler.HOUSE_TABLE_NAME, HOUSES);
	        mUriMatcher.addURI(AUTHORITY, DBHandler.HOUSE_TABLE_NAME+"/#", HOUSE);
	        mUriMatcher.addURI(AUTHORITY, DBHandler.ROOM_TABLE_NAME, ROOMS);
	        mUriMatcher.addURI(AUTHORITY, DBHandler.ROOM_TABLE_NAME+"/#", ROOM);
	        mUriMatcher.addURI(AUTHORITY, DBHandler.CONTROLLER_INTERFACE_TABLE_NAME, CONTROLLERS);
	        mUriMatcher.addURI(AUTHORITY, DBHandler.CONTROLLER_INTERFACE_TABLE_NAME+"/#", CONTROLLER);
	    }
	
	@Override
	public boolean onCreate() {
		mDBHandler= new DBHandler(getContext());
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		  case HOUSES: 
		   return "vnd.android.cursor.dir/vnd.com.example.houseremote.dbprovider.houses";
		  case HOUSE: 
		   return "vnd.android.cursor.item/vnd.com.example.houseremote.dbprovider.houses";
		  case ROOMS: 
		   return "vnd.android.cursor.dir/vnd.com.example.houseremote.dbprovider.rooms";
		  case ROOM: 
		   return "vnd.android.cursor.item/vnd.com.example.houseremote.dbprovider.rooms";
		  case CONTROLLERS: 
		   return "vnd.android.cursor.dir/vnd.com.example.houseremote.dbprovider.controllers";
		  case CONTROLLER: 
		   return "vnd.android.cursor.item/vnd.com.example.houseremote.dbprovider.controllers";
		  default: 
		   throw new IllegalArgumentException("Unsupported URI: " + uri);
		  }
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mDBHandler.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		String id;
		switch (mUriMatcher.match(uri)) {
		case HOUSES:
			queryBuilder.setTables(DBHandler.HOUSE_TABLE_NAME);
			break;
		case HOUSE:
			queryBuilder.setTables(DBHandler.HOUSE_TABLE_NAME);
			id = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(DBHandler.HOUSE_ID + "=" + id);
			break;
		case ROOMS:
			queryBuilder.setTables(DBHandler.ROOM_TABLE_NAME);
			break;
		case ROOM:
			queryBuilder.setTables(DBHandler.ROOM_TABLE_NAME);
			id = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(DBHandler.ROOM_ID + "=" + id);
			break;
		case CONTROLLERS:
			queryBuilder.setTables(DBHandler.CONTROLLER_INTERFACE_TABLE_NAME);
			break;
		case CONTROLLER:
			queryBuilder.setTables(DBHandler.CONTROLLER_INTERFACE_TABLE_NAME);
			id = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(DBHandler.CONTROLLER_ID + "=" + id);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDBHandler.getWritableDatabase();
		long id;
		switch (mUriMatcher.match(uri)) {
		case HOUSES:
			id = db.insert(DBHandler.HOUSE_TABLE_NAME, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(HOUSES_URI + "/" + id);
		case ROOMS:
			id = db.insert(DBHandler.ROOM_TABLE_NAME, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(ROOMS_URI + "/" + id);
		case CONTROLLERS:
			id = db.insert(DBHandler.CONTROLLER_INTERFACE_TABLE_NAME, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(CONTROLLERS_URI + "/" + id);
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDBHandler.getWritableDatabase();
		int deleteCount;
		String id;
		switch (mUriMatcher.match(uri)) {
		case HOUSES:
			deleteCount = db.delete(DBHandler.HOUSE_TABLE_NAME, selection,
					selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return deleteCount;
		case HOUSE:
			id = uri.getPathSegments().get(1);
			selection = DBHandler.HOUSE_ID
					+ "="
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			deleteCount = db.delete(DBHandler.HOUSE_TABLE_NAME, selection,
					selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return deleteCount;
		case ROOMS:
			deleteCount = db.delete(DBHandler.ROOM_TABLE_NAME, selection,
					selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return deleteCount;
		case ROOM:
			id = uri.getPathSegments().get(1);
			selection = DBHandler.ROOM_ID
					+ "="
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			deleteCount = db.delete(DBHandler.ROOM_TABLE_NAME, selection,
					selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return deleteCount;
		case CONTROLLERS:
			deleteCount = db.delete(DBHandler.CONTROLLER_INTERFACE_TABLE_NAME, selection,
					selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return deleteCount;
		case CONTROLLER:
			id = uri.getPathSegments().get(1);
			selection = DBHandler.CONTROLLER_ID
					+ "="
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			deleteCount = db.delete(DBHandler.CONTROLLER_INTERFACE_TABLE_NAME, selection,
					selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return deleteCount;
			
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int updateCount;
		String id;
		SQLiteDatabase db = mDBHandler.getWritableDatabase();
		switch (mUriMatcher.match(uri)) {
		case HOUSES:
			updateCount = db.update(DBHandler.HOUSE_TABLE_NAME, values,
					selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return updateCount;
		case HOUSE:
			id = uri.getPathSegments().get(1);
			selection = DBHandler.HOUSE_ID
					+ "="
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			updateCount = db.update(DBHandler.HOUSE_TABLE_NAME, values,
					selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return updateCount;
		case ROOMS:
			updateCount = db.update(DBHandler.ROOM_TABLE_NAME, values,
					selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return updateCount;
		case ROOM:
			id = uri.getPathSegments().get(1);
			selection = DBHandler.ROOM_ID
					+ "="
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			updateCount = db.update(DBHandler.ROOM_TABLE_NAME, values,
					selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return updateCount;
		case CONTROLLERS:
			updateCount = db.update(DBHandler.CONTROLLER_INTERFACE_TABLE_NAME, values,
					selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return updateCount;
		case CONTROLLER:
			id = uri.getPathSegments().get(1);
			selection = DBHandler.CONTROLLER_ID
					+ "="
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			updateCount = db.update(DBHandler.CONTROLLER_INTERFACE_TABLE_NAME, values,
					selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return updateCount;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		  
	}

}
