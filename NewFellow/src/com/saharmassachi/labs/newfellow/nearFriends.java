package com.saharmassachi.labs.newfellow;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class nearFriends extends Activity implements LocationListener {

	private TextView tv1;
	private TextView nearest;
	private TextView which;
	private DBhelper helper;
	private LocationManager mgr;
	private Location whereIam;
	private String best;
	private double lat = 0;
	private double lng= 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearfriends);
		
		helper = new DBhelper(this);
		tv1 = (TextView) findViewById(R.id.textView1);
		nearest = (TextView) findViewById(R.id.closestplace);
		which = (TextView) findViewById(R.id.friendname);
		
		
		mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		best = mgr.getBestProvider(criteria, true);
		whereIam = mgr.getLastKnownLocation(best);
		
		
		//findClosest();
	}
	
	
	private void updateLocation(Location location){
		lat = location.getLatitude();
		lng = location.getLongitude();
		
	}
	
	public void onLocationChanged(Location location) {
		updateLocation(location);
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Start updates (doc recommends delay >= 60000 ms)
		mgr.requestLocationUpdates(best, 15000, 1, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Stop updates to save power while app paused
		mgr.removeUpdates(this);
	}
	
	
	
	/*private void findClosest(){
		//given a point, find the closest entry in your db to it.		
		
		
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		
		List<Address> addresses;
		try {
			addresses = geocoder.getFromLocation(lat, lng, 1);
			
		}
		catch(Exception e){
			e.printStackTrace();
			while(lng == 0){}
			findClosest();
			return;
		}
		String[] s;
		
		try{
			Address a = addresses.get(0);
			String state = a.getAdminArea();
			s = helper.findClosestAddress(lat, lng, state);
		}
		catch(Exception e){
			//guess there is no state
			e.printStackTrace();
			s = helper.findClosestAddress(lat, lng);
		}
		
		
		
		//findClosestAddress return a tuple. s[0] = string representation of "where" s[1] = string representation of "who"
		nearest.setText("boogy boogy");
		which.setText("fix my hair!");
		
		nearest.setText(s[0]);
		which.setText(s[1]);
		
	}
	
	*/
}
