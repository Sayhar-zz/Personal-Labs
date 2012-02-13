package com.saharmassachi.labs.newfellow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

import static com.saharmassachi.labs.newfellow.Constants.PHONE;
import static com.saharmassachi.labs.newfellow.Constants.EMAIL;
import static com.saharmassachi.labs.newfellow.Constants.TWITTER;
import static com.saharmassachi.labs.newfellow.Constants.FBID;
import static com.saharmassachi.labs.newfellow.Constants.PRIMARYLOC;

import static com.saharmassachi.labs.newfellow.Constants.AID;
import static com.saharmassachi.labs.newfellow.Constants.FNAME;
import static com.saharmassachi.labs.newfellow.Constants.LNAME;
import static com.saharmassachi.labs.newfellow.Constants.WORK;
import static com.saharmassachi.labs.newfellow.Constants.PRELOAD_TABLE;

import static com.saharmassachi.labs.newfellow.Constants.PREFSNAME;
import static com.saharmassachi.labs.newfellow.Constants.SINCE;

public class DBhelper {
	private FellowDB fdb;
	private ReadWriteLock globalLock;
	private Lock readLock;
	private Lock writeLock;
	private Context ctx;

	public DBhelper(Context ctx) {
		fdb = new FellowDB(ctx);
		this.ctx = ctx;
		globalLock = new ReentrantReadWriteLock();
		readLock = globalLock.readLock();
		writeLock = globalLock.writeLock();
	}

	protected long addAddress(Address a) {
		long toreturn;
		ContentValues values = addressToContentValues(a);
		writeLock.lock();
		try {
			SQLiteDatabase db = fdb.getWritableDatabase();
			try {
				toreturn = db.insertOrThrow(LOCATION_TABLE, null, values);

			} catch (Exception e) {
				toreturn = -1;
			} finally {
				db.close();
			}
		} finally {
			writeLock.unlock();
		}
		return toreturn;

	}

	protected long addContact(String fname, String lname, long locationID) {
		long toreturn = -1;
		ContentValues values = new ContentValues();
		values.put(FNAME, fname);
		values.put(LNAME, lname);
		values.put(PRIMARYLOC, locationID);

		writeLock.lock();
		try {
			SQLiteDatabase db = fdb.getWritableDatabase();
			try {
				toreturn = db.insertOrThrow(NAME_TABLE, null, values);
			} catch (Exception e) {
				e.printStackTrace();
				toreturn = -2;
			} finally {
				db.close();
			}
		} finally {
			writeLock.unlock();
		}
		return toreturn;
	}

	protected long addContact(String fname, String lname, String phone, String email,
			String tweet, long locationID) {
		writeLock.lock();
		long toreturn = -1;
		ContentValues values = new ContentValues();
		values.put(FNAME, fname);
		values.put(FNAME, lname);
		values.put(PRIMARYLOC, locationID);
		values.put(EMAIL, email);
		values.put(TWITTER, tweet);
		values.put(PHONE, phone);
		try {
			SQLiteDatabase db = fdb.getWritableDatabase();

			try {
				toreturn = db.insertOrThrow(NAME_TABLE, null, values);
			} catch (Exception e) {
				e.printStackTrace();
				toreturn = -2;
			} finally {
				db.close();
			}
		} finally {
			writeLock.unlock();
		}
		return toreturn;

	}
	
	private ContentValues addressToContentValues(Address a) {

		int lat = (int) (a.getLatitude() * 1E6);
		int lng = (int) (a.getLongitude() * 1E6);
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
		if (street != null) {
			values.put(STREETNAME, street);
		}
		values.put(CITY, city);

		if (rawzip != null) {
			// if there is a zipcode:
			// if it's a longer zipcode, split it into zip and suffix
			if (rawzip.contains("-")) {
				String[] zips = rawzip.split("-");
				String zip = zips[0];
				String zipsuffix = zips[1];
				values.put(ZIP, zip);
				values.put(ZIPSUFFIX, zipsuffix);
			}
			// otherwise just store the zip code
			else {
				values.put(ZIP, rawzip);
			}
		}

		int j = a.getMaxAddressLineIndex();
		String toString = "";
		for (int i = 0; i <= j; i++) {
			toString += a.getAddressLine(i);
			toString += "\n";
		}
		values.put(RAWLOC, toString);

		return values;
	}

	private SimpleContact createSimpleContact(Cursor c) {
		int lat = c.getInt(0);
		int lng = c.getInt(1);
		String fname = c.getString(2);
		String lname = c.getString(3);
		String rawloc = c.getString(4);
		long nid = c.getLong(5);
		long lid = c.getLong(6);

		SimpleContact sc = new SimpleContact(lat, lng, fname, lname, rawloc, nid, lid);
		return sc;
	}

	protected void downAllAttendees() {
		SharedPreferences settings = ctx.getSharedPreferences(PREFSNAME, 0);
		ArrayList<String[]> a;
		
		long since = settings.getLong(SINCE, 0);
		long unixTime = System.currentTimeMillis() / 1000L;
		a = NetHelper.downAllAttendees(since);
		
		//a = NetHelper.downAllAttendees();

		SQLiteDatabase db;
		ContentValues values;
		for (String[] s : a) {

			values = new ContentValues();
			values.put(FNAME, s[0]);
			values.put(LNAME, s[1]);

			if ((s[2] != null) && (s[2].trim() != "".trim())) {
				values.put(CITY, s[2]);
			}

			if ((s[3] != null) && (s[3].trim() != "")) {
				values.put(WORK, s[3]);
			}

			if (!existsPreload(values)) { // avoiding dupes
				writeLock.lock();
				try {
					db = fdb.getWritableDatabase(); // write to the table
					try {
						db.insertOrThrow(PRELOAD_TABLE, null, values);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						db.close();
					}
				} finally {
					writeLock.unlock();
				}
			}

		}
		
		Editor e = settings.edit();
		e.putLong(SINCE, unixTime);
		e.commit();
	}

	private boolean existsPreload(ContentValues values) {
		int count;
		String[] columns = { AID };
		String WHERE = FNAME + "= ? AND " + LNAME + " = ?";
		String[] args = new String[4];
		args[0] = values.getAsString(FNAME);
		args[1] = values.getAsString(LNAME);
		boolean iscity = false;
		boolean iswork = false;
		if(values.containsKey(CITY)){
			WHERE += " AND " + CITY + " = ? ";
			iscity = true;
			args[2] = values.getAsString(CITY);
		}
		if(values.containsKey(WORK)){
			WHERE += " AND " + WORK + "= ?";
			iswork = true;
			if(args[2]== null) { args[2] = values.getAsString(WORK);}
			else{args[3] = values.getAsString(WORK);}
		}
		if(args[2] == null){
			String[] tmpargs = new String[2];
			tmpargs[0] = args[0];
			tmpargs[1] = args[1];
			args = tmpargs;
		}
		else if (args[3] == null){
			String[] tmpargs = new String[3];
			tmpargs[0] = args[0];
			tmpargs[1] = args[1];
			tmpargs[2] = args[2];
			args = tmpargs;
		}
		
		
		
		readLock.lock();
		try {
			SQLiteDatabase db = fdb.getReadableDatabase();	
			Cursor c = db.query(PRELOAD_TABLE, columns, WHERE, args, null,
					null, null);
			c.moveToLast();
			count = c.getCount();
			c.close();
			db.close();
		}
		finally {
			readLock.unlock();
		}
		if (count > 0) {
			return true;
		}
		return false;

	}

	private long findOwnerOfLocation(long pos) {
		// given the row of a location in the location table, find the user in
		// the name table that corresponds to it.
		long toreturn = -1;
		String[] columns = { CID };
		readLock.lock();
		try {
			SQLiteDatabase db = fdb.getReadableDatabase();
			try {
	
				
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
		} finally {
			readLock.unlock();
		}
		return toreturn;
	}

	protected Contact[] getAllAttendees() {
		Contact[] toReturn;
		String[] columns = { AID, FNAME, LNAME };
		readLock.lock();
		try {
			SQLiteDatabase db = fdb.getReadableDatabase();
			Cursor c = db.query(PRELOAD_TABLE, columns, null, null, null, null,
					null);
			toReturn = new Contact[c.getCount()];
			int i = 0;
			while (c.moveToNext()) {
				long id = c.getLong(0);
				String fname = c.getString(1);
				String lname = c.getString(2);
				toReturn[i] = new Contact(id, fname, lname);
				i++;
			}
			c.close();
			db.close();
		} finally {
			readLock.unlock();
		}
		return toReturn;
	}

	protected Contact[] getAllFullContacts(){
		Contact[] toReturn;
		String[] columns = { CID, FNAME, LNAME };
		readLock.lock();
		try {
			SQLiteDatabase db = fdb.getReadableDatabase();
			Cursor c = db.query(NAME_TABLE, columns, null, null, null, null,
					null);
			toReturn = new Contact[c.getCount()];
			int i = 0;
			while (c.moveToNext()) {
				long id = c.getLong(0);
				String fname = c.getString(1);
				String lname = c.getString(2);
				toReturn[i] = new Contact(id, fname, lname);
				i++;
			}
			c.close();
			db.close();
		} finally {
			readLock.unlock();
		}
		return toReturn;
		
	}
	
	protected ArrayList<SimpleContact> getAllSimpleContacts() {
		readLock.lock();
		ArrayList<SimpleContact> a = new ArrayList<SimpleContact>();
		String sql = "SELECT DISTINCT " + LOCATION_TABLE + "." + LAT + " , "
		+ LOCATION_TABLE + "." + LONG + " , " + NAME_TABLE + "." + FNAME
		+ " , " + NAME_TABLE + "." + LNAME + " , " + LOCATION_TABLE + "." + RAWLOC + " , " + NAME_TABLE
		+ "." + CID + " , " + LOCATION_TABLE + "." + LID + " FROM "
		+ NAME_TABLE + " " + " JOIN " + LOCATION_TABLE + " ON "
		+ NAME_TABLE + "." + PRIMARYLOC + " = " + LOCATION_TABLE + "."
		+ LID + ";";
		try {
			SQLiteDatabase db = fdb.getReadableDatabase();

			// TODO:
			Cursor cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				SimpleContact sc = createSimpleContact(cursor);
				a.add(sc);
			}
			cursor.close();
			db.close();
		} finally {
			readLock.unlock();
		}
		return a;
	}

	protected Contact getContactInfo(long cid) {
		Contact toReturn = null;
		String[] columns = { CID, FNAME, LNAME, PHONE, EMAIL, TWITTER, FBID };
		readLock.lock();
		try {
			SQLiteDatabase db = fdb.getReadableDatabase();
			try {

				Cursor c = db.query(NAME_TABLE, columns, CID + " = " + cid,
						null, null, null, null);
				if (c.getCount() != 0) {
					c.moveToFirst();
					toReturn = new Contact(c.getLong(0), c.getString(1), c.getString(2));
					String p = c.getString(3);
					String e = c.getString(4);
					String t = c.getString(5);
					String f = c.getString(6);
					if(p != null){ toReturn.setPhone(p);}
					if(e != null){ toReturn.setEmail(e);}
					if(t != null){ toReturn.setTwitter(t);}
					if(f != null){ toReturn.setFbid(f);}
				}
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.close();
			}
		} finally {
			readLock.unlock();
		}
		return toReturn;
	}

	protected String getEmail(long cid) {
		String toreturn = null;
		String[] columns = { EMAIL };
		readLock.lock();
		try {
			SQLiteDatabase db = fdb.getReadableDatabase();
			try {

				Cursor c = db.query(NAME_TABLE, columns, CID + " = " + cid,
						null, null, null, null);
				if (c.getCount() != 0) {
					c.moveToFirst();
					toreturn = c.getString(0);
				}
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
				toreturn = null;
			} finally {
				db.close();
			}
		} finally {
			readLock.unlock();
		}
		return toreturn;

	}

	protected String[] getOneAttendee(String fname, String lname) {
		String[] toReturn = new String[5];

		readLock.lock();
		try {

			SQLiteDatabase db = fdb.getReadableDatabase();
			// String[] columns = {NAME};
			Cursor c = db.query(PRELOAD_TABLE, null, FNAME + "='" + fname + "'"
					+ " AND " + LNAME + " = '" + lname + "'", null, null, null,
					null);
			// Cursor c = db.query(PRELOAD_TABLE, columns, NAME +
			// "='aaron Ireland'"
			// , null, null, null, null);
			// String sql = "SELECT * FROM attendees WHERE name= ;
			// Cursor c = db.rawQuery(sql, null);
			boolean b = c.moveToFirst();
			int j = c.getCount();
			for (int i = 0; i < 4; i++) {
				toReturn[i] = c.getString(i);
			}
			c.close();
			db.close();
		} finally {
			readLock.unlock();
		}
		return toReturn;
	}

	protected String quickanddirtyGetRecordGivenID(long id, String table) {
		readLock.lock();
		String s = "error";
		String ID = CID;
		try {
			SQLiteDatabase db = fdb.getReadableDatabase();

			if (table == NAME_TABLE) {
				ID = CID;
			} else if (table == LOCATION_TABLE) {
				ID = LID;
			}
			try {
				Cursor c = db.query(table, null, ID + " = " + id, null, null,
						null, null);
				c.moveToFirst();

				int l = c.getColumnCount();
				s = "";
				for (int i = 0; i < l; i++) {
					s += c.getString(i);
					s += "|\t ";
				}
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				db.close();
			}
		} finally {
			readLock.unlock();
		}
		return s;
	}

	protected ArrayList<SimpleContact> search(String searchstring) {

		ArrayList<SimpleContact> a = new ArrayList<SimpleContact>();
		String sql = "SELECT DISTINCT " + LOCATION_TABLE + "." + LAT + " , "
		+ LOCATION_TABLE + "." + LONG + " , " + NAME_TABLE + "." + FNAME + " , " + NAME_TABLE + "." + LNAME
		+ " , " + LOCATION_TABLE + "." + RAWLOC + " , " + NAME_TABLE
		+ "." + CID + " , " + LOCATION_TABLE + "." + LID + " FROM "
		+ NAME_TABLE + " " + " JOIN " + LOCATION_TABLE + " ON "
		+ NAME_TABLE + "." + PRIMARYLOC + " = " + LOCATION_TABLE + "."
		+ LID + " WHERE ( " + FNAME + " LIKE " + "'%" + searchstring
		+ "%' ) " + "OR ( " + RAWLOC + " LIKE " + "'%" + searchstring
		+ "%' ) " + " OR ( " + FNAME + " LIKE " + "'%" + searchstring
				+ "%' )  ;";
		readLock.lock();
		try {
			SQLiteDatabase db = fdb.getReadableDatabase();

			Cursor cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				SimpleContact sc = createSimpleContact(cursor);
				a.add(sc);
			}
			cursor.close();
			db.close();
		} finally {
			readLock.unlock();
		}
		return a;
	}

	protected boolean uploadMyInfo(Contact c){
		try{
			NetHelper.uploadMyInfo(c);
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
}
