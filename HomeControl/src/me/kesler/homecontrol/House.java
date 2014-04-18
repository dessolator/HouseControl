package me.kesler.homecontrol;

public class House implements Listable {
	String name;
	String imageName;
	

	public House(String name, String imageName) {
		super();
		this.name = name;
		this.imageName = imageName;
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
