package me.kesler.homecontrol;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class HousesActivity extends Activity {

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), 0, R.string.action_Edit);
		menu.add(0, v.getId(), 0, R.string.action_Delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();//yank the context menu's info
		if(item.getTitle().equals(getString(R.string.action_Edit))){
			Intent i = new Intent(getApplicationContext(), EditHouseActivity.class);//create intent
			i.putExtra("houseName", (String)((GridView)findViewById(R.id.houseGrid)).getAdapter().getItem(info.position));//give info about the house
			startActivity(i);//start the activity	   		
	   		return true;
	   	}
	   	if(item.getTitle().equals(getString(R.string.action_Delete))){
	   		SQLiteDatabase db = new DBHandler(this).getWritableDatabase();//grab a database
	   		db.execSQL("DELETE FROM house WHERE house_name='"+(String)((GridView)findViewById(R.id.houseGrid)).getAdapter().getItem(info.position)+"'");
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
		super.onCreate(savedInstanceState);//default on create
		setupActionBar();
		setContentView(R.layout.activity_houses);//inflate the layout
		SQLiteDatabase db = new DBHandler(this).getReadableDatabase();//grab a database
		Cursor c=db.rawQuery("SELECT * FROM house",null);//run query getting all the houses
		ArrayList<String> houseList=new ArrayList<String>();//initialize the arraylist
		ArrayList<String> houseImageList=new ArrayList<String>();//initialize the arraylist
		if(c!=null){//if the query got anything
			if(c.moveToFirst()){//start from the beginning
				do{
					houseList.add(c.getString(c.getColumnIndex("house_name")));//add the names
					houseImageList.add(c.getString(c.getColumnIndex("house_image_name")));
				}while(c.moveToNext());//and iterate as far as possible
			}
		}
		
		
		db.close();
		GridView myGrid = (GridView) findViewById(R.id.houseGrid);//grab the gridview
		myGrid.setAdapter(new GridAdapter(this,houseList,houseImageList));//attach adapter to gridview
		
		myGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
 
                Intent i = new Intent(getApplicationContext(), RoomsActivity.class);
                i.putExtra("houseName",((TextView)v.findViewById(R.id.gridItemText)).getText().toString() );
                startActivity(i);
            }
        });//set the onItemClickListener to the grid
		registerForContextMenu(myGrid);

	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setTitle("Houses");
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.houses, menu);// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//being as the only menu item is Add A House
		SQLiteDatabase db = new DBHandler(this).getWritableDatabase();//grab database
		db.execSQL("INSERT INTO house(house_name,house_wifi_name,house_wifi_type,house_wifi_pass,house_image_name) VALUES('"+getString(R.string.newHouseName)+"','kesler','WPA','12345678','house')");//add a blank house
		Intent i = new Intent(getApplicationContext(), EditHouseActivity.class);//create intent
		i.putExtra("houseName", getString(R.string.newHouseName));//give info about the house
		startActivity(i);//start the activity
		db.close();
        return true;
	}
	

}
