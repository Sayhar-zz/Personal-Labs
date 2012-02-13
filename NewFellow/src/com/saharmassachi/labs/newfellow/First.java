package com.saharmassachi.labs.newfellow;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import static com.saharmassachi.labs.newfellow.Constants.MYID;
import static com.saharmassachi.labs.newfellow.Constants.PREFSNAME;

public class First extends Activity {
	DBhelper h = new DBhelper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first);
		makeDownloadThread();

	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences settings = getSharedPreferences(PREFSNAME, 0);
		if (!settings.contains(MYID)) {
			Intent i = new Intent(this, Login.class);
			startActivity(i);

		} else {
			Intent i = new Intent(this, FriendsMap.class);
			startActivity(i);
		}

	}

	private void makeDownloadThread() {
		// TODO in the future this will not call h.getAllAttendees but a different method.
		
		Runnable r = new Runnable() {
			@Override
			public void run() {
				h.downAllAttendees();
			}
		};
		new Thread(r).start();
	}
}
