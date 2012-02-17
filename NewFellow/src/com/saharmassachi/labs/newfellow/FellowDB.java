package com.saharmassachi.labs.newfellow;


import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static com.saharmassachi.labs.newfellow.Constants.LAT;
import static com.saharmassachi.labs.newfellow.Constants.LONG;
import static com.saharmassachi.labs.newfellow.Constants.RAWLOC;

import static com.saharmassachi.labs.newfellow.Constants.PRIVATE_TABLE;
import static com.saharmassachi.labs.newfellow.Constants.PUBLIC_TABLE;
import static com.saharmassachi.labs.newfellow.Constants.CID;

import static com.saharmassachi.labs.newfellow.Constants.PHONE;
import static com.saharmassachi.labs.newfellow.Constants.EMAIL;
import static com.saharmassachi.labs.newfellow.Constants.TWITTER;
import static com.saharmassachi.labs.newfellow.Constants.FBID;
import static com.saharmassachi.labs.newfellow.Constants.NOTES;
//import static com.saharmassachi.labs.newfellow.Constants.PRIMARYLOC;


import static com.saharmassachi.labs.newfellow.Constants.BID;
//import static com.saharmassachi.labs.newfellow.Constants.AID;
import static com.saharmassachi.labs.newfellow.Constants.WORK;
import static com.saharmassachi.labs.newfellow.Constants.FNAME;
import static com.saharmassachi.labs.newfellow.Constants.LNAME;
import static com.saharmassachi.labs.newfellow.Constants.UPLOADED;



public class FellowDB extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "fellow.db";
	private static final int DATABASE_VERSION = 3; 
	private static final String TAG = "FellowDB";
	private static FellowDB mInstance;

	/*
	private String CREATE_TABLE_1 = "CREATE TABLE " +  LOCATION_TABLE +" (" +
		LID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		LAT + " INTEGER, " +
		LONG + " INTEGER, " +
		STATE + " TEXT, " +
		ZIP + " TEXT, " +
		ZIPSUFFIX + " TEXT, " +
		STREETNAME + " TEXT, " +
		NUMBER + " INTEGER, " +
		CITY + " TEXT, "+
		RAWLOC + " TEXT);"
		;

	private String CREATE_TABLE_2 ="CREATE TABLE " + NAME_TABLE + " ( " +
		CID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		FNAME + " TEXT, " +
		LNAME + " TEXT, " +
		PHONE + " TEXT, " +
		EMAIL + " TEXT, " +
		TWITTER + " TEXT, " +
		FBID + " TEXT, " +
		PRIMARYLOC + " INTEGER, " +
		"FOREIGN KEY(" + PRIMARYLOC + ") REFERENCES " + LOCATION_TABLE + "(" + LID + "));";
*/
	private String CREATE_PUBLIC_TABLE = "CREATE TABLE " + PUBLIC_TABLE + " ( " +
		BID + " INTEGER PRIMARY KEY, " +
		FNAME + " TEXT, " +
		LNAME + " TEXT, " +
		PHONE + " TEXT, " +
		EMAIL + " TEXT, " +
		TWITTER + " TEXT, " +
		FBID + " TEXT, " +
		LAT + " INTEGER, " +
		LONG + " INTEGER, " +
		RAWLOC + " TEXT );"; 

	private String CREATE_CONTACTS_TABLE = "CREATE TABLE " + PRIVATE_TABLE + " ( " +
		CID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		BID + " INTEGER, " +
		FNAME + " TEXT, " +			
		LNAME + " TEXT, " +
		PHONE + " TEXT, " +
		EMAIL + " TEXT, " +
		TWITTER + " TEXT, " +
		FBID + " TEXT, " +
		LAT + " INTEGER, " +
		LONG + " INTEGER, " +
		RAWLOC + " TEXT, " +
		UPLOADED + " INTEGER, " +
		NOTES + " TEXT);"; 

	/*private String CREATE_TABLE_3 = "CREATE TABLE " + PRELOAD_TABLE + " ( " +
		AID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		FNAME + " TEXT, " +
		LNAME + " TEXT, " +
		CITY + " TEXT, " +
		WORK + " TEXT);";*/

	
	
	
	private FellowDB(Context ctx){
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static synchronized FellowDB getInstance(Context context) {
	        if (mInstance == null) {
	            mInstance = new FellowDB(context.getApplicationContext());
	        }
	 
	        return mInstance;
	    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PUBLIC_TABLE);
		db.execSQL(CREATE_CONTACTS_TABLE);
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v(TAG, "upgrading");
		db.execSQL("DROP TABLE IF EXISTS " + PRIVATE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + PUBLIC_TABLE);
		onCreate(db);

	}
	
	public void clearTable(SQLiteDatabase db, String s){
		if(s.equalsIgnoreCase(PUBLIC_TABLE)){
			db.execSQL("DROP TABLE IF EXISTS " + PUBLIC_TABLE);
			db.execSQL(CREATE_PUBLIC_TABLE);
		}
	}
}
