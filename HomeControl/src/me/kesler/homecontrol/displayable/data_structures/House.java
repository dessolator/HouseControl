package me.kesler.homecontrol.displayable.data_structures;

import me.kesler.homecontrol.displayable.Listable;

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
