package com.example.houseremote.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.houseremote.R;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;
import com.example.houseremote.database.DataBaseAsyncQueryHandler;
import com.example.houseremote.database.interfaces.DatabaseOperationCompleteListener;

/**
 * Activity used to edit simple light switch controllers.
 * 
 * @author Ivan Kesler
 * 
 */
public class EditLightSwitchActivity extends ActionBarActivity implements DatabaseOperationCompleteListener {

	private static final String selection = DBHandler.CONTROLLER_ID + "=?";
	private long controllerID;
	private DataBaseAsyncQueryHandler mAsyncQueryManager;
	private EditText lightSwitchNameField;
	private EditText lightSwitchPinField;
	private EditText lightSwitchIpField;
	private EditText lightSwitchPortField; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_edit_light_switch);

		String[] projection = { DBHandler.CONTROL_PIN_NUMBER, DBHandler.CONTROLLER_NAME,
				DBHandler.CONTROLLER_IP, DBHandler.CONTROLLER_PORT };
		controllerID = getIntent().getExtras().getLong(DBHandler.CONTROLLER_ID);
		String[] selectionArgs = { "" + controllerID };
		
		lightSwitchNameField = ((EditText) findViewById(R.id.lightSwitchNameField));
		lightSwitchPinField = ((EditText) findViewById(R.id.lightSwitchPinField));
		lightSwitchIpField = ((EditText) findViewById(R.id.lightSwitchIpField));
		lightSwitchPortField = ((EditText) findViewById(R.id.lightSwitchPortField));
		
		mAsyncQueryManager = new DataBaseAsyncQueryHandler(getContentResolver(), this);
		mAsyncQueryManager.startQuery(0, null, DBProvider.CONTROLLERS_URI, projection, selection,
				selectionArgs, null);

		((Button) findViewById(R.id.saveLightSwitchButton)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ContentValues cv = new ContentValues();
				cv.put(DBHandler.CONTROLLER_NAME, lightSwitchNameField.getText().toString());
				cv.put(DBHandler.CONTROL_PIN_NUMBER, lightSwitchPinField.getText().toString());
				cv.put(DBHandler.CONTROLLER_IP, lightSwitchIpField.getText().toString());
				cv.put(DBHandler.CONTROLLER_PORT,lightSwitchPortField.getText().toString());
				String[] selectionArgs = { "" + controllerID };
				mAsyncQueryManager.startUpdate(0, null, DBProvider.CONTROLLERS_URI, cv, selection,
						selectionArgs);
				onBackPressed();//TODO move to onUpdateFinished

			}

		});
	}


	@Override
	public boolean onNavigateUp() {
		super.onBackPressed();
		return true;
	}

	@Override
	public void onQueryFinished(Cursor cursor, CursorAdapter o) {
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				lightSwitchPinField.setText(cursor.getString(cursor.getColumnIndex(DBHandler.CONTROL_PIN_NUMBER)));
				lightSwitchNameField.setText(cursor.getString(cursor.getColumnIndex(DBHandler.CONTROLLER_NAME)));
				lightSwitchIpField.setText(cursor.getString(cursor.getColumnIndex(DBHandler.CONTROLLER_IP)));
				lightSwitchPortField.setText(cursor.getString(cursor.getColumnIndex(DBHandler.CONTROLLER_PORT)));
			}
			cursor.close();
		}

	}

	
	@Override
	public void onInsertFinished(long parseId, int token) {
		
	}


}
