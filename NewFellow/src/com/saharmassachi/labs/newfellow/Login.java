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
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import static com.saharmassachi.labs.newfellow.Constants.MYKEY;
import static com.saharmassachi.labs.newfellow.Constants.PREFSNAME;
import static com.saharmassachi.labs.newfellow.Constants.BID;


public class Login extends Activity {
	
	long badgeID;
	// TODO Address a;
	String fbid;
	String twitterid;
	DBhelper h;
	UUID key;
	private EditText etBadge;
	private EditText etName;
	
	private EditText[] ets;
	private KeyListener[] listeners;
	private InputFilter[][] filters;
	private TextView topName;
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

	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_badge);
		h = new DBhelper(this);
		etBadge = (EditText) findViewById(R.id.etBadgeID);
		geocoder = new Geocoder(this);
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
		String names = etName.getText().toString();
		if((names == null) || (names.length() < 1)){
			dialog.cancel();
			return;
		}
		String[] n = names.split(" ", 2);
		if((n == null) || (n.length != 2)){
			dialog.cancel();
			Toast.makeText(this, "Please use a firstname and lastname", Toast.LENGTH_SHORT).show();
			return;
			
		}
		fname = n[0];
		lname = n[1];
		dialog.show();
		//long bdg = //h.registerDevice(badgeID, etName.getText().toString() );
		dialog.dismiss();
		setContentView(R.layout.login);
		goLoginView();
		
	}
	
	public void save(View v){
		uploadMyInfo();
		Intent i = new Intent(this, FriendsMap.class);
		startActivity(i);
	}
	
	private void goLoginView(){
		ets = new EditText[numRows];
		topName = (TextView) findViewById(R.id.topName);
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
	

	
	private void uploadMyInfo() {
		// TODO in the future this will not call h.getAllAttendees but a different method.
		String[] newnames = ets[0].getText().toString().split(" ", 2);
		if((newnames == null) || (newnames[1] == null)){
			Toast.makeText(this, "Please use a firstname and lastname", Toast.LENGTH_SHORT).show();
			return;
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
		
		key = UUID.randomUUID();
		final Context ctx = this;
		/*Runnable r = new Runnable() {
			@Override
			public void run() {
				Contact me = new Contact(BID, fname, lname);
				me.setFbid("sahar.massachi");
				me.setPhone("555989009");
				me.setTwitter("sayhar");
				h.uploadMyInfo(me);
				
				//so we only do this once.
				SharedPreferences settings = ctx.getSharedPreferences(PREFSNAME, 0);
				Editor e = settings.edit();
				e.putString(MYKEY, key.toString());
				e.commit();
			}
		};
		new Thread(r).start();*/
	}
	
	public void getAddressInfo(View v) {
		keyboardclear();
		Toast t = Toast.makeText(this,
				"Address not recognized, please try again", Toast.LENGTH_LONG);
		
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

				}
				if(addressList.size() == 0){
					t.show();
				}
				adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, addressList);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
	
}
