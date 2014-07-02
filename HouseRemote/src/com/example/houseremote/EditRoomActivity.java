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

public class EditRoomActivity extends Activity {

	String roomName;
	String houseName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_room);
		Intent startIntent=getIntent();
		roomName=startIntent.getExtras().getString("roomName");
		houseName=startIntent.getExtras().getString("houseName");
		Button saveButton=(Button)findViewById(R.id.saveRoomButton);
		((EditText)findViewById(R.id.roomNameField)).setText(roomName);
		SQLiteDatabase db = new DBHandler(this).getReadableDatabase();//grab a database
		Cursor c=db.rawQuery("SELECT * FROM room WHERE room_name='"+roomName+"' AND house_name='"+houseName+"'",null);//run query getting all the houses
		if(c!=null){//if the query got anything
			if(c.moveToFirst()){//start from the begining
				((EditText)findViewById(R.id.roomIpField)).setText(c.getString(c.getColumnIndex("controler_ip")));//add the names
			}
		}
		
		
		db.close();
		
		saveButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				SQLiteDatabase db=new DBHandler(getApplicationContext()).getWritableDatabase();
				db.execSQL("UPDATE room SET "
						+ "room_name='"+((EditText)findViewById(R.id.roomNameField)).getText().toString()+"',"
								+ "controler_ip='"+((EditText)findViewById(R.id.roomIpField)).getText().toString()+"'"
								+ " WHERE room_name='"+roomName+"' AND house_name='"+houseName+"'");
				onBackPressed();
				db.close();
				
				
			}
			
		});
	}
	





	
	
	
}
