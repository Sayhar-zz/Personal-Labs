package com.saharmassachi.labs.newfellow;

//This is step 2 in the add step - location

import android.app.Activity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import static com.saharmassachi.labs.newfellow.Constants.BID;
import static com.saharmassachi.labs.newfellow.Constants.FNAME;
import static com.saharmassachi.labs.newfellow.Constants.LNAME;
import static com.saharmassachi.labs.newfellow.Constants.MANUAL;

public class AddFriendLoc extends Activity implements OnClickListener {
	
	
	private Spinner spincheck;
	private Button addToDB;
	private Geocoder geocoder;
	private TextView tv1;
	private TextView tvname;
	private final int MAXRESULTS = 5;

	// String addressList[] = new String[MAXRESULTS];
	private ArrayList<String> addressList = new ArrayList<String>();
	private List<Address> allAddresses;
	private ArrayAdapter<String> adapter;
	private int whichAddress;
	private DataHelper helper;
	
	private String fname;
	private String lname;
	
	private EditText etaddress;
	private EditText etPhone;
	private TextView etEmail;
	private TextView etTweet;
	
	private String oldp;
	private String oldb;
	private String olde;
	private String oldt;
	private int oldlat;
	private int oldlong;
	private int newlat;
	private int newlong;
	private Address newAddress;
	
	private long id;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		helper = new DataHelper(this);
		setContentView(R.layout.addfriends);

		loadViews();
		init();
		preLoad();
		
		
	}

	
	
	public void preLoad() {
		// name = name.substring(1, name.length()-1);
		//try to preload things
		try {
			//TODO
			if(id > 0){
				Contact c = helper.getOnePublic(id);
				oldp = c.getPhone();
				oldb = c.getBase();
				olde = c.getEmail();
				oldt = c.getTwitter();
				if(check(oldb)){
					etaddress.setText(oldb);
				}
				if(check(oldp)){
					etPhone.setText(oldp);
				}
				if(check(olde)){
					etEmail.setText(olde);
				}
				if(check(oldt)){
					etTweet.setText(oldt);
				}
				oldlat = c.getLat();
				oldlong = c.getLong();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getAddressInfo(View v) {
		//hide keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
		
		
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

				

				adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, addressList);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spincheck.setAdapter(adapter);
			}
			else{
				t.show();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.friendDbAdd:
			
			newAddress = allAddresses.get(whichAddress);
			newlat = (int) (newAddress.getLatitude() * 1E6);
			newlong = (int) (newAddress.getLongitude() * 1E6);
			
			
			Contact con = createContact();
			
			if(id < 0){
				con.setBase(etaddress.getText().toString());
			}
			
			long j = helper.putPrivate(con);
			Contact tmp = helper.getContact(j);
			
			tv1.append(tmp.toString());
			break;
		}

	}
	
	private Contact createContact(){
		//we only want to save those elements that have changed.
		//those that haven't, should be inhereted from public information, and therefore be left blank.
		
		Contact contact = new Contact(id, fname, lname);
		String phone = etPhone.getText().toString().trim();
		String mail = etEmail.getText().toString().trim();
		String tweet = etTweet.getText().toString().trim();
		
		if(check(phone) && !(phone.equalsIgnoreCase(oldp))){
			contact.setPhone(phone);	
		}
		if(check(mail) && !(mail.equalsIgnoreCase(olde))){
			contact.setEmail(mail);
		}
		if(check(tweet) && !(tweet.equalsIgnoreCase(oldt))){
			if(tweet.charAt(0) == '@'){
				tweet = tweet.substring(1);
			}
			contact.setTwitter(tweet);
		}
		if((oldlat != newlat) && (oldlong != newlong)){
			contact.setLat(newlat);
			contact.setLong(newlong);
			contact.setBase(etaddress.getText().toString());
		}
		if(id < 0){
			contact.setID(-1);
		}
		return contact;
		
	}
	
	
	
	
	
	
	
	
	private void loadViews(){
		etEmail = (TextView) findViewById(R.id.getemail);
		etTweet = (TextView) findViewById(R.id.gettwitter);
		etPhone = (EditText) findViewById(R.id.getphone);
		
		spincheck = (Spinner) findViewById(R.id.spincheck);
		addToDB = (Button) findViewById(R.id.friendDbAdd);
		etaddress = (EditText) findViewById(R.id.friendaddress);
		geocoder = new Geocoder(this);
		
		tvname = (TextView) findViewById(R.id.nametext);
		tv1 = (TextView) findViewById(R.id.textView1);
	}
	
	private void init(){
		addToDB.setOnClickListener(this);
		
		Bundle extras = getIntent().getExtras();
		
		fname = extras.getString(FNAME);
		if(extras.containsKey(LNAME)){
			lname = extras.getString(LNAME);
		}
		else{
			lname = ".";
		}
		if(extras.containsKey(MANUAL)){
			id = -1; // to show that it's not set yet;
		}
		else{
			id = extras.getLong(BID);
		}
		tvname.setText(fname + " " + lname);
		
		spincheck
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					public void onItemSelected(AdapterView adapter, View v,
							int pos, long lng) {
						// do something here
						Toast.makeText(
								spincheck.getContext(),
								"The location is "
										+ spincheck.getItemAtPosition(pos)
												.toString(), Toast.LENGTH_LONG)
								.show();
						whichAddress = pos;
						addToDB.setVisibility(View.VISIBLE);
					}
					public void onNothingSelected(AdapterView<?> parent) {
						// Do nothing.
						addToDB.setVisibility(View.INVISIBLE);
					}
				}
				);

	}
	
	// given string s - is it not null and length > 0?
	private boolean check(String s){
		if((s != null) && (s.length() > 1)){
			return true;
		}
		return false;
	}
	
}
