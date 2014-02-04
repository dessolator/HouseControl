package me.kesler.homecontrol;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class RoomsActivity extends Activity {
	
	private String houseName;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), 0, "Edit");
		menu.add(0, v.getId(), 0, "Delete");
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();//yank the context menu's info
		if(item.getTitle().equals("Edit")){
			Intent i = new Intent(getApplicationContext(), EditRoomActivity.class);//create intent
			Log.v("ROOMACTIVITY","roomName="+(String)((GridView)findViewById(R.id.roomGrid)).getAdapter().getItem(info.position));
			i.putExtra("roomName", (String)((GridView)findViewById(R.id.roomGrid)).getAdapter().getItem(info.position));//give info about the house
			i.putExtra("houseName", houseName);
			startActivity(i);//start the activity	   		
	   		return true;
	   	}
	   	if(item.getTitle().equals("Delete")){
	   		SQLiteDatabase db = new DBHandler(this).getWritableDatabase();//grab a database
	   		db.execSQL("DELETE FROM room WHERE room_name='"+(String)((GridView)findViewById(R.id.roomGrid)).getAdapter().getItem(info.position)+"' AND house_name='"+houseName+"'");
	   		db.close();
	   		onResume();
	   		return true;
	   	}
	   	return false;
	}
	@Override
	protected void onResume() {
		super.onResume();
		onCreate(null);
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rooms);
		// Show the Up button in the action bar.
		setupActionBar();
		ArrayList<String> roomList=new ArrayList<String>();
		houseName=getIntent().getExtras().getString("houseName");
		SQLiteDatabase db = new DBHandler(this).getReadableDatabase();//grab a database
		Cursor c=db.rawQuery("SELECT * FROM room WHERE house_name='"+houseName+"'",null);//run query getting all the houses
		if(c!=null){//if the query got anything
			if(c.moveToFirst()){//start from the beginning
				do{
					roomList.add(c.getString(c.getColumnIndex("room_name")));//add the names
				}while(c.moveToNext());//and iterate as far as possible
			}
		}
		
		
		db.close();
				
		GridView myGrid = (GridView) findViewById(R.id.roomGrid);//grab the gridview
		myGrid.setAdapter(new GridAdapter(this,roomList));//attach adapter to gridview
		myGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
 
                // Sending image id to FullScreenActivity
                Intent i = new Intent(getApplicationContext(), ControlersActivity.class);
                // passing array index
                i.putExtra("houseName", houseName);
                i.putExtra("roomName",((TextView)v.findViewById(R.id.gridItemText)).getText().toString() );
                startActivity(i);
            }
        });
		registerForContextMenu(myGrid);
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
		getMenuInflater().inflate(R.menu.rooms, menu);
		return true;
	}


	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_addRoom:
			SQLiteDatabase db = new DBHandler(this).getWritableDatabase();//grab database
			db.execSQL("INSERT INTO room(room_name,controler_ip,house_name) VALUES('NewRoom','','"+getIntent().getExtras().getString("houseName")+"')");//add a blank house
			Intent i = new Intent(getApplicationContext(), EditRoomActivity.class);//create intent
			i.putExtra("roomName", "NewRoom");//give info about the house
			i.putExtra("houseName", houseName);
			startActivity(i);//start the activity
			db.close();
	        return true;
			
		}
		return false;
	}

}



	

	

	
	
	



	
	
	
	


	
	



