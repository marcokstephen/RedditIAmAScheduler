package com.sm.redditiamascheduler;

public class Person {
	private String name;
	private String day;
	private String time;
	private String description;
	private String infoURL;
	
	public Person(String name, String day, String time, String description, String infoURL) {
		super();
		this.name = name;
		this.day = day;
		this.time = time;
		this.description = description;
		this.infoURL = infoURL;
	}
	
	public String getName() {
		return name;
	}
	public String getDay() {
		return day;
	}
	public String getTime() {
		return time;
	}
	public String getDescription(){
		return description;
	}
	public String getInfoURL() {
		return infoURL;
	}
	
	
	
	
}


