package com.saharmassachi.labs.newfellow;

//This is stage one in the add step - name

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;

import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;


import com.saharmassachi.labs.newfellow.book.FriendsGetProfilePics;
import com.saharmassachi.labs.newfellow.book.SessionStore;
import com.saharmassachi.labs.newfellow.book.Utility;

public class AddName extends Activity implements OnItemClickListener {
	public static final String APP_ID = "234125573324281";
	private DBhelper helper;

	private TextView tvquery;
	private EditText etname;
	private TextView filler;

	ProgressDialog dialog;
	
	protected Handler h;
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;

	protected ListView friendsList;
	protected static JSONArray jsonArray;
	private String graph_or_fql;
	String apiResponse = "";
	private long friendid;
	private Intent nextPage;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addname);
		
		helper = new DBhelper(this);
		mFacebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		SessionStore.restore(mFacebook, this);

		tvquery = (TextView) findViewById(R.id.askName);
		etname = (EditText) findViewById(R.id.friendname);
		filler = (TextView) findViewById(R.id.filler);

		nextPage = new Intent(this, AddFriendLoc.class);
		
		
		
		h = new Handler();
		
		graph_or_fql = "fql";
		
		

	}

	public void search(View v){
	//When the search button is pressed, do a facebook friend search (on whatever is in the edittext) and then call the listadapter on THAT 
		try {
			callGraph();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void callGraph() {
		//graph:
		/*Bundle params = new Bundle();
		params.putString("fields", "name, picture, location");
		
		dialog = ProgressDialog.show(this, "", "please wait", true, true);

		dialog.show();
		mAsyncRunner.request("me/friends", params, frl);
		*/
		
		//fql:
		/*
		String query = "select name, current_location, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) order by name";
		String query2 = "select name, current_location, uid, pic_square from user where uid in (select name, current_location, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) order by name) AND first_name='matt'";
		
        String mquery = "'query1':'SELECT uid, rsvp_status FROM event_member WHERE eid=12345678'; 'query2': 'SELECT name, url, pic FROM profile WHERE id IN (SELECT uid FROM #query1)'";
        
        String query3 = String.format(" select name, current_location, uid, pic_square from user where " +
        		"uid in (select uid2 from friend where uid1=me()) " +
        		"AND uid in (SELECT uid FROM user WHERE contains('%s'))", etname.getText().toString());
        
        String query4 = String.format(" select name, current_location, uid, pic_square from user where " +
        		"contains('%s') AND " +
        		"uid in (select uid , name from user where uid in (select uid2 from friend where uid1=me())) ", etname.getText().toString());
        
        String query5 = String.format("select name, current_location, uid, pic_square from user where contains('%s') AND " +
        		"uid in (select uid2 from friend where uid1=me())", etname.getText().toString());
        
        String query6 = String.format("select name, current_location, uid, pic_square from user where " +
        		"uid in (select uid2 from friend where uid1=me()) AND " +
        		"'%s' IN (select name from user where uid in (select uid2 from friend where uid1=me()))", etname.getText().toString());*/
        
		 String query7 = String.format("select name, current_location, uid, pic_square from user WHERE " +
	        		"uid in (select uid2 from friend where uid1=me()) " + 
	        		" AND (strpos(lower(name), lower('%s')) >= 0)", etname.getText().toString());
	        
	        Bundle params = new Bundle();
	        params.putString("method", "fql.query");
	        params.putString("query", query7);
	        
			FriendsRequestListener frl = new FriendsRequestListener();
			mAsyncRunner.request(null, params, frl);
	        dialog = ProgressDialog.show(this, "", "please wait", true, true);
			dialog.show();
		}

	private void dispGraph() {
		try {
			if (graph_or_fql.equals("graph")) {
				jsonArray = new JSONObject(apiResponse).getJSONArray("data");
			} else {
				jsonArray = new JSONArray(apiResponse);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		friendsList = (ListView) findViewById(R.id.friends_list);
		friendsList.setOnItemClickListener(this);
		friendsList.setAdapter(new FriendListAdapter(this));

	}
	
	
	public void saveName(View v) {
		String text = etname.getText().toString();
		if (text.length() > 0) {
			nextPage.putExtra("name", text);
			
			startActivity(nextPage);
		}
	}

	/**
	 * Definition of the list adapter
	 */
	public class FriendListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		AddName friendsList;

		public FriendListAdapter(AddName friendsList) {
			this.friendsList = friendsList;
			if (Utility.model == null) {
				Utility.model = new FriendsGetProfilePics();
			}
			Utility.model.setListener(this);
			mInflater = LayoutInflater.from(friendsList.getBaseContext());
		}

		@Override
		public int getCount() {
			return jsonArray.length();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			JSONObject jsonObject = null;
			try {
				jsonObject = jsonArray.getJSONObject(position);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			View hView = convertView;
			if (convertView == null) {
				hView = mInflater.inflate(R.layout.friend_item, null);
				ViewHolder holder = new ViewHolder();
				holder.profile_pic = (ImageView) hView
						.findViewById(R.id.profile_pic);
				holder.name = (TextView) hView.findViewById(R.id.name);
				holder.info = (TextView) hView.findViewById(R.id.info);
				hView.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) hView.getTag();
			try {
				if (graph_or_fql.equals("graph")) {
					holder.profile_pic.setImageBitmap(Utility.model.getImage(
							jsonObject.getString("id"),
							jsonObject.getString("picture")));
				} else {
					holder.profile_pic.setImageBitmap(Utility.model.getImage(
							jsonObject.getString("uid"),
							jsonObject.getString("pic_square")));
				}
			} catch (JSONException e) {
				holder.name.setText("");
			}
			try {
				holder.name.setText(jsonObject.getString("name"));
			} catch (JSONException e) {
				holder.name.setText("");
			}
			try {
				if (graph_or_fql.equals("graph")) {
					holder.info.setText(jsonObject.getJSONObject("location")
							.getString("name"));
				} else {
					JSONObject location = jsonObject
							.getJSONObject("current_location");
					holder.info.setText(location.getString("city") + ", "
							+ location.getString("state"));
				}

			} catch (JSONException e) {
				holder.info.setText("");
			}
			return hView;
		}

	}

	class ViewHolder {
		ImageView profile_pic;
		TextView name;
		TextView info;
	}

	public class FriendsRequestListener extends
			com.saharmassachi.labs.newfellow.book.BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			dialog.dismiss();
			apiResponse = response;
			Runnable r = new Runnable(){
				@Override
				public void run(){
					dispGraph();
				}};
			h.post(r);
			
			
			
		}

		public void onFacebookError(FacebookError error) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(),
					"Facebook Error: " + error.getMessage(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

		try {
			final long friendId;
			if (graph_or_fql.equals("graph")) {
				friendId = jsonArray.getJSONObject(position).getLong("id");
			} else {
				friendId = jsonArray.getJSONObject(position).getLong("uid");
			}
			final String name = jsonArray.getJSONObject(position).getString(
					"name");

			new AlertDialog.Builder(this)
					.setTitle("Is this it?")
					.setMessage(
							String.format(
									"Is %1$s the person you are thinking of?",
									name))
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Bundle params = new Bundle();
									/*
									 * Source Tag: friend_wall_tag To write on a
									 * friend's wall, provide friend's UID in
									 * the 'to' parameter. More info on feed
									 * dialog:
									 * https://developers.facebook.com/docs
									 * /reference/dialogs/feed/
									 */
									params.putString("to",
											String.valueOf(friendId));
									friendid = friendId;
									nextPage.putExtra("friendid", friendid);
									nextPage.putExtra("name", name);
									startActivity(nextPage);

								}

							}).setNegativeButton(R.string.no, null).show();
		} catch (JSONException e) {
			showToast("Error: " + e.getMessage());
		}
	}

	private void showToast(String msg){
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		
	}
	
}
