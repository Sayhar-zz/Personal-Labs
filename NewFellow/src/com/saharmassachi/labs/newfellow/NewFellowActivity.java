package com.saharmassachi.labs.newfellow;


import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;



import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.R;



import com.facebook.android.Util;

import com.saharmassachi.labs.newfellow.AddFriendLoc;
import com.saharmassachi.labs.newfellow.book.BaseRequestListener;
import com.saharmassachi.labs.newfellow.book.LoginButton;
import com.saharmassachi.labs.newfellow.book.SessionEvents;
import com.saharmassachi.labs.newfellow.book.SessionEvents.AuthListener;
import com.saharmassachi.labs.newfellow.book.SessionEvents.LogoutListener;
import com.saharmassachi.labs.newfellow.book.SessionStore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewFellowActivity extends Activity {
	public static final String APP_ID = "234125573324281";

	private TextView mText;

	private LoginButton mLoginButton;

	private Button mRequestButton;
	String[] permissions = { "offline_access", "publish_stream", "user_photos",
			"publish_checkins", "photo_upload" };
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;	
	private DBhelper dbhelper;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        if (APP_ID == null) {
            Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " +
                    "specified before running this example: see Example.java");
        }
        
       	mText = (TextView) findViewById(R.id.maintext);
        mLoginButton = (LoginButton) findViewById(R.id.login);
        
    	mFacebook = new Facebook(APP_ID);
       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
       	
       	SessionStore.restore(mFacebook, this);
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());
        mLoginButton.init(this, mFacebook, permissions);
       	dbhelper = new DBhelper(this);
        
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }

    public class SampleAuthListener implements AuthListener {

        public void onAuthSucceed() {
            mText.setText("You have logged in! ");
         
        }

        public void onAuthFail(String error) {
            mText.setText("Login Failed: " + error);
        }
    }

    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
            mText.setText("Logging out...");
        }

        public void onLogoutFinish() {
            mText.setText("You have logged out! ");
         
        }
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
                NewFellowActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mText.setText("Hello there, " + name + "!");
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }
    
    
    public void downloadAllAttendees(View v){
    	dbhelper.downAllAttendees();
    }
    
    public void goAddNew(View v){
    	//Intent i = new Intent(this, AddName.class);
		Intent i = new Intent(this, FilterAttendees.class);
    	startActivity(i);
    }
    
    public void goSeeNearest(View v){
    	Intent i = new Intent(this, FriendsMap.class);
		startActivity(i);
    }
    
    public void requestButton(View v){
    	mAsyncRunner.request("me", new SampleRequestListener());
    }
    
}