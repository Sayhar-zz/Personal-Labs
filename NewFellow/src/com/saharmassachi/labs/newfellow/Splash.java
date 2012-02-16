package com.saharmassachi.labs.newfellow;

import static com.saharmassachi.labs.newfellow.Constants.MYID;
import static com.saharmassachi.labs.newfellow.Constants.PREFSNAME;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.EditText;

public class Splash extends Activity {

	private boolean firsttime;
	private DataHelper datahelper;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		datahelper = new DataHelper(this);
		firsttime = false;
		
		SharedPreferences settings = getSharedPreferences(PREFSNAME, 0);
		/*if (!settings.contains(MYID)) {
			downloadPublic();
			firsttime = true;
			Intent i = new Intent(this, Login.class);
			startActivity(i);
		}*/
		downloadPublic();
		
		Intent i = new Intent(this, FriendsMap.class);
		startActivity(i);
		
	}
	
	private void downloadPublic() {
		if(firsttime == false){
			//if it's not the first time you're running this, 
			//then the updates are short and can be down as a method  
			datahelper.downPublic();
		}
		if(firsttime == true){
			//if it's the first time you're running this, then
			//the updates are long and should be a thread
			Runnable r = new Runnable() {
				@Override
				public void run() {
					datahelper.downPublic();
				}
			};
			new Thread(r).start();
		}
		
		
	}
	
}
