package com.saharmassachi.labs.newfellow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import static com.saharmassachi.labs.newfellow.Constants.MYID;
import static com.saharmassachi.labs.newfellow.Constants.PREFSNAME;

public class Login extends Activity {
	String fname;
	String lname;
	long ID;
	String phone;
	// TODO Address a;
	String fbid;
	String twitterid;
	DBhelper h;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		h = new DBhelper(this);
	}
	
	public void goMain(View v){
		uploadMyInfo();
		Intent i = new Intent(this, FriendsMap.class);
		startActivity(i);
	}
	
	private void uploadMyInfo() {
		// TODO in the future this will not call h.getAllAttendees but a different method.
		fname = "Example";
		lname = "Jones";
		ID = 111;
		final Context ctx = this;
		Runnable r = new Runnable() {
			@Override
			public void run() {
				Contact me = new Contact(ID, fname, lname);
				me.setFbid("sahar.massachi");
				me.setPhone("555989009");
				me.setTwitter("sayhar");
				h.uploadMyInfo(me);
				
				//so we only do this once.
				SharedPreferences settings = ctx.getSharedPreferences(PREFSNAME, 0);
				Editor e = settings.edit();
				e.putLong(MYID, ID);
				e.commit();
			}
		};
		new Thread(r).start();
	}
}
