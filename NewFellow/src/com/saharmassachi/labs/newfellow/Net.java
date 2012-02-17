package com.saharmassachi.labs.newfellow;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import static com.saharmassachi.labs.newfellow.Constants.FNAME;
import static com.saharmassachi.labs.newfellow.Constants.LNAME;
import static com.saharmassachi.labs.newfellow.Constants.PHONE;
import static com.saharmassachi.labs.newfellow.Constants.EMAIL;
import static com.saharmassachi.labs.newfellow.Constants.TWITTER;
import static com.saharmassachi.labs.newfellow.Constants.FBID;
import static com.saharmassachi.labs.newfellow.Constants.RAWLOC;
import static com.saharmassachi.labs.newfellow.Constants.LAT;
import static com.saharmassachi.labs.newfellow.Constants.LONG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


//NetHelper is STATIC
//NetHelper should only be called through DBhelper

public class Net {	
	public static final String appKey = "VM5ROYOT7FUHFXZ65D";
	public static final String TAG = "NETHELPER";
	private static final String baseURL = "http://rootscamp.herokuapp.com"; 
	
	
	
	public static ArrayList<Contact> downPublic(){
		//TODO 
		HttpGet request = new HttpGet();
		ArrayList<Contact> a = null;
		String page = "";
		try {
			request.setURI( new URI(baseURL + "/users.json"));
			page = connectionHelper(request);
			a = newParse(page);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		Log.e(TAG, "DOWNPUBLIC stub");
		return a;
		
	}
	
	
	
	public static boolean login(String key, Contact c){
		HttpPut request = new HttpPut();
		long badge = c.getID();

		try {
			
			request.setURI(new URI(baseURL + "/users/" + badge + ".json"));
			request.addHeader("Content-Type", "application/json");
			request.addHeader("Accept", "application/json");
			JSONObject o = new JSONObject();
			JSONObject k = new JSONObject();
			
			String fname = c.getfirst();
			String lname = c.getlast();
			String e = c.getEmail();
			String t = c.getTwitter();
			String p = c.getPhone();
			String fb = c.getFbid();
			String loc = c.getBase();
			
			
			k.put("api_key", key);
			if(check(fname)){
				k.put(FNAME, fname);
			}
			if(check(lname)){
				k.put(LNAME, lname);
			}
			if(check(e)){
				k.put(EMAIL, e);
			}
			if(check(t)){
				k.put(TWITTER, t);
			}
			if(check(p)){
				k.put(PHONE, p);
			}
			if(check(fb)){
				k.put(FBID, fb);
			}
			if(check(loc)){
				k.put(RAWLOC, loc);
			}
			try{
				k.put(LAT, c.getLat());
				k.put(LONG, c.getLong());
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			o.put("user", k);
			
			
			StringEntity se = new StringEntity(o.toString());
			request.setEntity(se);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(request);
			
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	public static boolean register(String badgeid, String key){
		HttpPut request = new HttpPut();

		String page = "";

		try {
			request.setURI(new URI(baseURL + "/users/" + badgeid + ".json"));
			request.addHeader("Content-Type", "application/json");
			request.addHeader("Accept", "application/json");

			JSONObject o = new JSONObject();
			JSONObject k = new JSONObject();
			k.put("api_key", key);
			o.put("user", k);

			StringEntity se = new StringEntity(o.toString());
			request.setEntity(se);
			HttpClient client = new DefaultHttpClient();
			try {
				HttpResponse response = client.execute(request);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return checkLogin(badgeid, key);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public static void uploadNewContacts(){
		//TODO 
		Log.e(TAG, "upload new contacts stub");
		//remember to get the new badge numbers for those who don't have badges
	}
	
	public static void downPrivate(){
		//TODO 
		Log.e(TAG, "DOWNPRIVATE stub");
	}
	
	
	private static String connectionHelper(HttpRequestBase request){
		String toreturn = "";
		BufferedReader in = null;
		HttpClient client = new DefaultHttpClient();
		try{
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			toreturn = sb.toString();
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}}}
		return toreturn;
	}
	
	private static ArrayList<Contact> newParse(String in){
		ArrayList<Contact> a = new ArrayList<Contact>();
		try {
			JSONArray jarray = new JSONArray(in); 
			for (int i = 0; i < jarray.length(); i++) {
				// so each o is an attendee
				JSONObject o = jarray.getJSONObject(i);
				Contact c = jsonToContact(o);
				a.add(c);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;
	}

	private static Contact jsonToContact(JSONObject o){
		String fname = null, lname = null;
		long bid = -1; //they're all -1 because yale forgot to include badge_id
		try {
			fname = o.getString(FNAME);
			lname = o.getString(LNAME);	
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		Contact c = new Contact(-1, fname, lname);
		
		try {
			String t = o.getString(TWITTER);
			String p = o.getString(PHONE);
			String e = o.getString(EMAIL);
			String f = o.getString(FBID);
			String loc = o.getString(RAWLOC);
			
			if(check(t)){
				c.setTwitter(t);
			}
			if(check(p)){
				c.setPhone(p);
			}
			if(check(e)){
				c.setEmail(e);
			}
			if(check(f)){
				c.setFbid(f);
			}
			if(check(loc)){
				c.setBase(loc);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			//IF there is no lat/long, this will return an execption, but
			//this exception will be handled by the catch right underneath so no harm no foul.
			//don't put anything after c.setLong and before the catch.
			c.setLat(o.getInt(LAT));
			c.setLong(o.getInt(LONG));
		}
		catch(Exception e){
			Log.d(TAG, "Lat/Long are null");
		}
		
		return c;
		
		
	
	}

	private static boolean checkLogin(String uid, String key) {
		Log.e(TAG, "DOWNPUBLIC stub");
		HttpGet request = new HttpGet();
		String page = "";
		try {
			request.setURI(new URI(baseURL + "/users/" + uid + ".json"));
			page = connectionHelper(request);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		JSONObject p;
		try {
			p = new JSONObject(page);
			String apk = p.getString("api_key");
			String bid = p.getString("badge_id");

			if (apk.equalsIgnoreCase(key)) 
				//	&& bid.equalsIgnoreCase(uid)) //TODO fix this once the server supports it. 
			{
				return true;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
	
	// given string s - is it not null and length > 0?
	private static boolean check(String s){
		if((s != null) && (s.length() > 1) && (s.trim() != "null")){
			return true;
		}
		return false;
	}
}
