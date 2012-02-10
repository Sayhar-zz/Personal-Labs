package com.saharmassachi.labs.newfellow;

//This is step 2 in the add step - location

import android.app.Activity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.saharmassachi.labs.newfellow.NewFellowActivity.SampleRequestListener;
import com.saharmassachi.labs.newfellow.book.BaseRequestListener;
import com.saharmassachi.labs.newfellow.book.SessionStore;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import static com.saharmassachi.labs.newfellow.Constants.LOCATION_TABLE;
import static com.saharmassachi.labs.newfellow.Constants.NAME_TABLE;



public class AddFriendLoc extends Activity implements OnClickListener {
	public static final String APP_ID = "234125573324281";
	 
	 
	private EditText etaddress;
	
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
	private DBhelper helper;
	
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;
	
	private String name;
	
	private EditText etPhone;
	private TextView etEmail;
	private TextView etTweet;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		helper = new DBhelper(this);

		setContentView(R.layout.addfriends);

		
		etEmail = (TextView) findViewById(R.id.getemail);
		etTweet = (TextView) findViewById(R.id.gettwitter);
		etPhone = (EditText) findViewById(R.id.getphone);
		
		spincheck = (Spinner) findViewById(R.id.spincheck);
		addToDB = (Button) findViewById(R.id.friendDbAdd);
		etaddress = (EditText) findViewById(R.id.friendaddress);
		geocoder = new Geocoder(this);
		
		tvname = (TextView) findViewById(R.id.nametext);
		tv1 = (TextView) findViewById(R.id.textView1);
		addToDB.setOnClickListener(this);
		
		mFacebook = new Facebook(APP_ID);
	   	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		SessionStore.restore(mFacebook, this);
		
		Bundle extras = getIntent().getExtras();
		
		name = extras.getString("name");
		tvname.setText(name);
		
		trySetAddress();
		
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

	public void trySetAddress() {
		// name = name.substring(1, name.length()-1);
		try {
			String[] atnd = helper.getOneAttendee(name);
			String city = atnd[2];
			if (city != null) {
				etaddress.setText(city);
				//This happens every time. WHY? 
				//Oh well, O-R model will fi.
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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

				t = Toast.makeText(this, "all is well", Toast.LENGTH_SHORT);

				adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, addressList);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spincheck.setAdapter(adapter);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		t.show();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.friendDbAdd:
			String phone = etPhone.getText().toString().trim();
			String mail = etEmail.getText().toString().trim();
			String tweet = etTweet.getText().toString().trim();
			Address a = allAddresses.get(whichAddress);
			long i = helper.addAddress(a);
			Toast.makeText(this, "record " + i + " saved", Toast.LENGTH_LONG).show();
			tv1.setText(helper.quickanddirtyGetRecordGivenID(i, LOCATION_TABLE));
			
			long j = helper.addContact(name, phone, mail, tweet, i);
			tv1.append("name:");
			tv1.append(helper.quickanddirtyGetRecordGivenID(j, NAME_TABLE));
			break;
		}

	}
	
	public void requestButton(View v){
    	mAsyncRunner.request("me", new SampleRequestListener());
    }
	
	public class SampleRequestListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            try {
                // process the response here: executed in background thread
                Log.d("Facebook-Example", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                final String name = json.getString("name");

                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original
                // thread that created a view hierarchy can touch its views."
               AddFriendLoc.this.runOnUiThread(new Runnable() {
                    public void run() {
                        tv1.setText("Hello there, " + name + "!");
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }
}