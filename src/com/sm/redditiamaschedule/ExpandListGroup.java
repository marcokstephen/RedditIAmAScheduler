package com.sm.redditiamaschedule;

import java.util.ArrayList;

public class ExpandListGroup {
	private String date;
	private String name;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	private String description;
	ArrayList<ExpandListChild> Child;

	public ArrayList<ExpandListChild> getChild() {
		return Child;
	}
	public void setChild(ArrayList<ExpandListChild> child) {
		Child = child;
	}
	
	
}
