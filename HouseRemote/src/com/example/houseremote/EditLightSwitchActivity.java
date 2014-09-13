package com.example.houseremote;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseQueryManager;
import com.example.houseremote.interfaces.ReplyListener;

public class EditLightSwitchActivity extends ActionBarActivity implements ReplyListener {

	String houseName;
	String roomName;
	String lightSwitchName;
	DataBaseQueryManager mAsyncQueryManager;
	EditText lightSwitchNameField;
	EditText lightSwitchPinField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_edit_light_switch);

		Intent startIntent = getIntent();
		Button saveButton = (Button) findViewById(R.id.saveLightSwitchButton);
		String[] projection = { DBHandler.CONTROL_PIN1_NUMBER };
		roomName = startIntent.getExtras().getString(DBHandler.ROOM_NAME);
		houseName = startIntent.getExtras().getString(DBHandler.HOUSE_NAME);
		lightSwitchName = startIntent.getExtras().getString(DBHandler.CONTROLLER_INTERFACE_NAME);
		String selection = DBHandler.HOUSE_NAME + "=?" + " AND " + DBHandler.ROOM_NAME + "=?" + " AND "
				+ DBHandler.CONTROLLER_INTERFACE_NAME + "=?";
		String[] selectionArgs = { houseName, roomName, lightSwitchName };

		lightSwitchNameField = ((EditText) findViewById(R.id.lightSwitchNameField));
		lightSwitchNameField.setText(lightSwitchName);
		lightSwitchPinField = ((EditText) findViewById(R.id.lightSwitchPinField));
		mAsyncQueryManager = new DataBaseQueryManager(getContentResolver(), this);

		mAsyncQueryManager.startQuery(0, null, DBProvider.CONTROLLERS_URI, projection, selection,
				selectionArgs, null);

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ContentValues cv = new ContentValues();
				cv.put(DBHandler.CONTROLLER_INTERFACE_NAME, lightSwitchNameField.getText().toString());
				cv.put(DBHandler.CONTROL_PIN1_NUMBER, lightSwitchPinField.getText().toString());
				String selection = DBHandler.HOUSE_NAME + "=?" + " AND " + DBHandler.ROOM_NAME + "=?"
						+ " AND " + DBHandler.CONTROLLER_INTERFACE_NAME + "=?";
				String[] selectionArgs = { houseName, roomName, lightSwitchName };
				mAsyncQueryManager.startUpdate(0, null, DBProvider.CONTROLLERS_URI, cv, selection,
						selectionArgs);
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
	public void replaceCursor(Cursor cursor, Object o) {
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				lightSwitchPinField.setText(Integer.toString(cursor.getInt(cursor
						.getColumnIndex(DBHandler.CONTROL_PIN1_NUMBER))));
			}
			cursor.close();
		}

	}

}
