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

public class NetHelper {

	public static final String appKey = "VM5ROYOT7FUHFXZ65D";

	public void downAllAttendees() {
//todo- something with all the attendee data
		//also remember to change count to 5000 or something
		//
		try {
			String page = "";
			HttpGet request = new HttpGet();
			String rootscampid = "2025659803";
			String baseURL = "http://www.eventbrite.com/json/event_list_attendees?app_key="
					+ appKey + "&id=" + rootscampid + "&count=2";

			request.setURI(new URI(baseURL));
			page = connectionHelper(request);
			ArrayList<String> a = parse(page);
		} catch (Exception e) {
			e.printStackTrace();
		}

		

	}

	private ArrayList<String> parse(String in) {
		// TODO
		ArrayList<String> a = new ArrayList<String>();
		try {
			JSONObject wrapper = new JSONObject(in);
			JSONArray jarray = wrapper.getJSONArray("attendees");
			for (int i = 0; i < jarray.length(); i++) {
				// so each o is an attendee
				JSONObject o = jarray.getJSONObject(i);
				o = o.getJSONObject("attendee");
				a.add(o.getString("first_name") + " " + o.getString("last_name"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;
	}

	private String connectionHelper(HttpRequestBase request) {
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

}
