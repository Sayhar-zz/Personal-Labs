package com.saharmassachi.labs.newfellow;

import android.location.Address;

public class Location {
	String street;
	String zip;
	String zipsuffix;
	String city;
	String state;
	int lat;
	int lng;
	long locID;
	Address a;
	
	public Location(){}
	
	public Location(long id, Address add){
		locID = id;
		a = add;
	}
	
	public Location(long id, int la, int ln){
		locID = id;
		lat = la;
		lng = ln;
	}
	
	public void setLong(int ln){
		lng = ln;
	}
	
	public void setLat(int lat){
		this.lat = lat;
	}
	
	public void setZip(String z){
		zip = z;
	}
	
	public void setSuffix(String sfx){
		zipsuffix = sfx;
	}
	
	public void setState(String s){
		state = s;
	}
	
	public void setCity(String c){
		city = c;
	}
	
	//TODO a getter method (or many)
}
