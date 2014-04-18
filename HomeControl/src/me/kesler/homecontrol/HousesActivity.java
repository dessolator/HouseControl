package me.kesler.homecontrol;

import java.util.ArrayList;

import me.kesler.homecontrol.database_manager.DBHandler;
import me.kesler.homecontrol.displayable.ListAdapter;
import me.kesler.homecontrol.displayable.Listable;
import me.kesler.homecontrol.displayable.data_structures.House;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class HousesActivity extends Activity {
	private ArrayList<Listable> houseList;

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
			Intent i = new Intent(getApplicationContext(), EditHouseActivity.class);//create intent to edit the house
			i.putExtra("houseName", 
					((Listable)((ListView)findViewById(R.id.houseGrid)).getAdapter().getItem(info.position)).getName()
					);//give info about the house
			startActivity(i);//start the activity	   		
	   		return true;
	   	}
	   	if(item.getTitle().equals(getString(R.string.action_Delete))){//if Delete was selected
	   		SQLiteDatabase db = new DBHandler(this).getWritableDatabase();//grab a database
	   		db.execSQL("DELETE FROM house WHERE house_name='"
	   				+ ((Listable)((ListView)findViewById(R.id.houseGrid)).getAdapter().getItem(info.position)).getName()+"'"
	   				);//remove the house from the database
	   		db.close();//close the database
	   		onResume();//TODO horrrrrrrrrribly inefficient refresh the view
	   		return true;
	   	}
	   	return false;
	}

	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		onCreate(null);//TODO inefficient to the point of pain just regenerate the window from scratch
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//default on create
		setupActionBar();//Set up the action bar
		setContentView(R.layout.activity_houses);//inflate the layout
		SQLiteDatabase db = new DBHandler(this).getReadableDatabase();//grab a database
		Cursor c=db.rawQuery("SELECT * FROM house",null);//run query getting all the houses
		houseList=new ArrayList<Listable>();//initialize the arraylist
		if(c!=null){//if the query got anything
			if(c.moveToFirst()){//start from the beginning
				do{
					houseList.add(
							new House(c.getString(c.getColumnIndex("house_name")),
									c.getString(c.getColumnIndex("house_image_name"))
									)
							);//add the houses and their images to the view
				}while(c.moveToNext());//and iterate as far as possible
			}
		}
		
		
		db.close();//close the database
		ListView myGrid = (ListView) findViewById(R.id.houseGrid);//grab the gridview
		myGrid.setAdapter(new ListAdapter(this,houseList));//attach adapter for the list of houses to gridview
		
		myGrid.setOnItemClickListener(new OnItemClickListener() {//attach an onClickListener to each grid element
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
 
                Intent i = new Intent(getApplicationContext(), RoomsActivity.class);//if a house was opened make the intent to show its rooms
                i.putExtra("houseName",((TextView)v.findViewById(R.id.gridItemText)).getText().toString());//add the name of the house to the intent
                startActivity(i);//start the room activity
            }
        });
		registerForContextMenu(myGrid);//attach a context menu to each grid item

	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setTitle("Houses");//change the title in the action bar
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.houses, menu);// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//being as the only menu item is Add A House no need to check for other options
		SQLiteDatabase db = new DBHandler(this).getWritableDatabase();//grab database
		db.execSQL("INSERT INTO house(house_name,house_wifi_name,house_wifi_type,house_wifi_pass,house_image_name) "
				+ "VALUES('"+getString(R.string.newHouseName)+"','kesler','WPA','12345678','house')");//add a blank house
		Intent i = new Intent(getApplicationContext(), EditHouseActivity.class);//create intent to edit the new house
		i.putExtra("houseName", getString(R.string.newHouseName));//give info about the house
		startActivity(i);//start the activity
		db.close();
        return true;
	}
	

}
