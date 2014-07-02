package com.example.houseremote.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.example.houseremote.EditLightSwitchActivity;
import com.example.houseremote.R;
import com.example.houseremote.adapters.GridAdapter;
import com.example.houseremote.adapters.Listable;
import com.example.houseremote.data_classes.BasicController;
import com.example.houseremote.database.DBHandler;

/**
 * A placeholder fragment containing a simple view.
 */

public class ControllersFragment extends Fragment {

	private ArrayList<Listable> controllerList;
	private String houseName;
	private String roomName;
	private GridView myGrid;
	private String roomIp;

	// private RoomSelectionListener myCallback;

	public ControllersFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		controllerList = new ArrayList<Listable>();
		this.houseName = getArguments().getString("house_name");// TODO
																// hardcoded
		this.roomName = getArguments().getString("room_name");
		SQLiteDatabase db = new DBHandler(getActivity()).getReadableDatabase();// grab
																				// a
																				// database
		Cursor c = db
				.rawQuery(
						"SELECT * FROM controller_interface WHERE house_name='"
								+ houseName + "'AND room_name='" + roomName
								+ "'", null);// run query getting all the houses
		if (c != null) {// if the query got anything
			if (c.moveToFirst()) {// start from the beginning
				do {// TODO switch case
					controllerList.add(new BasicController(c.getString(c
							.getColumnIndex("controler_interface_name")),
							c.getString(c
									.getColumnIndex("controler_image_name")),
							c.getString(c
									.getColumnIndex("controler_image_name")),
							c.getInt(c.getColumnIndex("control_pin1_number")),
							true));// TODO on and off image supportadd the names
				} while (c.moveToNext());// and iterate as far as possible
			}
		}
		c = db.rawQuery("SELECT * FROM room WHERE house_name='" + houseName
				+ "' AND room_name='" + roomName + "'", null);
		if (c != null) {
			if (c.moveToFirst()) {
				roomIp = c.getString(c.getColumnIndex("controler_ip"));
			}
		}

		db.close();
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_controllers,
				container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
//		controllerList.add(new BasicController("moo", "moo", "moo", 4, true));
		myGrid = (GridView) getActivity().findViewById(R.id.controllerGrid);
		myGrid.setAdapter(new GridAdapter(getActivity(), controllerList));

		myGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO ACTUAL LOGIC
			}
		});
		registerForContextMenu(myGrid);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(
				R.menu.controller_fragment_context_menu, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();//yank the context menu's info
		if (item.getItemId() == R.id.action_edit_controller) {
			Log.d("CONTROLLER", "EDIT");
			String selectedType=null;
			Intent i=null;
			SQLiteDatabase db = new DBHandler(getActivity()).getReadableDatabase();//grab a database
			Cursor c=db.rawQuery("SELECT * FROM controler_interface WHERE house_name='"+houseName+"' AND room_name='"+roomName+"' AND controler_interface_name='"+((Listable)((GridView)getActivity().findViewById(R.id.controllerGrid)).getAdapter().getItem(info.position)).getName()+"'", null);
			if(c!=null){
				if(c.moveToFirst()){
					selectedType=c.getString(c.getColumnIndex("controler_type"));
				}
			}
			
			db.close();
			
			if(selectedType.equals("lightSwitch")){
				i = new Intent(getActivity(), EditLightSwitchActivity.class);
			}else
			if(selectedType.equals("outletSwitch")){
//				Intent i = new Intent(getApplicationContext(), EditOutletSwitchActivity.class);
			}else
			if(selectedType.equals("someSwitch")){
//				Intent i = new Intent(getApplicationContext(), EditSomeSwitchActivity.class);	
			}
			
			i.putExtra("roomName", roomName);//give info about the house
			i.putExtra("houseName", houseName);
			i.putExtra("lightSwitchName", ((Listable)((GridView)getActivity().findViewById(R.id.controllerGrid)).getAdapter().getItem(info.position)).getName());
			startActivity(i);//start the activity	   		
	   		return true;
		}
		if (item.getItemId() == R.id.action_delete_controller) {
			Log.d("CONTROLLER", "DELETE");
			// TODO START ASYNC TASK DELETING FROM DB
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.controller_fragment_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_add_a_controller) {
			Log.d("MOO", "controller");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	// TODO MANAGE NAVIGATION... IF ROOM FRAGMENT IS IN THE RIGHT UP EXITS APP
	// IF ROOM IS IN THE LEFT, MOVE ROOM TO THE RIGHT AND LOAD HOUSES TO THE
	// LEFT

}