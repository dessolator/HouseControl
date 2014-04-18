package me.kesler.homecontrol;

public class BasicControler implements Listable {
	String name;
	String offImageName;
	String onImageName;
	int pinNumber;
	boolean state;
	
	public BasicControler(String name, String offImageName, String onImageName,
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
