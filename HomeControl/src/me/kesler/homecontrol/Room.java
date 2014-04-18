package me.kesler.homecontrol;

public class Room implements Listable {
	public String name;
	public String imageName;
	public String ip;
	
	
	public Room(String name, String imageName, String ip) {
		super();
		this.name = name;
		this.imageName = imageName;
		this.ip = ip;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public String getImageName() {
		return imageName;
	}

}
