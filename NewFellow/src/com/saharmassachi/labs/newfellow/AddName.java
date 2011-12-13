package com.saharmassachi.labs.newfellow;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.saharmassachi.labs.newfellow.book.SessionStore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AddName extends Activity {
	public static final String APP_ID = "234125573324281";
	private DBhelper helper;
	
	private TextView tvquery;
	private EditText etname;
	private TextView filler;
	
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		helper = new DBhelper(this);

		setContentView(R.layout.addname);
		mFacebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		SessionStore.restore(mFacebook, this);
		
		tvquery = (TextView) findViewById(R.id.askName);
		etname = (EditText) findViewById(R.id.friendname);
		filler = (TextView) findViewById(R.id.filler);
		
		filler();
	}
	
	private void filler(){
		String s = "";
		for (int i = 0; i < 163; i++){
			s += " " + (s.length() > 5 ? s.substring(s.length() - 5 ) : s);
			int k = (i + 90)%225;
			s +=  (char) k ;
			
		}
		filler.setText(s);
		
	}
	
	public void saveName(View v){
		String text = etname.getText().toString();
		if(text.length() > 0){
			String first = text.split(" ")[0];
			String last = text.split(" ")[1];
			Intent i = new Intent(this, AddFriends.class);
			i.putExtra("First", first);
			i.putExtra("Last", last);
			startActivity(i);
		}
	}
}
