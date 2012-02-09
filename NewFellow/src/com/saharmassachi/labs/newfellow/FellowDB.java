package com.saharmassachi.labs.newfellow;


import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static com.saharmassachi.labs.newfellow.Constants.LAT;
import static com.saharmassachi.labs.newfellow.Constants.LONG;
import static com.saharmassachi.labs.newfellow.Constants.CITY;
import static com.saharmassachi.labs.newfellow.Constants.LID;
import static com.saharmassachi.labs.newfellow.Constants.LOCATION_TABLE;
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



public class FellowDB extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "fellow.db";
	private static final int DATABASE_VERSION = 8; 
	private static final String TAG = "FellowDB"; 
	
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
			NAME + " TEXT, " +
			PHONE + " TEXT, " +
			EMAIL + " TEXT, " +
			TWITTER + " TEXT, " +
			FBID + " TEXT, " +
			PRIMARYLOC + " INTEGER, " +
			"FOREIGN KEY(" + PRIMARYLOC + ") REFERENCES " + LOCATION_TABLE + "(" + LID + "));";
		 
		
		
	public FellowDB(Context ctx){
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_1);
		db.execSQL(CREATE_TABLE_2);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v(TAG, "upgrading");
		db.execSQL("DROP TABLE IF EXISTS " + NAME_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
	    onCreate(db);
		
	}
}
