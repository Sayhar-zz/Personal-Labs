package com.saharmassachi.labs.newfellow;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e(TAG, "DOWNPUBLIC stub");
		return a;
		
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
			//in = (String) in.subSequence(1, in.length()-2);
			//char l = in.charAt(in.length()-1);
			//JSONObject wrapper = new JSONObject(in);
			JSONArray jarray = new JSONArray(in); 
				//.getJSONArray(in);
			for (int i = 0; i < jarray.length(); i++) {
				// so each o is an attendee
				JSONObject o = jarray.getJSONObject(i);
				Contact c = jsonToContact(o);
				a.add(c);
				//String[] s = {fname, lname, city, work };
				//a.add(s);
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

	
	
	// given string s - is it not null and length > 0?
	private static boolean check(String s){
		if((s != null) && (s.length() > 1) && (s.trim() != "null")){
			return true;
		}
		return false;
	}
}
