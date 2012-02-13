package com.saharmassachi.labs.newfellow;


public class SimpleContact {
	private int lat;
	private int lng;
	private String name;
	private String rawloc;
	private long cid;
	private long lid;
	
	public SimpleContact(int la, int ln, String fname, String lname, String rawloc, long cid, long lid){
		this.name = name;
		this.rawloc = rawloc;
		this.cid = cid;
		this.lat = la;
		this.lng = ln;
		this.lid = lid;
	}
	
	public int getLat(){
		return lat;
	}
	
	public int getLong(){
		return lng;
	}
	
	public String getName(){
		return name;
	}
	
	public String getAddress(){
		return rawloc;
	}
	
	public String toString(){
		return name + "\n" + rawloc;
	}
	
	public long getCid(){
		return cid;
	}
	
	public long getLid(){
		return lid;
	}
}


