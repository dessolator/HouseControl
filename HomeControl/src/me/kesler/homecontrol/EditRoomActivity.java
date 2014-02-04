package me.kesler.homecontrol;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.NavUtils;

public class EditRoomActivity extends Activity {

	String roomName;
	String houseName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_room);
		// Show the Up button in the action bar.
		setupActionBar();
		Intent startIntent=getIntent();
		roomName=startIntent.getExtras().getString("roomName");
		houseName=startIntent.getExtras().getString("houseName");
		Log.v("ROOMACTIVITY","roomName="+roomName);
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
	

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_room, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}




	
	
	
}
