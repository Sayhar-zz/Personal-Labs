package com.saharmassachi.droid.compass;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


//CompassActivity does all the calculation of how to rotate, then tells pointerview to do the actual painting.
public class CompassActivity extends Activity implements OnClickListener {
	private LocationManager gpsLM;
	private LocationListener LL;
	TextView g;
	TextView top;
	TextView gps2;
	TextView degrees;
	Button anchorButton;
	Button compassButton;
	private Location carLocation; 
	private Location roamer;
	private boolean anchor;
	private double lat;
	private double lon;
	private boolean compassOn;
	private SensorManager mSensorManager;
    private Sensor mSensor;
    private float[] mValues = {-200, 0, 0};
    GeomagneticField geoField;
    Handler h;
    PointerView pointerview;
    private boolean nogps;
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		init();
	}
	
	private void init(){
		//anchor is for when you've told the activity "ok here's my car"
		
		anchor = false;
		h = new Handler();
		g = (TextView) findViewById(R.id.GPS_blank);
		g.setText("GPS IS BLAH");
		nogps = true;
		
		top = (TextView) findViewById(R.id.top);
		gps2 = (TextView) findViewById(R.id.GPS_2); 
		degrees = (TextView) findViewById(R.id.degrees);
		
		pointerview = (PointerView) findViewById(R.id.sbl);
		anchorButton = (Button) findViewById(R.id.anchor);
		anchorButton.setOnClickListener(this);
		
		compassButton = (Button) findViewById(R.id.startCompass);
		compassButton.setOnClickListener(this);
	
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}

	
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.anchor:
			//if they press the anchor button.
			if(!anchor && !nogps ) //stops you from pressing multiple times
			{
				anchor = true;
				gpsLM.removeUpdates(LL);
				TextView t = (TextView) v;
				t.setText("ANCHORED.");
				
				top.setText("Your car is at");
			}
			break;
		
		case R.id.startCompass:
			if(!compassOn) //stops you from pressing lots of times
			{
				compassOn = true;
				setGPS();
				onCompassSensor();
			}
			break;
		}
		

	}
	
	
	private void setGPS() {
		gpsLM = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		LL = listener();
		gpsLM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, LL);
	}

	
	private LocationListener listener() {
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				makeUseOfNewLocation(location);
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};
		return locationListener;
	}
	
	

	private void makeUseOfNewLocation(Location location) {
		nogps = false;
		if(!anchor){
			lat = location.getLatitude();
			lon = location.getLongitude();
			carLocation = location;
			g.setText(lat + " , " + lon);
		}
		else if(compassOn){
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			//gps2.setText(lat + " , " + lon);
			roamer = location;
			paintCompass();
		}
	}

	private void endSensors(){
		gpsLM.removeUpdates(LL);
		mSensorManager.unregisterListener(mListener);
	}
	
	private void paintCompass(){
		//thank you stack overflow! http://stackoverflow.com/questions/4308262/calculate-compass-bearing-heading-to-location-in-android
		//Error HERE
		float bearing = roamer.bearingTo(carLocation);
		float heading = mValues[0];
		//don't forget to compensate for geomagnetic field later
		//float point = bearing - (bearing + heading);
		float point = bearing - heading;
		if (point<0) { point = point + 360;}
		while (point > 360) {
			point -= 360;
		
		} 
		//degrees.setText("Heading (Degrees you point away from north):" + String.valueOf(heading) + 
		//		"\n Bearing (Degrees car is away from north): " + String.valueOf(bearing) +
		//		"\n Compass should point: " + String.valueOf(point));
		postCompass(point);
	}
	
	
	private void postCompass(float point){
		final float value = point;
		Runnable runnable = new Runnable(){
    		public void run(){
    			h.post(new Runnable(){
    				public void run(){
    					pointerview.rotate(value);	
    				}	
    			});
    		}
		};
		new Thread(runnable).start();
		
	}
	
	
	private void onCompassSensor(){
		 mSensorManager.registerListener(mListener, mSensor,
	                SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		endSensors();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(!anchor || compassOn) {
			setGPS();
			onCompassSensor();
			
		}
		
	}


	 private final SensorEventListener mListener = new SensorEventListener() {
	        public void onSensorChanged(SensorEvent event) {
	            
	            mValues = event.values;
	            if(compassOn && (roamer != null)) paintCompass();
	            //if (mView != null) {
	            //    mView.invalidate();
	           // }
	        }

	        public void onAccuracyChanged(Sensor sensor, int accuracy) {
	        }
	    };
	    
	    
	  public void reset(View v){
		  init();
		  
	  }

}
