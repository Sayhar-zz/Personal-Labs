package com.saharmassachi.labs.newfellow;


public class SimpleContact {
	private int lat;
	private int lng;
	private String name;
	private String rawloc;
	private long cid;
	private long nid;
	
	public SimpleContact(int la, int ln, String name, String rawloc, long cid, long nid){
		this.name = name;
		this.rawloc = rawloc;
		this.cid = cid;
		this.lat = la;
		this.lng = ln;
		this.nid = nid;
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
		return toString();
	}
	
	public String toString(){
		return rawloc;
	}
	
	public long returnCid(){
		return cid;
	}
	
	public long returnNid(){
		return nid;
	}
}


