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

import com.example.houseremote.database.AsyncQueryManager;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.AsyncQueryManager.ReplyListener;

public class EditHouseActivity extends Activity implements ReplyListener {

	private AsyncQueryManager mAsyncQueryManager;
	EditText houseNameField;
	EditText houseWifiNameField;
	String houseName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_house);

		Button saveButton = (Button) findViewById(R.id.saveHouseButton);

		Intent startIntent = getIntent();
		houseName = startIntent.getExtras().getString("houseName");

		houseNameField = ((EditText) findViewById(R.id.houseNameField));
		houseWifiNameField = ((EditText) findViewById(R.id.houseWifiField));

		houseNameField.setText(houseName);

		mAsyncQueryManager = new AsyncQueryManager(getContentResolver(), this);
		String selection = DBHandler.HOUSE_NAME + "=?";
		String[] selectionArgs = { houseName };
		mAsyncQueryManager.startQuery(0, null, DBProvider.HOUSES_URI, null,
				selection, selectionArgs, null);

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String selection = DBHandler.HOUSE_NAME + "=?";
				String[] selectionArgs = { houseName };

				ContentValues cv = new ContentValues();
				cv.put(DBHandler.HOUSE_NAME, houseNameField.getText()
						.toString());
				cv.put(DBHandler.HOUSE_WIFI_NAME, houseWifiNameField.getText()
						.toString());

				mAsyncQueryManager.startUpdate(0, null, DBProvider.HOUSES_URI,
						cv, selection, selectionArgs);

				onBackPressed();

			}

		});
	}

	@Override
	public void dataSetChanged() {
	}

	@Override
	public void replaceCursor(Cursor cursor,int token) {
		if (cursor != null) {// if the query got anything
			if (cursor.moveToFirst()) {// start from the begining
				houseWifiNameField.setText(cursor.getString(cursor
						.getColumnIndex("house_wifi_name")));// add the names
			}
		}
		cursor.close();

	}

}
