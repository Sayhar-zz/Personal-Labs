package com.saharmassachi.labs.newfellow;

import java.util.Arrays;

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
import static com.saharmassachi.labs.newfellow.Constants.NAME_TABLE;
import static com.saharmassachi.labs.newfellow.Constants.NID;
import static com.saharmassachi.labs.newfellow.Constants.FIRST;
import static com.saharmassachi.labs.newfellow.Constants.LAST;
import static com.saharmassachi.labs.newfellow.Constants.PRIMARYLOC;

public class DBhelper {
	private FellowDB fdb;

	public DBhelper(Context ctx) {
		fdb = new FellowDB(ctx);
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

	protected long addContact(String first, String last, long locationID) {
		SQLiteDatabase db = fdb.getWritableDatabase();
		long toreturn = -1;

		ContentValues values = new ContentValues();
		values.put(FIRST, first);
		values.put(LAST, last);
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

	protected String quickanddirtyGetRecordGivenID(long id, String table) {
		SQLiteDatabase db = fdb.getReadableDatabase();
		String ID = NID;
		if (table == NAME_TABLE) {
			ID = NID;
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
	
	private long findOwnerOfLocation(long pos) {
		// given the row of a location in the location table, find the user in
		// the name table that corresponds to it.
		long toreturn = -1;
		SQLiteDatabase db = fdb.getReadableDatabase();
		try {

			String[] columns = { NID };
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

		double lat = a.getLatitude();
		double lng = a.getLongitude();
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
		values.put(STREETNAME, street);
		values.put(CITY, city);

		if (rawzip.contains("-")) {
			String[] zips = rawzip.split("-");
			String zip = zips[0];
			String zipsuffix = zips[1];
			values.put(ZIP, zip);
			values.put(ZIPSUFFIX, zipsuffix);
		} else {
			if (rawzip != null)
				values.put(ZIP, rawzip);
		}

		return values;
	}
}