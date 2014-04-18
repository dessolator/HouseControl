package me.kesler.homecontrol.activities;

import java.util.ArrayList;

import me.kesler.homecontrol.R;
import me.kesler.homecontrol.activities.edit_activities.EditRoomActivity;
import me.kesler.homecontrol.database_manager.DBHandler;
import me.kesler.homecontrol.displayable.ListAdapter;
import me.kesler.homecontrol.displayable.Listable;
import me.kesler.homecontrol.displayable.data_structures.Room;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class RoomsActivity extends Activity {
	private ArrayList<Listable> roomList;
	private String houseName;//the name of the house the room belongs to
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);//create the context menu
		menu.add(0, v.getId(), 0, R.string.action_Edit);//add Edit button to menu
		menu.add(0, v.getId(), 0, R.string.action_Delete);//add Delete button to menu
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();//yank the context menu's info
		if(item.getTitle().equals(getString(R.string.action_Edit))){//if Edit was selected
			Intent i = new Intent(getApplicationContext(), EditRoomActivity.class);//create intent
			i.putExtra("roomName",
					((Listable)((ListView)findViewById(R.id.roomGrid)).getAdapter().getItem(info.position)).getName()
					);//give info about the room
			i.putExtra("houseName", houseName);//give info about the house
			startActivity(i);//start the activity	   		
	   		return true;
	   	}
	   	if(item.getTitle().equals(getString(R.string.action_Delete))){//if Delete was selected
	   		SQLiteDatabase db = new DBHandler(this).getWritableDatabase();//grab a database
	   		db.execSQL("DELETE FROM room WHERE room_name='"
	   				+((Listable)((ListView)findViewById(R.id.roomGrid)).getAdapter().getItem(info.position)).getName()
	   				+"' AND house_name='"+houseName+"'");//delete the selected room
	   		db.close();//close the database
	   		onResume();//TODO HELP MEEEEEE and refresh the view
	   		return true;
	   	}
	   	return false;
	}
	@Override
	protected void onResume() {
		super.onResume();
		onCreate(null);//TODO horrible hard refresh the view
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rooms);
		// Show the Up button in the action bar.
		
		roomList=new ArrayList<Listable>();
		houseName=getIntent().getExtras().getString("houseName");
		setupActionBar();
		SQLiteDatabase db = new DBHandler(this).getReadableDatabase();//grab a database
		Cursor c=db.rawQuery("SELECT * FROM room WHERE house_name='"+houseName+"'",null);//run query getting all the houses
		if(c!=null){//if the query got anything
			if(c.moveToFirst()){//start from the beginning
				do{
					roomList.add(new Room(c.getString(c.getColumnIndex("room_name")),c.getString(c.getColumnIndex("room_image_name")),c.getString(c.getColumnIndex("controler_ip"))));//add the names
				}while(c.moveToNext());//and iterate as far as possible
			}
		}
		
		
		db.close();
		ListView myGrid = (ListView) findViewById(R.id.roomGrid);//grab the gridview
		myGrid.setAdapter(new ListAdapter(this,roomList));//attach adapter to gridview
		myGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ControlersActivity.class);
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
		getActionBar().setTitle("Rooms");//set the Activity title
		getActionBar().setSubtitle(houseName);//set the active house name as the subtitle
		getActionBar().setDisplayHomeAsUpEnabled(true);//enable back navigation

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
			db.execSQL("INSERT INTO room(room_name,controler_ip,house_name,room_image_name) "
					+ "VALUES('"+getString(R.string.newRoomName)+"','','"
					+getIntent().getExtras().getString("houseName")+"','bed')"
					);//add a blank house
			Intent i = new Intent(getApplicationContext(), EditRoomActivity.class);//create intent
			i.putExtra("roomName", getString(R.string.newRoomName));//give info about the house
			i.putExtra("houseName", houseName);
			startActivity(i);//start the activity
			db.close();
	        return true;
			
		}
		return false;
	}

}



	

	

	
	
	



	
	
	
	


	
	



