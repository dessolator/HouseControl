package com.example.houseremote.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.houseremote.R;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseAsyncQueryHandler;
import com.example.houseremote.database.interfaces.ReplyListener;

public class EditRoomActivity extends ActionBarActivity implements ReplyListener {

	long roomID;
	private EditText roomNameField;
	private DataBaseAsyncQueryHandler mAsyncQueryManager;
	private static final String selection = DBHandler.ROOM_ID + "=?";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_edit_room);

		roomID = getIntent().getExtras().getLong(DBHandler.ROOM_ID);
		
		String[] selectionArgs = {roomID+"" };
		roomNameField = (EditText) findViewById(R.id.roomNameField);
		mAsyncQueryManager = new DataBaseAsyncQueryHandler(getContentResolver(), this);
		mAsyncQueryManager.startQuery(0, null, DBProvider.ROOMS_URI, null, selection, selectionArgs, null);

		((Button) findViewById(R.id.saveRoomButton)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String selection = DBHandler.ROOM_ID + "=?";
				String[] selectionArgs = { roomID+"" };
				ContentValues cv = new ContentValues();
				cv.put(DBHandler.ROOM_NAME, roomNameField.getText().toString());
				mAsyncQueryManager.startUpdate(0, null, DBProvider.ROOMS_URI, cv, selection, selectionArgs);
				onBackPressed();

			}

		});
	}


	@Override
	public boolean onNavigateUp() {
		super.onBackPressed();
		return true;
	}

	@Override
	public void replaceCursor(Cursor cursor,Object o) {
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				roomNameField.setText(cursor.getString(cursor.getColumnIndex(DBHandler.ROOM_NAME)));
			}
			cursor.close();
		}

	}

	@Override
	public void reloadControllerData() {
		
	}

	@Override
	public void reloadHouseData() {
		
	}

	@Override
	public void reloadRoomData() {
		
	}

}
