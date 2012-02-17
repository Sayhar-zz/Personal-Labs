package com.saharmassachi.labs.newfellow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import static com.saharmassachi.labs.newfellow.Constants.MYKEY;
import static com.saharmassachi.labs.newfellow.Constants.PREFSNAME;
import static com.saharmassachi.labs.newfellow.Constants.BID;
import static com.saharmassachi.labs.newfellow.Constants.MYID;


public class Login extends Activity {

	private static final String TAG = "login";
	long badgeID;
	String fbid;
	String twitterid;
	DataHelper h;
	UUID key;
	private EditText etBadge;
	private EditText etName;

	private EditText[] ets;
	private Spinner spinner;
	private EditText etaddress;
	private String fname;
	private String lname;
	private String phone;
	private String email;
	private String twitter;
	private String base;
	private ArrayList<String> addressList = new ArrayList<String>();
	private List<Address> allAddresses;
	private ArrayAdapter<String> adapter;
	private int whichAddress;
	private Geocoder geocoder;
	private final int MAXRESULTS = 5;
	private int numRows = 4; //number of stanard textview :: edittext rows we have
	private DataHelper datahelper;
	private View savebutton;
	


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_badge);
		h = new DataHelper(this);
		etBadge = (EditText) findViewById(R.id.etBadgeID);
		geocoder = new Geocoder(this);
		datahelper = new DataHelper(this);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				datahelper.downPublic();
			}};
		new Thread(r).start();
		
			
	}


	public void sendBID(View v){
		badgeID = Long.parseLong(etBadge.getText().toString());
		keyboardclear();
		setContentView(R.layout.login_name);
	}

	public void noBID(View v){
		badgeID = -1;
		//A badge ID of -1 means they don't have a badge ID;

		setContentView(R.layout.login_name);
		keyboardclear();
	}

	private void keyboardclear(){
		//this is just copy/pasted code to forcefully hide the software keybaord once. Just need it for some reason. Otherwise the numpad keyboad goes over into the name section.
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

	}
	public void Send(View v){
		etName = (EditText) findViewById(R.id.etName);
		ProgressDialog dialog = ProgressDialog.show(this, "",
				"Registering with central server", true, true);
		dialog.show();
		String names = etName.getText().toString();
		if((names == null) || (names.length() < 1)){
			dialog.cancel();
			return;
		}
		names = names.replaceAll("  ", " ");
		String[] n = names.split(" ", 2);
		if((n == null) || (n.length != 2)){
			dialog.cancel();
			Toast.makeText(this, "Please use a firstname and lastname", Toast.LENGTH_SHORT).show();
			return;

		}
		fname = n[0];
		lname = n[1];
		key = UUID.randomUUID();
		if(h.register(badgeID, key)){
			dialog.dismiss();
			setContentView(R.layout.login);
			goLoginView();
		}
		else{
			dialog.dismiss();
			Toast.makeText(this, "error registering. Are you sure you have the right badge id?", Toast.LENGTH_SHORT);
		}
		//long bdg = //h.registerDevice(badgeID, etName.getText().toString() );


	}

	public void save(View v){
		if(uploadMyInfo()){
			Intent i = new Intent(this, FriendsMap.class);
			startActivity(i);	
		}

		
	}

	private void goLoginView(){
		ets = new EditText[numRows];

		etaddress = (EditText) findViewById(R.id.etaddress);
		ets[0] = (EditText) findViewById(R.id.TextView1b);
		ets[1] = (EditText) findViewById(R.id.TextView2b);
		ets[2] = (EditText) findViewById(R.id.TextView3b);
		ets[3] = (EditText) findViewById(R.id.TextView4b);

		ets[0].setText(fname + " " + lname);

		spinner = (Spinner) findViewById(R.id.spincheck);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView adapter, View v,
					int pos, long lng) {				
				whichAddress = pos;
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}



	private boolean uploadMyInfo() {
		String[] newnames = ets[0].getText().toString().split(" ", 2);
		if((newnames == null) || (newnames[1] == null)){
			Toast.makeText(this, "Please use a firstname and lastname", Toast.LENGTH_SHORT).show();
			return false;
		}
		fname = newnames[0];
		lname = newnames[1];
		phone = ets[1].getText().toString();
		email = ets[2].getText().toString();
		twitter = ets[3].getText().toString();
		Address a = allAddresses.get(whichAddress);
		int newlat = (int) (a.getLatitude() * 1E6);
		int newlong =(int) ( a.getLongitude() * 1E6); 


		int j = a.getMaxAddressLineIndex();
		String toString = "";
		for (int i = 0; i <= j; i++) {
			toString += a.getAddressLine(i);
			toString += "\n";
		}
		String newbase = toString;


		final Context ctx = this;

		SharedPreferences settings = ctx.getSharedPreferences(PREFSNAME, 0);
		Editor e = settings.edit();
		e.putString(MYKEY, key.toString());
		e.commit();

		
		final Contact me = new Contact(badgeID, fname, lname);
		if(check(email)){
			me.setEmail(email);
		}
		if(check(phone)){
			me.setPhone(phone);
		}
		if(check(twitter)){
			me.setTwitter(twitter);
		}
		if(check(newbase)){
			me.setBase(newbase);
		}
		try{
			me.setLat(newlat);
			me.setLong(newlong);
		}
		catch(Exception ex){
			//if lat/long aren't initialized, then there is an error that we safely catch.
			Log.d(TAG, "lat/long are null. no worries");
		}

		final Toast t = Toast.makeText(this, "Something went wrong. Try re-entering your name to match the one on your badge.", Toast.LENGTH_LONG);

		if(h.login(me)){
			//so we only do this once.
			if(badgeID > -1){
				e.putString(MYID, String.valueOf(badgeID));
				e.commit();
				return true;
			}
			else{
				//What do we do if they don't enter a badge number?
				//TODO
				return true;
			}

		}
		else{
			//Login didn't work.
			t.show();
			return false;
		}
	}




public void getAddressInfo(View v) {
	keyboardclear();
	Toast t = Toast.makeText(this,
			"Address not recognized, please try again", Toast.LENGTH_LONG);

	savebutton = findViewById(R.id.goMain);
	try {
		allAddresses = geocoder.getFromLocationName(etaddress.getText()
				.toString(), MAXRESULTS);
		if (!allAddresses.isEmpty()) {
			addressList.clear();
			for (Address a : allAddresses) {
				StringBuilder s = new StringBuilder();
				for (int i = 0; i < a.getMaxAddressLineIndex(); i++) {
					s.append(a.getAddressLine(i)).append("\n");
				}
				String sss = s.toString();
				addressList.add(s.toString());
				
				savebutton.setVisibility(View.VISIBLE);
			}
			if(addressList.size() == 0){
				t.show();
			}
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, addressList);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
		}
		else{
			savebutton.setVisibility(View.INVISIBLE);
		}

	} catch (IOException e) {
		e.printStackTrace();
	}	
}

// given string s - is it not null and length > 0?
private boolean check(String s){
	if((s != null) && (s.length() > 1) && (s.trim() != "null")){
		return true;
	}
	return false;
}

}
