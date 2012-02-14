package com.saharmassachi.labs.newfellow;

public class Contact {
	long badgeID;
	String first;
	String last;
	String twitter;
	String fbid;
	String phone;
	String email;
	String basename;
	int lat;
	int lng;
	
	public Contact(){}
	
	public Contact(long id, String f, String l){
		badgeID = id;
		first = f;
		last = l;
	}
	
	public String getBase(){
		return basename;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getfirst(){
		return first;
	}
	
	public long getID(){
		return badgeID;
	}
	
	public int getLat(){
		return lat;
	}
	
	public int getLong(){
		return lng;
	}
	
	public String getlast(){
		return last;
	}
	
	public String getName(){
		return first + " " + last;
	}
	
	public String getPhone(){
		return phone;
	}
	
	public String getTwitter(){
		return twitter;
	}
	
	public void setBase(String b){
		basename = b;
	}
	
	public void setEmail(String e){
		email = e;
	}
	
	public void setFbid(String id){
		fbid = id;
	}
	
	public void setFirst(String f){
		first = f;
	}
	
	public void setID(long i){
		badgeID = i;
	}
	
	public void setLast(String l){
		last = l;
	}
	
	public void setLat(int lat){
		this.lat = lat;
	}
	
	public void setLong(int ln){
		lng = ln;
	}
	
	public void setPhone(String p){
		phone = p;
	}
	
	public void setTwitter(String t){
		twitter = t;
	}
	
	
}
