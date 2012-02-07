package com.saharmassachi.labs.newfellow;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class FriendsMap extends MapActivity {

	protected MapView map; 
	int zoomLevel;
	GeoPoint center;
	MyLocationOverlay userLocationOverlay;
	protected MapController controller;
	int streetView;
	boolean goToMyLocation;
	ZoomPanListener zpl;
	protected Handler handler = new Handler();
	
	
	/**
	 * Initializes Activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		readIntents();
		

		//instantiates HappyData and creates an arraylist of all the bottles
		//HappyData datahelper = new HappyData(this);
		//ArrayList<HappyBottle> plottables = datahelper.getMyHistory();

	
		//initialize and display map view and user location
		initMapView();
		initMyLocation();
		goToMyLocation();
	

		center = new GeoPoint(-1,-1);
		zoomLevel = map.getZoomLevel();
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	
	//Starts tracking the users position on the map. 
	protected void initMyLocation() {
		userLocationOverlay = new MyLocationOverlay(this, map);
		userLocationOverlay.enableMyLocation();
		map.getOverlays().add(userLocationOverlay);  //adds the users location overlay to the overlays being displayed
	}
	
	
	
	protected void readIntents(){
		goToMyLocation = true;
	
	}
	
	//Finds and initializes the map view.
	protected void initMapView() {
		map = (MapView) findViewById(R.id.themap);
		controller = map.getController();	
		//checks streetView
		if (streetView == 1) {
			map.setStreetView(true);
			map.setSatellite(false);
		} else {
			map.setStreetView(false);
			map.setSatellite(true);	
		}
		map.invalidate();	

	
		map.setBuiltInZoomControls(false); //hides the default map zoom buttons so they don't interfere with the app buttons

	}
	
	
	protected boolean isMoved() {
		GeoPoint trueCenter =map.getMapCenter();
		int trueZoom = map.getZoomLevel();
		if(!((trueCenter.equals(center)) && (trueZoom == zoomLevel))){	
			//Log.d(TAG, "You moved!:" + center.toString() + " zoom: " + zoomLevel);
			return true;
		}else{
			return false;
		}}

	

	protected void goToMyLocation() {
		if (goToMyLocation == true) {
			userLocationOverlay.runOnFirstFix(new Runnable() {
				public void run() {
					// Zoom in to current location
					controller.animateTo(userLocationOverlay.getMyLocation());
					controller.setZoom(15); //sets the map zoom level to 15
				}
			});
		}
		map.getOverlays().add(userLocationOverlay); //adds the users location overlay to the overlays being displayed
	}

	//Our version of a listener - checks to see if the user moved.
	private class ZoomPanListener extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			while(true){
				if(zoomLevel != map.getZoomLevel()) {
					handler.post(new Runnable(){
						@Override
						public void run(){
							mapClear();
							zoomLevel = map.getZoomLevel();}
								
					});	}
				
				if(isMoved() ){
					drawRecentLocal();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}}}}
	
	
	private void drawRecentLocal(){
		if (!(isMoved() )){return;}
		//stub - to be filled in later 
	}
	
	protected void mapClear(){
		//stub - to be filled in later
		/*happyOverlay.emptyOverlay();
		sadOverlay.emptyOverlay();
		filter.clear();*/
	}
	
	
	//Disables MyLocation
	@Override
	protected void onPause() {
		super.onPause();
		userLocationOverlay.disableMyLocation();  
		synchronized (zpl){
			zpl.cancel(true);}
		super.onPause();
	}

	//Enables MyLocation
	@Override
	protected void onResume() {
		super.onResume();
		
		userLocationOverlay.enableMyLocation();
		zpl = new ZoomPanListener();
		zpl.execute(null);
	}


}