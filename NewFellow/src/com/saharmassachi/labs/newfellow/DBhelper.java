package com.saharmassachi.labs.newfellow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Location;
import static com.saharmassachi.labs.newfellow.Constants.LOCATION_TABLE;
import static com.saharmassachi.labs.newfellow.Constants.CITY;
import static com.saharmassachi.labs.newfellow.Constants.LAT;
import static com.saharmassachi.labs.newfellow.Constants.LID;
import static com.saharmassachi.labs.newfellow.Constants.LONG;
import static com.saharmassachi.labs.newfellow.Constants.NUMBER;
import static com.saharmassachi.labs.newfellow.Constants.STATE;
import static com.saharmassachi.labs.newfellow.Constants.STREETNAME;
import static com.saharmassachi.labs.newfellow.Constants.ZIP;
import static com.saharmassachi.labs.newfellow.Constants.ZIPSUFFIX;
import static com.saharmassachi.labs.newfellow.Constants.RAWLOC;

import static com.saharmassachi.labs.newfellow.Constants.NAME_TABLE;
import static com.saharmassachi.labs.newfellow.Constants.CID;
import static com.saharmassachi.labs.newfellow.Constants.NAME;
import static com.saharmassachi.labs.newfellow.Constants.PHONE;
import static com.saharmassachi.labs.newfellow.Constants.EMAIL;
import static com.saharmassachi.labs.newfellow.Constants.TWITTER;
import static com.saharmassachi.labs.newfellow.Constants.FBID;
import static com.saharmassachi.labs.newfellow.Constants.PRIMARYLOC;

public class DBhelper {
	private FellowDB fdb;

	public DBhelper(Context ctx) {
		fdb = new FellowDB(ctx);
	}

	protected String[] getContactInfo(long cid){
		String[] toreturn = new String[5];
		SQLiteDatabase db = fdb.getReadableDatabase();
		try{
			String[] columns = { NAME, PHONE, EMAIL, TWITTER, FBID };
			Cursor c = db.query(NAME_TABLE, columns, CID + " = " + cid,
				null, null, null, null);
			if(c.getCount() != 0){
				c.moveToFirst();
				for(int i = 0; i < 5; i++){
					String s = c.getString(i);
					if((s != null) 
							&& (s.trim() != null)) {
						toreturn[i] = s;}
				}
			}
			c.close();
		}catch (Exception e) {
			e.printStackTrace();
			toreturn = null;
		} finally {
			db.close();
		}
		return toreturn;
	}
	
	protected String getEmail(long cid){
		String toreturn = null;
		SQLiteDatabase db = fdb.getReadableDatabase();
		try{
			String[] columns = { EMAIL };
			Cursor c = db.query(NAME_TABLE, columns, CID + " = " + cid,
				null, null, null, null);
			if(c.getCount() != 0){
				c.moveToFirst();
				toreturn = c.getString(0);
			}
			c.close();
		}catch (Exception e) {
			e.printStackTrace();
			toreturn = null;
		} finally {
			db.close();
		}
		return toreturn;
		
	}
	
	protected long addAddress(Address a) {
		long toreturn;

		ContentValues values = addressToContentValues(a);

		SQLiteDatabase db = fdb.getWritableDatabase();
		try {
			toreturn = db.insertOrThrow(LOCATION_TABLE, null, values);

		} catch (Exception e) {
			toreturn = -1;
		} finally {
			db.close();
		}
		return toreturn;

	}

	protected long addContact(String name, long locationID) {
		SQLiteDatabase db = fdb.getWritableDatabase();
		long toreturn = -1;

		ContentValues values = new ContentValues();
		values.put(NAME, name);
		
		values.put(PRIMARYLOC, locationID);
		try {
			toreturn = db.insertOrThrow(NAME_TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			toreturn = -2;
		} finally {
			db.close();
		}
		return toreturn;
	}

	protected long addContact(String name, String phone, String email, String tweet, long locationID){
		SQLiteDatabase db = fdb.getWritableDatabase();
		long toreturn = -1;

		ContentValues values = new ContentValues();
		values.put(NAME, name);
		values.put(PRIMARYLOC, locationID);
		values.put(EMAIL, email);
		values.put(TWITTER, tweet);
		values.put(PHONE, phone);
		try {
			toreturn = db.insertOrThrow(NAME_TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			toreturn = -2;
		} finally {
			db.close();
		}
		return toreturn;
		
	}
	protected String quickanddirtyGetRecordGivenID(long id, String table) {
		SQLiteDatabase db = fdb.getReadableDatabase();
		String ID = CID;
		if (table == NAME_TABLE) {
			ID = CID;
		} else if (table == LOCATION_TABLE) {
			ID = LID;
		}
		
		String s = "error";
		
		try{
			Cursor c = db.query(table, null, ID + " = " + id, null, null, null,
				null);
			c.moveToFirst();
			
			int l = c.getColumnCount();
			s = "";
			for (int i = 0; i < l; i++) {
				s += c.getString(i);
				s += "|\t ";
			}
			c.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
		
			db.close();
		}
		return s;
	}

	protected ArrayList<SimpleContact> search(String searchstring){
		SQLiteDatabase db = fdb.getReadableDatabase();
		ArrayList<SimpleContact> a = new ArrayList<SimpleContact>();
		String sql = "SELECT DISTINCT " + 
				LOCATION_TABLE + "." +  LAT + 
		" , " + LOCATION_TABLE + "." +  LONG +
		" , " + NAME_TABLE     + "." +  NAME +
		" , " + LOCATION_TABLE + "." +  RAWLOC +
		" , " + NAME_TABLE     + "." +  CID + 
		" , " + LOCATION_TABLE + "." +  LID +
		" FROM " + NAME_TABLE  + " " +
		" JOIN " + LOCATION_TABLE + 
		" ON " + NAME_TABLE + "." + PRIMARYLOC +
		" = " + LOCATION_TABLE + "." + LID + 
		" WHERE ( " + NAME + " LIKE " + "'%" + searchstring + "%' ) " +
		"OR ( " + RAWLOC + " LIKE " + "'%" + searchstring + "%' ) ;"
		;

		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext() ){
			SimpleContact sc = createSimpleContact(cursor);
			a.add(sc);
		}
		cursor.close();
		db.close();
		return a;
	}
	
	protected ArrayList<SimpleContact> getAllSimpleContacts(){
		
		SQLiteDatabase db = fdb.getReadableDatabase();
		ArrayList<SimpleContact> a = new ArrayList<SimpleContact>();
		String sql = "SELECT DISTINCT " + 
					LOCATION_TABLE + "." +  LAT + 
			" , " + LOCATION_TABLE + "." +  LONG +
			" , " + NAME_TABLE     + "." +  NAME +
			" , " + LOCATION_TABLE + "." +  RAWLOC +
			" , " + NAME_TABLE     + "." +  CID + 
			" , " + LOCATION_TABLE + "." +  LID +
			" FROM " + NAME_TABLE  + " " +
			" JOIN " + LOCATION_TABLE + 
			" ON " + NAME_TABLE + "." + PRIMARYLOC +
			" = " + LOCATION_TABLE + "." + LID + ";" 
			;
		
		//TODO: 
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext() ){
			SimpleContact sc = createSimpleContact(cursor);
			a.add(sc);
		}
		cursor.close();
		db.close();
		return a;
	}
	
/*

	protected String[] findClosestAddress(double lat, double lng, String state) {
		// given a lat and long, find the closest address to that
		//returns a tuple.
		// toreturn[0] corresponds to the string representation of "where"
		// toreturn[1] corresponds to the string representation of "who"
		String[] toreturn = new String[2];
		
		SQLiteDatabase db = fdb.getReadableDatabase();
		Cursor c;
		if(state.equals("none")){
			c = db.query(LOCATION_TABLE, null, null,
					null, null, null, null);
		}
		else{
			c = db.query(LOCATION_TABLE, null, STATE + " = '" + state + "'",
				null, null, null, null);
		}
		int l = c.getCount();
		if (l < 1) {
			c.close();
			c = db.query(LOCATION_TABLE, null, null, null, null, null, null);
			l = c.getCount();
			if (l < 1) {
				toreturn[0] = "empty database";
				toreturn[1] = "nothing to see here";
				return toreturn;
			}
		}
		float[] distances = new float[l];
		int i = 0;
		// distances will store the distances between this point and each given
		// point in the cursor
		// i marks the index of the cursor position that corresponds to the cell
		// in distances
		float[] results;
		while (c.moveToNext()) {
			results = new float[3];
			double lat2 = c.getDouble(1);
			double lng2 = c.getDouble(2);
			Location.distanceBetween(lat, lng, lat2, lng2, results);
			distances[i] = results[0];
			i++;
		}

		i = 0;
		float smallestdist = distances[0];
		int smallestdistpos = 0;
		for (i = 0; i < distances.length; i++) {
			float f = distances[i];
			if (f < smallestdist) {
				smallestdist = f;
				smallestdistpos = i;
			}
		}

		c.moveToPosition(smallestdistpos);
		long pos = c.getLong(0);

		c.close();
		db.close();

		
		toreturn[0] = quickanddirtyGetRecordGivenID(pos, LOCATION_TABLE);
		toreturn[1] = quickanddirtyGetRecordGivenID(findOwnerOfLocation(pos), NAME_TABLE);
		return toreturn;
	}

	protected String[] findClosestAddress(double lat, double lng) {
	
		return findClosestAddress(lat, lng, "none");
	}
	*/
	private SimpleContact createSimpleContact(Cursor c){
		int lat = c.getInt(0);
		int lng = c.getInt(1);
		String name = c.getString(2);
		String rawloc = c.getString(3);
		long nid = c.getLong(4);
		long lid = c.getLong(5);
		
		SimpleContact sc = new SimpleContact(lat, lng, name, rawloc, nid, lid);
		return sc;
	}
	
	private long findOwnerOfLocation(long pos) {
		// given the row of a location in the location table, find the user in
		// the name table that corresponds to it.
		long toreturn = -1;
		SQLiteDatabase db = fdb.getReadableDatabase();
		try {

			String[] columns = { CID };
			Cursor c = db.query(NAME_TABLE, columns, PRIMARYLOC + " = " + pos,
					null, null, null, null);
			c.moveToFirst();
			toreturn = c.getLong(0);
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
			toreturn = -2;
		} finally {
			db.close();
		}
		return toreturn;
	}
	

	private ContentValues addressToContentValues(Address a) {

		int lat = (int)  (a.getLatitude() *1E6);
		int lng = (int) (a.getLongitude() *1E6);
		String state = a.getAdminArea();
		String street = a.getThoroughfare();
		String rawzip = a.getPostalCode();
		String rawstreetnum = a.getFeatureName();
		String city = a.getLocality();
		int streetnum;
		try {
			streetnum = Integer.parseInt(rawstreetnum);
		} catch (Exception e) {
			try {
				streetnum = Integer.parseInt(rawstreetnum.split(" ")[0]);
			} catch (Exception f) {
				streetnum = -1;
			}
		}

		ContentValues values = new ContentValues();
		values.put(LAT, lat);
		values.put(LONG, lng);
		values.put(STATE, state);
		if (streetnum > 0) {
			values.put(NUMBER, streetnum);
		}
		if(street != null){
			values.put(STREETNAME, street);
		}
		values.put(CITY, city);

		if(rawzip != null){
		//if there is a zipcode:
			//if it's a longer zipcode, split it into zip and suffix
			if (rawzip.contains("-")){
				String[] zips = rawzip.split("-");
				String zip = zips[0];
				String zipsuffix = zips[1];
				values.put(ZIP, zip);
				values.put(ZIPSUFFIX, zipsuffix);
			}
			//otherwise just store the zip code
			else{
				values.put(ZIP, rawzip);
			}
		}
		
		int j = a.getMaxAddressLineIndex();
		String toString = "";
		for (int i = 0; i <= j; i++){
			toString += a.getAddressLine(i); 
			toString += "\n";
		}
		values.put(RAWLOC, toString);
		

		return values;
	}
}
