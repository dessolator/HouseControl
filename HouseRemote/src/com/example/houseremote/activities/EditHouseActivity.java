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

public class EditHouseActivity extends ActionBarActivity implements ReplyListener {

	private DataBaseQueryManager mAsyncQueryManager;
	EditText houseNameField;
	EditText houseWifiNameField;
//	String houseName;
	long houseID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_edit_house);

		//Field inits
		Button saveButton = (Button) findViewById(R.id.saveHouseButton);
		Intent startIntent = getIntent();
		String selection = DBHandler.HOUSE_ID + "=?";
		houseID = startIntent.getLongExtra(DBHandler.HOUSE_ID, -1);
		String[] selectionArgs = { ""+houseID };
		
		houseNameField = ((EditText) findViewById(R.id.houseNameField));
		houseWifiNameField = ((EditText) findViewById(R.id.houseWifiField));
//		houseNameField.setText(houseName);
		mAsyncQueryManager = new DataBaseQueryManager(getContentResolver(), this);

		mAsyncQueryManager.startQuery(0, null, DBProvider.HOUSES_URI, null, selection, selectionArgs, null);

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String selection = DBHandler.HOUSE_ID + "=?";
				String[] selectionArgs = { ""+houseID };
				ContentValues cv = new ContentValues();
				cv.put(DBHandler.HOUSE_NAME, houseNameField.getText().toString());
				cv.put(DBHandler.HOUSE_WIFI_NAME, houseWifiNameField.getText().toString());
				mAsyncQueryManager.startUpdate(0, null, DBProvider.HOUSES_URI, cv, selection, selectionArgs);
				onBackPressed();

			}

		});
	}

	@Override
	public void dataSetChanged(int token, Object cookie) {		
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
				houseWifiNameField
						.setText(cursor.getString(cursor.getColumnIndex(DBHandler.HOUSE_WIFI_NAME)));
				houseNameField
				.setText(cursor.getString(cursor.getColumnIndex(DBHandler.HOUSE_NAME)));
			}
			cursor.close();
		}

	}


	

}