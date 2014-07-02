package com.example.houseremote;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.houseremote.database.DBHandler;

public class EditLightSwitchActivity extends Activity {

	String houseName;
	String roomName;
	String lightSwitchName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_light_switch);
		Intent startIntent=getIntent();
		roomName=startIntent.getExtras().getString("roomName");
		houseName=startIntent.getExtras().getString("houseName");
		lightSwitchName=startIntent.getExtras().getString("lightSwitchName");
		Button saveButton=(Button)findViewById(R.id.saveLightSwitchButton);
		((EditText)findViewById(R.id.lightSwitchNameField)).setText(lightSwitchName);
		SQLiteDatabase db = new DBHandler(this).getReadableDatabase();//grab a database
		Cursor c=db.rawQuery("SELECT * FROM controler_interface WHERE room_name='"+roomName+"' AND house_name='"+houseName+"' AND controler_interface_name='"+lightSwitchName+"'",null);//run query getting all the houses
		if(c!=null){//if the query got anything
			if(c.moveToFirst()){//start from the begining
				((EditText)findViewById(R.id.lightSwitchPinField)).setText(Integer.toString(c.getInt(c.getColumnIndex("control_pin1_number"))));//add the names
			}
		}
		
		
		db.close();
		
		saveButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				SQLiteDatabase db=new DBHandler(getApplicationContext()).getWritableDatabase();
				db.execSQL("UPDATE controler_interface SET "
						+ "controler_interface_name='"+((EditText)findViewById(R.id.lightSwitchNameField)).getText().toString()+"',"
								+ "control_pin1_number='"+((EditText)findViewById(R.id.lightSwitchPinField)).getText().toString()+"'"
								+ " WHERE room_name='"+roomName+"' AND house_name='"+houseName+"' AND controler_interface_name='"+lightSwitchName+"'");
				onBackPressed();
				db.close();
				
				
			}
			
		});
	}

	
}
