package com.saharmassachi.labs.newfellow;

import static com.saharmassachi.labs.newfellow.Constants.FNAME;
import static com.saharmassachi.labs.newfellow.Constants.LNAME;
import static com.saharmassachi.labs.newfellow.Constants.CID;
import android.app.Activity;
import android.os.Bundle;


public class ShowContact extends Activity {
	
	DBhelper helper;
	String name;
	long id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showcontact);
		helper = new DBhelper(this);
		Bundle extras = getIntent().getExtras();
		String fname = extras.getString(FNAME);
		String lname = extras.getString(LNAME);
		long id = extras.getLong(CID);
		
	}
}
