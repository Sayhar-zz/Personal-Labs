package com.saharmassachi.labs.newfellow;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class ContactOverlayItem extends OverlayItem {

	long contact;
	
	public ContactOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	
	}
	
	public ContactOverlayItem(GeoPoint point, String title, String snippet, long cid) {
		super(point, title, snippet);
		contact = cid;
	
	}
	
	public long getContact(){
		return contact;
	}

}
