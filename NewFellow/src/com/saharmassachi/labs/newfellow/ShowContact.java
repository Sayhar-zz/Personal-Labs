package com.saharmassachi.labs.newfellow;

import static com.saharmassachi.labs.newfellow.Constants.FNAME;
import static com.saharmassachi.labs.newfellow.Constants.LNAME;
import static com.saharmassachi.labs.newfellow.Constants.CID;
import static com.saharmassachi.labs.newfellow.Constants.RAWLOC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnFocusChangeListener;

public class ShowContact extends Activity {

	private DataHelper helper;
	private long id;
	private TextView[] tvs;
	private EditText[] ets;
	private TextView topName;
	private Spinner spinner;
	private EditText etaddress;

	private Button notesbutton;
	private boolean editMode = false;
	private Button reset;
	private Button savebutton;
	private int numRows = 4; // number of stanard textview :: edittext rows we
								// have
	private String fname;
	private String lname;
	private String phone;
	private String email;
	private String twitter;
	private String base;
	private String newnotestxt;
	private String oldnotestxt;
	private int oldlat;
	private int oldlong;
	private Contact toShow;
	private long cid;

	private ArrayList<String> addressList = new ArrayList<String>();
	private List<Address> allAddresses;
	private ArrayAdapter<String> adapter;
	private int whichAddress;
	private Geocoder geocoder;
	private Context ctx;

	private final int MAXRESULTS = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showcontact);
		helper = new DataHelper(this);
		Bundle extras = getIntent().getExtras();
		fname = extras.getString(FNAME);
		lname = extras.getString(LNAME);
		cid = extras.getLong(CID);
		geocoder = new Geocoder(this);
		ctx = this;
		getValues();
		loadViews();
		setViews();

	}

	private void loadViews() {
		notesbutton = (Button) findViewById(R.id.notesbutton);
		
		tvs = new TextView[numRows];
		ets = new EditText[numRows];
		// listeners = new KeyListener[numRows];
		// filters = new InputFilter[numRows][10];

		topName = (TextView) findViewById(R.id.topName);
		etaddress = (EditText) findViewById(R.id.etaddress);
		ets[0] = (EditText) findViewById(R.id.TextView1b);
		ets[1] = (EditText) findViewById(R.id.TextView2b);
		ets[2] = (EditText) findViewById(R.id.TextView3b);
		ets[3] = (EditText) findViewById(R.id.TextView4b);

		tvs[0] = (TextView) findViewById(R.id.TextView1a);
		tvs[1] = (TextView) findViewById(R.id.TextView2a);
		tvs[2] = (TextView) findViewById(R.id.TextView3a);
		tvs[3] = (TextView) findViewById(R.id.TextView4a);

		/*
		 * for(int i = 0; i < numRows; i++){ listeners[i] =
		 * ets[i].getKeyListener(); filters[i] = ets[i].getFilters(); }
		 */
		reset = (Button) findViewById(R.id.resetButton);
		savebutton = (Button) findViewById(R.id.saveButton);
		spinner = (Spinner) findViewById(R.id.spincheck);
		spinner.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus)
					v.setVisibility(View.GONE);
			}

		});
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView adapter, View v, int pos,
					long lng) {
				whichAddress = pos;
				savebutton.setVisibility(View.VISIBLE);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}

		});

	}

	private void getValues() {
		toShow = helper.getContact(cid);
		phone = toShow.getPhone();
		twitter = toShow.getTwitter();
		email = toShow.getEmail();
		base = toShow.getBase();
		oldlat = toShow.getLat();
		oldlong = toShow.getLong();
		oldnotestxt = toShow.getNotes();
	}

	private void setViews() {
		savebutton.setVisibility(View.INVISIBLE);
		topName.setText(fname + " " + lname);
		tvs[0].setText("Name:");
		ets[0].setText(fname + " " + lname);

		tvs[1].setText("Phone:");
		if (check(phone)) {
			ets[1].setText(phone);
		} else {
			ets[1].setText("");
			phone = "";
		}

		tvs[2].setText("Email:");
		if (check(email)) {
			ets[2].setText(email);
		} else {
			ets[2].setText("");
			email = "";
		}

		tvs[3].setText("Twitter:");
		if (check(twitter)) {
			ets[3].setText(twitter);
		} else {
			ets[3].setText("");
			twitter = "";
		}
		if (check(base)) {
			etaddress.setText(base);
		}
		if(check(oldnotestxt)){
			newnotestxt = oldnotestxt;
		}

		getAddressInfo(etaddress);

	}

	public void toggleEditMode(View v) {
		CheckBox b = (CheckBox) v;
		if (b.isChecked()) {
			editMode = true;
			// setEditables(editMode);
			// reset.setVisibility(View.VISIBLE);
		} else {
			editMode = false;
			// setEditables(editMode);
			// reset.setVisibility(View.INVISIBLE);
		}
	}

	/*
	 * private void setEditables(boolean m){ if(m){ for (int i=0; i<numRows;
	 * i++){ //ets[i].setKeyListener(listeners[i]);
	 * ets[i].setFilters(filters[i]);
	 * 
	 * /*ets[i].setFocusable(true); ets[i].setFocusableInTouchMode(true); //
	 * user touches widget on phone with touch screen ets[i].setClickable(true);
	 *//*
		 * } } else{ for (int i=0; i<numRows; i++){
		 * //ets[i].setKeyListener(null); ets[i].setFilters(new InputFilter[] {
		 * new InputFilter() { public CharSequence filter(CharSequence src, int
		 * start, int end, Spanned dst, int dstart, int dend) { return
		 * src.length() < 1 ? dst.subSequence(dstart, dend) : ""; } } });
		 * /*ets[i].setFocusable(false); ets[i].setFocusableInTouchMode(false);
		 * // user touches widget on phone with touch screen
		 * ets[i].setClickable(false);
		 */
	/*
	 * } }
	 * 
	 * }
	 */

	public void getAddressInfo(View v) {
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
				spinner.setAdapter(adapter);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void Save(View v) {
		// delete old value in the table
		// add this one instead.
		String[] newnames = ets[0].getText().toString().split(" ", 2);
		if (newnames[1] == null) {
			Toast.makeText(this, "Please use a firstname and lastname",
					Toast.LENGTH_SHORT).show();
			return;
		}
		String newfname = newnames[0];
		String newlname = newnames[1];
		String newphone = ets[1].getText().toString();
		String newemail = ets[2].getText().toString();
		String newtwitter = ets[3].getText().toString();
		int newlat = oldlat;
		int newlong = oldlong;
		String newbase = base;
		if (adapter != null) {
			Address a = allAddresses.get(whichAddress);
			newbase = helper.showBase(a);
			newlat = (int) (a.getLatitude() * 1E6);
			newlong = (int) (a.getLongitude() * 1E6);
		}
	
		// unless all the values are the same...
		if (!(newfname.equalsIgnoreCase(fname)
				&& newlname.equalsIgnoreCase(lname)
				&& newphone.equalsIgnoreCase(phone)
				&& newtwitter.equalsIgnoreCase(twitter)
				&& newemail.equalsIgnoreCase(email) 
				&& (oldlat == newlat) 
				&& (oldlong == newlong)
				&& newnotestxt.equalsIgnoreCase(oldnotestxt))) {
		
	
			Address a = allAddresses.get(whichAddress);
	
			newlat = (int) (a.getLatitude() * 1E6);
			newlong = (int) (a.getLongitude() * 1E6);
			Contact c = new Contact(toShow.getID(), newfname, newlname);
			if(!newemail.equalsIgnoreCase(email)){
				c.setEmail(newemail);
			}
			if(!newphone.equalsIgnoreCase(phone)){
				c.setPhone(newphone);
			}
			if(!twitter.equalsIgnoreCase(newtwitter)){
				c.setTwitter(newtwitter);
			}
			if(! (oldlat == newlat) && (oldlong == newlong)){
				c.setBase(newbase);
				c.setLat(newlat);
				c.setLong(newlong);
			}
			if(!newnotestxt.equalsIgnoreCase(oldnotestxt)){
				c.setNotes(newnotestxt);
			}
			
			email = newemail;
			phone = newphone;
			twitter = newtwitter;
			base = newbase;
			oldlat = newlat;
			oldlong = newlong;
	
			helper.editPrivate(c, cid);
			showSavedAlert();
		}
	
	}

	public void notes(View v){
	
		Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.notesdialog);
		dialog.setTitle("Notes");
		final EditText notes = (EditText) dialog.findViewById(R.id.notestext);
		if(check(newnotestxt)){
			notes.setText(newnotestxt);
		}
		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener(){


				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					newnotestxt = notes.getText().toString();
					dialog.dismiss();
				
				}

		    });
			
			
		
		dialog.show();
		
		
	}
	
	public void Reset(View v) {
		ets[0].setText(fname + " " + lname);
		if (phone != null) {
			ets[1].setText(phone);
		} else {
			ets[1].setText("");
			
		}
		if (email != null) {
			ets[2].setText(email);
		} else {
			ets[2].setText("");
		}
		if(oldnotestxt != null){
			newnotestxt = oldnotestxt;
		}
		else{
			newnotestxt = "";
		}
		if (twitter != null) {
			ets[3].setText(twitter);
			
		} else {
			ets[3].setText("");
		}
		if (base != null) {
			etaddress.setText(base);
		} else {
			etaddress.setText("");
		}
		if (adapter != null) {
			adapter.clear();
		}

	}

	private void showSavedAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Saved Changes").setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}
	
	
	private void delete(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Really delete this contact?").setCancelable(false)
			.setNegativeButton("NO",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			}) 	
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						helper.deleteContact(cid);
						Intent intent;
						intent = new Intent (ctx, ViewContacts.class);
						startActivity(intent);
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.contactmenu, menu);
	    return true;
	    
	    
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.delete:
	            delete();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private boolean check(String s){
		if((s != null) && (s.length() > 0)){
			return true;
		}
		return false;
	}
}
