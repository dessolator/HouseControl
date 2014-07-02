package com.example.houseremote.data_classes;

import com.example.houseremote.adapters.Listable;


public class BasicController implements Listable {
	String name;
	String offImageName;
	String onImageName;
	int pinNumber;
	boolean state;
	
	public BasicController(String name, String offImageName, String onImageName,
			int pinNumber, boolean state) {
		super();
		this.name = name;
		this.offImageName = offImageName;
		this.onImageName = onImageName;
		this.pinNumber = pinNumber;
		this.state = state;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public String getImageName() {
		if(state)
			return onImageName;
		return offImageName;
	}
}
