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
import com.example.houseremote.database.AsyncQueryManager.ReplyListener;
import com.example.houseremote.database.DBHandler;
import com.example.houseremote.database.DBProvider;

public class EditLightSwitchActivity extends Activity implements ReplyListener {

	String houseName;
	String roomName;
	String lightSwitchName;
	AsyncQueryManager mAsyncQueryManager;
	EditText lightSwitchNameField;
	EditText lightSwitchPinField;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_light_switch);
		
		Intent startIntent=getIntent();
		roomName=startIntent.getExtras().getString("roomName");
		houseName=startIntent.getExtras().getString("houseName");
		lightSwitchName=startIntent.getExtras().getString("lightSwitchName");
		
		Button saveButton=(Button)findViewById(R.id.saveLightSwitchButton);
		
		lightSwitchNameField=((EditText)findViewById(R.id.lightSwitchNameField));
		lightSwitchNameField.setText(lightSwitchName);
		
		lightSwitchPinField=((EditText)findViewById(R.id.lightSwitchPinField));
		
		mAsyncQueryManager=new AsyncQueryManager(getContentResolver(), this);
		
		String[] projection={DBHandler.CONTROL_PIN1_NUMBER};
		String selection= DBHandler.HOUSE_NAME+"=?"+" AND "+DBHandler.ROOM_NAME+"=?"+" AND "+DBHandler.CONTROLLER_INTERFACE_NAME+"=?";
		String[] selectionArgs={houseName,roomName,lightSwitchName};
		mAsyncQueryManager.startQuery(0, null, DBProvider.CONTROLLERS_URI, projection, selection, selectionArgs, null);
		
		saveButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ContentValues cv=new ContentValues();
				cv.put(DBHandler.CONTROLLER_INTERFACE_NAME,lightSwitchNameField.getText().toString());
				cv.put(DBHandler.CONTROL_PIN1_NUMBER, lightSwitchPinField.getText().toString());
				String selection= DBHandler.HOUSE_NAME+"=?"+" AND "+DBHandler.ROOM_NAME+"=?"+" AND "+DBHandler.CONTROLLER_INTERFACE_NAME+"=?";
				String[] selectionArgs={houseName,roomName,lightSwitchName};
				mAsyncQueryManager.startUpdate(0, null, DBProvider.CONTROLLERS_URI, cv, selection, selectionArgs);
				
				onBackPressed();
				
				
			}
			
		});
	}
	@Override
	public void dataSetChanged() {}
	@Override
	public void replaceCursor(Cursor cursor) {
		if(cursor!=null){//if the query got anything
			if(cursor.moveToFirst()){//start from the begining
				lightSwitchPinField.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(DBHandler.CONTROL_PIN1_NUMBER))));//add the names
			}
			cursor.close();
		}
		
	}

	
}
