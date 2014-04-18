package me.kesler.homecontrol.activities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import me.kesler.homecontrol.R;
import me.kesler.homecontrol.activities.edit_activities.EditLightSwitchActivity;
import me.kesler.homecontrol.database_manager.DBHandler;
import me.kesler.homecontrol.displayable.GridAdapter;
import me.kesler.homecontrol.displayable.Listable;
import me.kesler.homecontrol.displayable.data_structures.BasicControler;
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
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

public class ControlersActivity extends Activity {
	private String houseName;//name of the house these controllers belong to
	private String roomName;//name of the room these controllers belong to
	private String roomIp;//the ip of the server these controllers belong to
	private PopupMenu addPopup;//the popup menu that picks the type of controller
	private Socket mySocket;//the socket used to communicate with the server
	private DataOutputStream outToServer;//output stream to server
	private DataInputStream inFromServer;//input stream from server
	private ArrayList<Listable> controlerList;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controlers);
		Intent startIntent=getIntent();
		controlerList=new ArrayList<Listable>();
		houseName=startIntent.getExtras().getString("houseName");
		roomName=startIntent.getExtras().getString("roomName");
		setupActionBar();
		
		SQLiteDatabase db = new DBHandler(this).getReadableDatabase();//grab a database
		Cursor c=db.rawQuery("SELECT * FROM controler_interface WHERE house_name='"+houseName+"'AND room_name='"+roomName+"'",null);//run query getting all the houses
		if(c!=null){//if the query got anything
			if(c.moveToFirst()){//start from the beginning
				do{//TODO switch case
					controlerList.add(new BasicControler(c.getString(c.getColumnIndex("controler_interface_name")),c.getString(c.getColumnIndex("controler_image_name")),c.getString(c.getColumnIndex("controler_image_name")),c.getInt(c.getColumnIndex("control_pin1_number")),true));// TODO on and off image supportadd the names
				}while(c.moveToNext());//and iterate as far as possible
			}
		}
		c=db.rawQuery("SELECT * FROM room WHERE house_name='"+houseName+"' AND room_name='"+roomName+"'", null);
		if(c!=null){
			if(c.moveToFirst()){
				roomIp=c.getString(c.getColumnIndex("controler_ip"));
			}
		}
		
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
        				controlerType=c.getString(c.getColumnIndex("controler_type"));
        				controlerPin1=c.getInt(c.getColumnIndex("control_pin1_number"));
        			}
        		}
        		db.close();
        		if(controlerType==null) return;
        		if(controlerType.equals("lightSwitch")){
		        	try {
						outToServer.writeUTF("FLIP"+'_'+controlerPin1);
						outToServer.flush();//flush the stream
					} catch (IOException e) {
						e.printStackTrace();
					}//send the FLIP command to the server
		        	return;
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
		getActionBar().setTitle("Controllers");
		getActionBar().setSubtitle(houseName+" > "+roomName);
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
			addPopup.getMenu().add(R.string.action_addLightSwitch);
			addPopup.getMenu().add(R.string.action_addOutletSwitch);
			addPopup.getMenu().add(R.string.action_addSomeOtherSwitch);
			addPopup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					if(item.getTitle().equals(getString(R.string.action_addLightSwitch))){
						SQLiteDatabase db = new DBHandler(getApplicationContext()).getWritableDatabase();//grab database
						db.execSQL("INSERT INTO controler_interface(controler_interface_name,controler_ip,control_pin1_number,house_name,controler_type,controler_image_name,room_name) VALUES('"+getString(R.string.newLightSwitchName)+"','"+roomIp+"',0,'"+houseName+"','lightSwitch','"+"house"+"','"+roomName+"')");
						db.close();
						Intent i=new Intent(getApplicationContext(),EditLightSwitchActivity.class);
						i.putExtra("lightSwitchName", getString(R.string.newLightSwitchName));
						i.putExtra("houseName", houseName);
						i.putExtra("roomName", roomName);
						i.putExtra("roomIp", roomIp);
						startActivity(i);
						return true;
						
					}
					if(item.getTitle().equals(getString(R.string.action_addOutletSwitch))){
						return true;
						
					}
					if(item.getTitle().equals(getString(R.string.action_addSomeOtherSwitch))){
						return true;
						
					}
					return false;
				}
			});
			addPopup.show();
	        return true;
			
		}
		return false;
	}
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
			String selectedType=null;
			Intent i=null;
			SQLiteDatabase db = new DBHandler(this).getReadableDatabase();//grab a database
			Cursor c=db.rawQuery("SELECT * FROM controler_interface WHERE house_name='"+houseName+"' AND room_name='"+roomName+"' AND controler_interface_name='"+((Listable)((GridView)findViewById(R.id.controlerGrid)).getAdapter().getItem(info.position)).getName()+"'", null);
			if(c!=null){
				if(c.moveToFirst()){
					selectedType=c.getString(c.getColumnIndex("controler_type"));
				}
			}
			
			db.close();
			
			if(selectedType.equals("lightSwitch")){
				i = new Intent(getApplicationContext(), EditLightSwitchActivity.class);
			}else
			if(selectedType.equals("outletSwitch")){
//				Intent i = new Intent(getApplicationContext(), EditOutletSwitchActivity.class);
			}else
			if(selectedType.equals("someSwitch")){
//				Intent i = new Intent(getApplicationContext(), EditSomeSwitchActivity.class);	
			}
			
			i.putExtra("roomName", roomName);//give info about the house
			i.putExtra("houseName", houseName);
			i.putExtra("lightSwitchName", ((Listable)((GridView)findViewById(R.id.controlerGrid)).getAdapter().getItem(info.position)).getName());
			startActivity(i);//start the activity	   		
	   		return true;
	   	}
	   	if(item.getTitle().equals(getString(R.string.action_Delete))){
	   		SQLiteDatabase db = new DBHandler(this).getWritableDatabase();//grab a database
	   		db.execSQL("DELETE FROM controler_interface WHERE controler_interface_name='"+((Listable)((GridView)findViewById(R.id.controlerGrid)).getAdapter().getItem(info.position)).getName()+"' AND room_name='"+roomName+"' AND house_name='"+houseName+"'");
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
