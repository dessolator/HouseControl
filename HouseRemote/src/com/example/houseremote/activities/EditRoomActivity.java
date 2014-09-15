package com.example.houseremote.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.houseremote.R;
import com.example.houseremote.R.id;
import com.example.houseremote.R.layout;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseQueryManager;
import com.example.houseremote.database.interfaces.ReplyListener;

public class EditRoomActivity extends ActionBarActivity implements ReplyListener {

	String roomName;
	String houseName;
	EditText roomNameField;
	EditText roomIpField;
	DataBaseQueryManager mAsyncQueryManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_edit_room);

		Intent startIntent = getIntent();
		Button saveButton = (Button) findViewById(R.id.saveRoomButton);
		roomName = startIntent.getExtras().getString(DBHandler.ROOM_NAME);
		houseName = startIntent.getExtras().getString(DBHandler.HOUSE_NAME);
		String selection = DBHandler.HOUSE_NAME + "=?" + " AND " + DBHandler.ROOM_NAME + "=?";
		String[] selectionArgs = { houseName, roomName };
		roomNameField = (EditText) findViewById(R.id.roomNameField);
		roomNameField.setText(roomName);
		roomIpField = (EditText) findViewById(R.id.roomIpField);
		mAsyncQueryManager = new DataBaseQueryManager(getContentResolver(), this);
		
		mAsyncQueryManager.startQuery(0, null, DBProvider.ROOMS_URI, null, selection, selectionArgs, null);

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String selection = DBHandler.ROOM_NAME + "=?" + " AND " + DBHandler.HOUSE_NAME + "=?";
				String[] selectionArgs = { roomName, houseName };
				ContentValues cv = new ContentValues();
				cv.put(DBHandler.ROOM_NAME, roomNameField.getText().toString());
				cv.put(DBHandler.CONTROLLER_IP, roomIpField.getText().toString());
				mAsyncQueryManager.startUpdate(0, null, DBProvider.ROOMS_URI, cv, selection, selectionArgs);
				onBackPressed();

			}

		});
	}

	@Override
	public void dataSetChanged(int token, Object o) {

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
				roomIpField.setText(cursor.getString(cursor.getColumnIndex(DBHandler.CONTROLLER_IP)));
			}
			cursor.close();
		}

	}

}
