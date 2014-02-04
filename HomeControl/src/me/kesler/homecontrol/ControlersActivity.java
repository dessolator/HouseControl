package me.kesler.homecontrol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v4.app.NavUtils;

public class ControlersActivity extends Activity {
	private String houseName;
	private String roomName;
	private String roomIp;
	private PopupMenu addPopup;
	private Socket mySocket;
	private DataOutputStream outToServer;//output stream to server
	private DataInputStream inFromServer;//input stream from server
	
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controlers);
		// Show the Up button in the action bar.
		setupActionBar();
		
		
		Intent startIntent=getIntent();
		ArrayList<String> controlerList=new ArrayList<String>();
		houseName=startIntent.getExtras().getString("houseName");
		roomName=startIntent.getExtras().getString("roomName");
		
		
		SQLiteDatabase db = new DBHandler(this).getReadableDatabase();//grab a database
		Cursor c=db.rawQuery("SELECT * FROM controler_interface WHERE house_name='"+houseName+"'AND room_name='"+roomName+"'",null);//run query getting all the houses
		if(c!=null){//if the query got anything
			if(c.moveToFirst()){//start from the beginning
				do{
					controlerList.add(c.getString(c.getColumnIndex("controler_interface_name")));//add the names
				}while(c.moveToNext());//and iterate as far as possible
			}
		}
//		Log.v("ROOMACTIVITY","roomName="+roomName);
//		Log.v("ROOMACTIVITY","houseName="+houseName);
		c=db.rawQuery("SELECT * FROM room WHERE house_name='"+houseName+"' AND room_name='"+roomName+"'", null);
		if(c!=null){
			if(c.moveToFirst()){
//				Log.v("ROOMACTIVITY","Found the roomColumn");
				roomIp=c.getString(c.getColumnIndex("controler_ip"));
			}
		}
//		Log.v("ROOMACTIVITY","roomIp="+roomIp);
		
		db.close();
		
		new Thread(){
			public void run(){
				try {
					mySocket=new Socket(InetAddress.getByName(roomIp),55000);
					outToServer = new DataOutputStream(mySocket.getOutputStream());//initialize output stream to server
					inFromServer = new DataInputStream(mySocket.getInputStream());//initialize input stream from server
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		//TODO read what house it was from the intent
		//toss the house name into the nav bar?
		GridView myGrid = (GridView) findViewById(R.id.controlerGrid);//grab the gridview
		myGrid.setAdapter(new GridAdapter(this,controlerList));//attach adapter to gridview
		myGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	String controlerType=null;
            	int controlerPin1=0;
                SQLiteDatabase db = new DBHandler(getApplicationContext()).getReadableDatabase();//grab a database
                Cursor c=db.rawQuery("SELECT * FROM controler_interface WHERE house_name='"+houseName+"' AND room_name='"+roomName+"'AND controler_interface_name='"+((TextView)v.findViewById(R.id.gridItemText)).getText().toString()+"'", null);
        		if(c!=null){
        			if(c.moveToFirst()){
//        				Log.v("ROOMACTIVITY","Found the roomColumn");
        				controlerType=c.getString(c.getColumnIndex("controler_type"));
        				controlerPin1=c.getInt(c.getColumnIndex("control_pin1_number"));
        			}
        		}
        		Log.v("ROOMACTIVITY","controlerType="+controlerType);
        		Log.v("ROOMACTIVITY","controlerPin="+controlerPin1);
        		db.close();
        		if(controlerType==null) return;
        		if(controlerType.equals("lightSwitch")){
        			Log.v("ROOMACTIVITY","SUCCESS");
		        	try {
						outToServer.writeUTF("FLIP"+'_'+controlerPin1);
						outToServer.flush();//flush the stream
					} catch (IOException e) {
						e.printStackTrace();
					}//send the FLIP command to the server
        		}
            	//do some controler spesific thing here
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
		getMenuInflater().inflate(R.menu.controlers, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_addControler:
			View temp=findViewById(R.id.action_addControler);
			if(temp==null){
				temp=findViewById(R.id.controlersView);
			}
			addPopup=new PopupMenu(this, temp);
			addPopup.getMenu().add("Add LightSwitch");//TODO hardcoded string
			addPopup.getMenu().add("Add OutletSwitch");//TODO hardcoded string
			addPopup.getMenu().add("Add SomeOtherSwitch");//TODO hardcoded string
			addPopup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					if(item.getTitle().equals("Add LightSwitch")){
						SQLiteDatabase db = new DBHandler(getApplicationContext()).getWritableDatabase();//grab database
						db.execSQL("INSERT INTO controler_interface(controler_interface_name,controler_ip,control_pin1_number,house_name,controler_type,room_name) VALUES('New Lightswitch','"+roomIp+"',0,'"+houseName+"','lightSwitch','"+roomName+"')");
						db.close();
						Intent i=new Intent(getApplicationContext(),EditLightSwitchActivity.class);//TODO fix hardcode
						i.putExtra("lightSwitchName", "New Lightswitch");
						i.putExtra("houseName", houseName);
						i.putExtra("roomName", roomName);
						i.putExtra("roomIp", roomIp);
						startActivity(i);
						return true;
						
					}
					if(item.getTitle().equals("Add OutletSwitch")){
						return true;
						
					}
					if(item.getTitle().equals("Add SomeOtherSwitch")){
						return true;
						
					}
					Log.v("POPUP","addSomethingElse");
					return false;
				}
			});
			addPopup.show();
//			SQLiteDatabase db = new DBHandler(this).getWritableDatabase();//grab database
//			db.execSQL("INSERT INTO room(room_name,controler_ip,house_name) VALUES('NewRoom','','"+getIntent().getExtras().getString("houseName")+"')");//add a blank house
//			Intent i = new Intent(getApplicationContext(), EditRoomActivity.class);//create intent
//			i.putExtra("roomName", "NewRoom");//give info about the house
//			i.putExtra("houseName", houseName);
//			startActivity(i);//start the activity
//			db.close();
	        return true;
			
		}
		return false;
	}
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
			Intent i = new Intent(getApplicationContext(), EditLightSwitchActivity.class);//TODO fix hardcodecreate intent
			i.putExtra("roomName", roomName);//give info about the house
			i.putExtra("houseName", houseName);
			i.putExtra("lightSwitchName", (String)((GridView)findViewById(R.id.controlerGrid)).getAdapter().getItem(info.position));
			Log.v("CALLINGEDIT","houseName="+houseName);
			Log.v("CALLINGEDIT","roomName="+roomName);
			Log.v("CALLINGEDIT","lightSwitchName="+(String)((GridView)findViewById(R.id.controlerGrid)).getAdapter().getItem(info.position));
			startActivity(i);//start the activity	   		
	   		return true;
	   	}
	   	if(item.getTitle().equals("Delete")){
	   		SQLiteDatabase db = new DBHandler(this).getWritableDatabase();//grab a database
	   		db.execSQL("DELETE FROM controler_interface WHERE controler_interface_name='"+(String)((GridView)findViewById(R.id.controlerGrid)).getAdapter().getItem(info.position)+"' AND room_name='"+roomName+"' AND house_name='"+houseName+"'");
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
	
	
}
