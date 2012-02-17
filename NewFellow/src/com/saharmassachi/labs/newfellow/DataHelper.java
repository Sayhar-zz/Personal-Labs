package com.saharmassachi.labs.newfellow;

import static com.saharmassachi.labs.newfellow.Constants.PREFSNAME;
import static com.saharmassachi.labs.newfellow.Constants.MYKEY;
import static com.saharmassachi.labs.newfellow.Constants.MYID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static com.saharmassachi.labs.newfellow.Constants.BID;
import static com.saharmassachi.labs.newfellow.Constants.PRIVATE_TABLE;
import static com.saharmassachi.labs.newfellow.Constants.CID;
import static com.saharmassachi.labs.newfellow.Constants.FNAME;
import static com.saharmassachi.labs.newfellow.Constants.LNAME;
import static com.saharmassachi.labs.newfellow.Constants.PHONE;
import static com.saharmassachi.labs.newfellow.Constants.EMAIL;
import static com.saharmassachi.labs.newfellow.Constants.PUBLIC_TABLE;
import static com.saharmassachi.labs.newfellow.Constants.TWITTER;
import static com.saharmassachi.labs.newfellow.Constants.FBID;
import static com.saharmassachi.labs.newfellow.Constants.LAT;
import static com.saharmassachi.labs.newfellow.Constants.LONG;
import static com.saharmassachi.labs.newfellow.Constants.RAWLOC;
import static com.saharmassachi.labs.newfellow.Constants.UPLOADED;
import static com.saharmassachi.labs.newfellow.Constants.NOTES;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.util.Log;

public class DataHelper {
	private static final String TAG = "DATAHELPER";
	private FellowDB fdb;
	private Context ctx;

	public DataHelper(Context c) {
		ctx = c;
		fdb = FellowDB.getInstance(c);
	}

	// The public methods to be implemented are:
	// downPublic = download all public attendees
	// downPrivate = download all contacts rows that are mine
	// login = authenticate with UUID and badge, then send my public info to the server
	// putPrivate = save a new contact, and send it to the server
	// getBasicPublic = get the names, ids, and lat/long of preloaded attendees (as contacts)
	// getContact = get all contact information (merge of their public info & my private info)
	// getBasicContacts = get the id, name, and lat/long of all my contacts (merge of public info and my private)
	// editPrivate = edit a contact row (update to the new info attached)
	// )

	public synchronized void downPublic() {
		//clear the current public database;
		try{
			SQLiteDatabase db = fdb.getWritableDatabase();
			fdb.clearTable(db, PUBLIC_TABLE);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		ArrayList<Contact> publics = Net.downPublic();

		
			for (Contact c : publics) {
				SQLiteDatabase db = fdb.getWritableDatabase(); // write to the table
				ContentValues values = contactToValues(c);
				
					try {
						values.remove(BID);
						values.remove(UPLOADED);
						db.insertOrThrow(PUBLIC_TABLE, null, values);
					} catch (Exception e) {
						e.printStackTrace();
					}	finally{
						db.close();
					}
				
			}
		

	}

	
	public synchronized void downPrivate() {
		Net.downPrivate();
	}
	
	public synchronized boolean register(long badge, UUID id){
		return Net.register(String.valueOf(badge), id.toString());
	}

	public synchronized boolean login(Contact c) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFSNAME, 0);
		String uid = settings.getString(MYKEY, "null");
		if (uid.equalsIgnoreCase("null")) {
			return false;
			// login didn't work
		}
		return Net.login(uid, c);
	}

	
	public synchronized void deleteContact(long cid){
		
		SQLiteDatabase db = fdb.getWritableDatabase();
		try{
			db.delete(PRIVATE_TABLE, CID  + " = " + cid, null);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			db.close();
		}
	}
	
	// putPrivate = save a new contact, and send it to the server
	public synchronized long putPrivate(Contact c) {
		long toreturn;
		
		ContentValues values = new ContentValues();
		
		values = contactToValues(c);
		
		SQLiteDatabase db = fdb.getWritableDatabase();
		try {
			toreturn = db.insertOrThrow(PRIVATE_TABLE, null, values);

		} catch (Exception exception) {
			exception.printStackTrace();
			toreturn = -1;
		} finally {
			db.close();
		}

		
		//todo
		SharedPreferences settings = ctx.getSharedPreferences(PREFSNAME, 0);
		final String myid = settings.getString(MYID, "-1");
		
		Runnable r = new Runnable() {
			@Override
			public void run() {
				ArrayList<Contact> up = getContactsToUpload();
				try{
					for(Contact con : up){
					if(Net.uploadNewContact(con, myid)){
						markUploaded(con.getCid(), con);
						}
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}};
		new Thread(r).start();
		
		

		return toreturn;
	}

	
	public synchronized Contact[] getAllBasicPublic() {
		Contact[] toReturn = new Contact[0];
		String[] columns = { BID, FNAME, LNAME, LAT, LONG };
		SQLiteDatabase db = fdb.getReadableDatabase();
		try {

			Cursor c = db.query(PUBLIC_TABLE, columns, null, null, null, null, null);
			//Can we check this?
			toReturn = new Contact[c.getCount()];
			int i = 0;
			while (c.moveToNext()) {
				long id = c.getLong(0);
				String fname = c.getString(1);
				String lname = c.getString(2);
				int lat = c.getInt(3);
				int lng = c.getInt(4);
				toReturn[i] = new Contact(id, fname, lname);
				toReturn[i].setLat(lat);
				toReturn[i].setLong(lng);
				i++;
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return toReturn;
	}
	

	public synchronized Contact getOnePublic(long badgeid){
		Contact toreturn = null; 
		SQLiteDatabase db = fdb.getReadableDatabase();
		String[] selection = {String.valueOf(badgeid)};
		try{
			Cursor c = db.query(PUBLIC_TABLE, null, BID + " = " + badgeid, null, null, null, null);
			c.moveToFirst();
			toreturn = publicCursorToContact(c);
			c.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			db.close();
		}
		
		return toreturn;
	}
	
	// getContact = get contact information (merge of their public info & my private info)
	public synchronized Contact getContact(long contactid){
		Contact priv = getOnePrivate(contactid);
		if(priv.getID() > 0) {
			Contact pub = getOnePublic(priv.getID()); 
			return merge(priv, pub);
		}
		else{
			return priv;
		}
	
		
	}
	
	
	//This uses java where SQL would be faster. If there's time, redo this with a sql join instead
	//for those searching for these keywords: dirty hack todo fixit later
	// getContacts = get all my contacts (merge of public info and my private
	public synchronized Contact[] getBasicContacts(){
		Contact[] publics = this.getAllBasicPublic();
		HashMap<String, Contact> privates = getBasicPrivateMap();
		ArrayList<Contact> list = new ArrayList<Contact>();
		
		//for each public contact, see if there's a corresponding private contact. if there is, merge the two and add
		for (Contact c : publics){
			String id = String.valueOf(c.getID());
			if(privates.containsKey(id)){
				list.add(merge(privates.get(id), c));
				privates.remove(id);
			}
		}
		
		//and now for contacts in privates that aren't in public
		//for (Contact c : privates.values()) {
		//	list.add(c);
		//}
		
		//nullpointer exception?
		for (Contact c: getNoBadges()){
			list.add(c);
		}
		Contact[] toreturn = new Contact[list.size()];
		list.toArray(toreturn);
		return toreturn;
	}
	
	
	
	
	//editPrivate = edit a contact row (update to the new info attached)
	public synchronized void editPrivate(Contact c, long cid){
		
		ContentValues values = contactToValues(c);
		values.put(CID, cid);
		SQLiteDatabase db = fdb.getWritableDatabase();
		try{
			db.replaceOrThrow(PRIVATE_TABLE, null, values);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			db.close();
		}
		
		
		
	}
	
	
	
	protected ArrayList<Contact> search(String searchstring) {

		ArrayList<Contact> a = new ArrayList<Contact>();
		String sql = 
		"SELECT DISTINCT " 
		+ PRIVATE_TABLE + "." + LAT + " , "
		+ PRIVATE_TABLE + "." + LONG + " , " 
		+ PRIVATE_TABLE + "." + FNAME + " , " 
		+ PRIVATE_TABLE + "." + LNAME+ " , " 
		+ PRIVATE_TABLE + "." + RAWLOC + " , " 
		+ PUBLIC_TABLE + "." + RAWLOC + " , "
		+ PRIVATE_TABLE + "." + BID + " , " 
		+ PRIVATE_TABLE + "." + CID + " "
		+ " FROM "
		+ PRIVATE_TABLE + " " + " LEFT OUTER JOIN " + PUBLIC_TABLE //OUTER LEFT JOIN! 
		+ " ON "
		+ PRIVATE_TABLE + "."  + BID + " = " + PUBLIC_TABLE + "."+ BID + 
		" WHERE ( " + PRIVATE_TABLE + "." 
		+ FNAME + " LIKE " + "'%" + searchstring
		+ "%' ) " + "OR ( " + PRIVATE_TABLE + "." + RAWLOC + " LIKE " + "'%" + searchstring
		+ "%' ) " + "OR ( " + PUBLIC_TABLE + "." + RAWLOC + " LIKE " + "'%" + searchstring
		+ "%' ) " 
		+ " OR ( " + PRIVATE_TABLE + "."  + LNAME + " LIKE " + "'%" + searchstring
				+ "%' )  ;";
		
		
			SQLiteDatabase db = fdb.getReadableDatabase();

			Cursor cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				long bid  = cursor.getLong(6);
				String fname = cursor.getString(2);
				String lname = cursor.getString(3);
				Contact con = new Contact(bid, fname, lname);
				con.setLat(cursor.getInt(0));
				con.setLong(cursor.getInt(1));
				con.setCid(cursor.getLong(7));
				a.add(con);;
			}
			cursor.close();
			db.close();
		
			return a;
			
	}
	
	private synchronized void markUploaded(long cid, Contact contact){
		ContentValues values = contactToValues(contact);
		values.remove(UPLOADED);
		values.put(UPLOADED, 1);
		values.put(CID, cid);
		
		Contact toreturn = null;
		SQLiteDatabase db = fdb.getWritableDatabase();
		try {
			long r = db.replace(PRIVATE_TABLE, null, values);
		}
		finally{
			db.close();
		}
		
	}
	
	private synchronized ArrayList<Contact> getContactsToUpload(){
		ArrayList<Contact> toreturn = new ArrayList<Contact>();
		
		SQLiteDatabase db = fdb.getReadableDatabase();
		try {

			Cursor c = db.query(PRIVATE_TABLE, null, UPLOADED+"<0", null, null, null, null);
			//Can we check this?
			while(c.moveToNext()){
				toreturn.add(privateCursorToContact(c));
			}
			
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return toreturn;
		

	}
	
	

	//return all entries in the privates table
	//but just their name, lat, long, and badge id
	//also we won't return things without badge ids (because there's a separate function to return just those)
	private synchronized HashMap<String, Contact> getBasicPrivateMap(){
		HashMap<String, Contact> map = new HashMap<String, Contact>();
		String[] columns = { CID, BID, FNAME, LNAME, LAT, LONG };
		SQLiteDatabase db = fdb.getReadableDatabase();
		try{
			Cursor c = db.query(PRIVATE_TABLE, columns, BID + ">" + -1, null, null,
					null, null);

			while (c.moveToNext()) {
				long cid = c.getLong(0);
				long bid = c.getLong(1);
				String fname = c.getString(2);
				String lname = c.getString(3);
				int lat = c.getInt(4);
				int lng = c.getInt(5);
				Contact con = new Contact(bid, fname, lname);
				con.setLat(lat);
				con.setLong(lng);
				con.setCid(cid);
				map.put(String.valueOf(bid), con);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

		return map;
	}

	
	//given 2 contacts, merge them. In case of conflict, first one has precedence.
	//we usually merge with private given precedence
	private synchronized Contact merge(Contact A, Contact b){
		String f = A.getfirst();
		String l = A.getlast();
		long badge = A.getID();
		Contact m = new Contact(badge, f, l);
		
		if(A.getCid() >-1){
			m.setCid(A.getCid());
		}
		else if(b.getCid() > -1) {
			m.setCid(b.getCid());
		}
		//if A has a twitter/phone/etc, then use As. Otherwise, use b's, if it has one
		if(check(A.getTwitter())){
			m.setTwitter(A.getTwitter());
		}
		else if (check(b.getTwitter())){
			m.setTwitter(b.getTwitter());
		}
		
		if(check(A.getEmail())){
			m.setEmail(A.getEmail());
		}
		else if (check(b.getEmail())){
			m.setEmail(b.getEmail());
		}
		
		if(check(A.getPhone())){
			m.setPhone(A.getPhone());
		}
		else if (check(b.getPhone())){
			m.setPhone(b.getPhone());
		}
		
		if(check(A.getFbid())){
			m.setFbid(A.getFbid());
		}
		else if(check(b.getFbid())){
			m.setFbid(b.getFbid());
		}
		if(check(A.getNotes())){
			m.setNotes(A.getNotes());
		}
		if(check(A.getBase())){
			m.setBase(A.getBase());
		}
		try{
			m.setLat(A.getLat());
			m.setLong(A.getLong());
		}
		catch(Exception e){
		}
		return m;
	}
	
	private synchronized Contact getOnePrivate(long contactid){
		//given a contactID, return a contact corresponding to that id;
		SQLiteDatabase db = fdb.getReadableDatabase();
		//String[] selection = {String.valueOf(contactid)};
		Contact toreturn = null;
		try {

			Cursor c = db.query(PRIVATE_TABLE, null, CID + "= " + contactid, null, null,
					null, null);
			c.moveToFirst();
			toreturn = privateCursorToContact(c);
			c.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			db.close();
		}
		return toreturn;
	}
	
	
	
	private Contact privateCursorToContact(Cursor curse) throws Exception{
		return cursorToContact(curse, PRIVATE_TABLE);
	}
	
	private synchronized Contact cursorToContact(Cursor curse, String S) throws Exception{
		boolean isprivate = false;
		if(S.equalsIgnoreCase(PRIVATE_TABLE)){  isprivate = true;}
		else if(S.equalsIgnoreCase(PUBLIC_TABLE)){ isprivate = false;}
		else{ throw new Exception("THIS SHOULD NEVER HAPPEN: CURSOR IS SEARCHING NEITHER THE PUBLIC NOR PRIVATE TABLE");}
		
		
		Contact contact;
		int i = 0;
		long cid = -1;
		if(isprivate){
			//if private, then we need to increment the counter by 1 (and set CID)
			cid = curse.getLong(i++);
		}
		
		//so either BID, fname, lname are columns [0, 1, 2] or [1, 2, 3] depending on whether we're using the private table or public table.
		//we are taking advantage of how similar these tables are
		long bID = curse.getLong(i); //these are guaranteed.
		i++;
		String fname = curse.getString(i++);
		String lname = curse.getString(i++);
		contact = new Contact(bID, fname, lname);
		if(isprivate){
			contact.setCid(cid);
		}
		//THE ORDER for private table: (public table drops CID, and UPLOADED)
		/*	0 CID 
			1 BID
			2 FNAME
			3 LNAME 
			4 PHONE 
			5 EMAIL 
			6 TWITTER 
			7 FBID +
			8 LAT + 
			9 LONG +
			10 RAWLOC 
			11 UPLOADED */
		
		String p = curse.getString(i++); //3 or 4 (public or private)
		String e = curse.getString(i++);
		String t = curse.getString(i++);
		String f = curse.getString(i++);
		int lat  = curse.getInt(i++); //7 or 8
		int lng  = curse.getInt(i++);
		String base = curse.getString(i++);
		
		if(isprivate){
			String notes = curse.getString(12);
			if(check(notes)){
				contact.setNotes(notes);
			}
		}
		
		
		if(check(p)){ contact.setPhone(p);}
		if(check(t)){ contact.setTwitter(t);}
		if(check(e)){ contact.setEmail(e);}
		if(check(f)){ contact.setFbid(f);}
		if(check(base)) {contact.setBase(base); }
		if(lat != 0) {contact.setLat(lat);}
		if(lng != 0) { contact.setLong(lng);}
		return contact;
	}
	
	private Contact publicCursorToContact(Cursor curse) throws Exception{
		return cursorToContact(curse, PUBLIC_TABLE);
	}
	
	private synchronized Contact[] getNoBadges(){
		SQLiteDatabase db = fdb.getReadableDatabase();
		//String[] selection = {String.valueOf(contactid)};
		Contact[] toreturn = null;
		try {

			Cursor c = db.query(PRIVATE_TABLE, null, BID + "<" + 0, null, null,
					null, null);
			toreturn = new Contact[c.getCount()];
			int i = 0;
			while (c.moveToNext()) {
				toreturn[i] = privateCursorToContact(c);
				i++;
			}
			c.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			db.close();
		}
		return toreturn;
	}
	
	private synchronized ContentValues contactToValues(Contact c){
		//this sets badge ID if it exists
		//This also sets UPLOADED to -1;
		//it does not set cid
		//it does set notes, if they exist
		//fix both by removing those keys if these assumptions are not true.
		ContentValues values = new ContentValues();
		String b = c.getBase();
		String e = c.getEmail();
		String f = c.getfirst();
		String l = c.getlast();
		int lat = c.getLat();
		int lng = c.getLong();
		String p = c.getPhone();
		String t = c.getTwitter();
		String n = c.getNotes();
		long badge = c.getID();
		
		if (check(b)) {
			values.put(RAWLOC, b);
		}
		if (check(e)) {
			values.put(EMAIL, e);
		}
		if (check(f)) {
			values.put(FNAME, f);
		}
		if (check(l)) {
			values.put(LNAME, l);
		}
		if (check(p)) {
			values.put(PHONE, p);
		}
		if (check(t)) {
			values.put(TWITTER, t);
		}
		if(check(n)){
			values.put(NOTES, n);
		}
		if(badge > -1){
			values.put(BID, badge);
			Log.e(TAG, "No badge id. This is probably an error");
		}
		
		try{
			values.put(LAT, lat);
			values.put(LONG, lng);
		}
		catch(Exception ex){}
		values.put(UPLOADED, -1); // no booleans in SQLite. This means that this
		// contact hasn't been uploaded.
		return values;
	}
	
	


	













	//TEMPORARY DEBUG
	protected void downAllAttendees() {
		SharedPreferences settings = ctx.getSharedPreferences(PREFSNAME, 0);
		ArrayList<Contact> a;

		//long since = settings.getLong(SINCE, 0);
		//long unixTime = System.currentTimeMillis() / 1000L;
		a = Net.downPublic();
		//a = NetHelper.downAllAttendees();

		/*SQLiteDatabase db;
		ContentValues values;
		for (String[] s : a) {

			values = new ContentValues();
			values.put(FNAME, s[0]);
			values.put(LNAME, s[1]);

			if ((s[2] != null) && (s[2].trim() != "".trim())) {
				values.put(RAWLOC, s[2]);
			}

			db = fdb.getWritableDatabase(); // write to the table
			try {
				db.insertOrThrow(PUBLIC_TABLE, null, values);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.close();
			}
		}*/
	}

	
	//given an address, return a reasonable toString
	public String showBase(Address a){
		int j = a.getMaxAddressLineIndex();
		int i = 0;
		String out ="";
		while(i<=j){
			out += a.getAddressLine(i);
			out += "\n";
			i++;
		}
		return out;
	}
	
	
	// given string s - is it not null and length > 0?
	private boolean check(String s){
		if((s != null) && (s.length() > 0)){
			return true;
		}
		return false;
	}
	
}