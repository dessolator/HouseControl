package com.example.houseremote;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.fragments.AsyncQueryManager;
import com.example.houseremote.fragments.AsyncQueryManager.ReplyListener;

public class EditRoomActivity extends Activity implements ReplyListener {

	String roomName;
	String houseName;
	EditText roomNameField;
	EditText roomIpField;
	AsyncQueryManager mAsyncQueryManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_room);

		Intent startIntent = getIntent();
		roomName = startIntent.getExtras().getString("roomName");
		houseName = startIntent.getExtras().getString("houseName");

		Button saveButton = (Button) findViewById(R.id.saveRoomButton);

		roomNameField = (EditText) findViewById(R.id.roomNameField);
		roomNameField.setText(roomName);

		mAsyncQueryManager = new AsyncQueryManager(getContentResolver(), this);
		String selection = DBHandler.HOUSE_NAME + "=?" + " AND "
				+ DBHandler.ROOM_NAME + "=?";
		String[] selectionArgs = { houseName, roomName };
		mAsyncQueryManager.startQuery(0, null, DBProvider.ROOMS_URI, null,
				selection, selectionArgs, null);

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String selection=DBHandler.ROOM_NAME+"=?"+" AND "+DBHandler.HOUSE_NAME+"=?";
				String[] selectionArgs={roomName,houseName};
				ContentValues cv=new ContentValues();
				cv.put(DBHandler.ROOM_NAME, roomNameField.getText().toString());
				cv.put(DBHandler.CONTROLLER_IP, roomIpField.getText().toString());
				
				mAsyncQueryManager.startUpdate(0, null, DBProvider.ROOMS_URI, cv, selection, selectionArgs);
				onBackPressed();

			}

		});
	}

	@Override
	public void dataSetChanged() {

	}

	@Override
	public void replaceCursor(Cursor cursor) {
		if (cursor != null) {// if the query got anything
			if (cursor.moveToFirst()) {// start from the begining
				roomIpField.setText(cursor.getString(cursor
						.getColumnIndex(DBHandler.CONTROLLER_IP)));// add
				// the
				// names
			}
		}
		cursor.close();

	}

}
