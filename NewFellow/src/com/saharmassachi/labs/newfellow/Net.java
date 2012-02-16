package com.saharmassachi.labs.newfellow;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Formatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


//NetHelper is STATIC
//NetHelper should usually be called through DBhelper

public class Net {

	public static final String appKey = "VM5ROYOT7FUHFXZ65D";
	public static final String TAG = "NETHELPER";
	
	public static ArrayList<String[]> downAllAttendees(long since) {

		//TODO = change downAllAttendees to use heroku.
		//since is the unixtimestamp of the last time it downloaded. 
		//if since == 0 , this is the first time, so download them all.
		ArrayList<String[]> a = null;
		try {
			String page = "";
			HttpGet request = new HttpGet();
			String rootscampid = "2025659803";
			String baseURL = "http://www.eventbrite.com/json/event_list_attendees?app_key="
					+ appKey + "&id=" + rootscampid + "&count=10";

			request.setURI(new URI(baseURL));
			page = connectionHelper(request);
			a = parse(page);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return a;

	}

	private static ArrayList<String[]> parse(String in) {
		ArrayList<String[]> a = new ArrayList<String[]>();
		try {
			JSONObject wrapper = new JSONObject(in);
			JSONArray jarray = wrapper.getJSONArray("attendees");
			for (int i = 0; i < jarray.length(); i++) {
				// so each o is an attendee
				JSONObject o = jarray.getJSONObject(i);
				
				o = o.getJSONObject("attendee");
				String fname, lname;
				fname = o.getString("first_name"); 
				lname = o.getString("last_name");
				String city;
				String work;
				try{
					city = "'"+ o.getString("home_city") + "'";
					if(city.trim().equals("''")) city = null;
				}
				catch(Exception e){
					city = null;
				}
				try{
					work = "'" + o.getString("company") + "'";
					if(work.trim().equals("''")) work = null;
				}
				catch(Exception e){
					work = null;
				}
				String[] s = {fname, lname, city, work };
				a.add(s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;
	}

	private static String connectionHelper(HttpRequestBase request) {
		String page = "error"; // NOTICE: if you ever change this, change the
		// code in upload() that checks for "error" to
		// see if this worked or not.
		BufferedReader in = null;
		HttpClient client = new DefaultHttpClient();
		try {

			HttpResponse response = client.execute(request);
			// the following is all a way to easily read the resopose and put it
			// in a string.
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			page = sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return page;
	}

	public static void downPublic(){
		//TODO 
		Log.e(TAG, "DOWNPUBLIC stub");
	}
	
	public static void downPrivate(){
		//TODO 
		Log.e(TAG, "DOWNPRIVATE stub");
	}
	
	public static boolean login(String uid, Contact c){
		//TODO 
		Log.e(TAG, "login stub");
		return true;
	}
	
	public static void uploadNewContacts(){
		//TODO 
		Log.e(TAG, "upload new contacts stub");
		//remember to get the new badge numbers for those who don't have badges
	}
	
	
}
