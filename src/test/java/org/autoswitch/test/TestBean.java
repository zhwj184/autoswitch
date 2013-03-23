package org.autoswitch.test;

import java.util.List;

public class TestBean {
	
	private int id;
	
	private String name;
	
	private List<String> catList;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getCatList() {
		return catList;
	}

	public void setCatList(List<String> catList) {
		this.catList = catList;
	}

}
