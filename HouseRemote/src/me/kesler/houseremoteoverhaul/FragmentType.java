package me.kesler.houseremoteoverhaul;

public enum FragmentType {
	LIGHTS(0),
	SWITCHES(1),
	MEDIA(2),
	APPLIANCES(3),
	ROOMS(4);
	
	private int value;
	
	private FragmentType(int value) {
		this.value=value;
	}
	
	public int getValue(){
		return value;
	}

}
