package com.saharmassachi.labs.newfellow;

public class Contact {
	long officialID;
	String first;
	String last;
	String twitter;
	String fbid;
	String phone;
	//Location l;
	
	public Contact(){}
	
	public Contact(long id, String f, String l){
		officialID = id;
		first = f;
		last = l;
	}
	
	public void setTwitter(String t){
		twitter = t;
	}
	
	public void setFbid(String id){
		fbid = id;
	}
	
	public void setPhone(String p){
		phone = p;
	}
	
	public void setFirst(String f){
		first = f;
	}
	
	public void setLast(String l){
		last = l;
	}
	
	public void setID(long i){
		officialID = i;
	}
	
	public String getName(){
		return first + " " + last;
	}
	
	public long getID(){
		return officialID;
	}
	
}
