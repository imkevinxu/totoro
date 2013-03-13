package com.totoro.incardisplay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProfileCarDB extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "InCarDisplay";
	
	// Profile table
    private static final String TABLE_NAME = "profile";
    private static final String PROFILE_ID = "id";
    private static final String PROFILE_YEAR = "year";
    private static final String PROFILE_MAKE = "make";
    private static final String PROFILE_MODEL = "model";
    private static final String PROFILE_MPG = "mpg";


    ProfileCarDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
    				PROFILE_ID + " INTEGER PRIMARY KEY, " +
        			PROFILE_YEAR + " INTEGER, " +
                    PROFILE_MAKE + " TEXT, " +
                    PROFILE_MODEL + " TEXT, " +
                    PROFILE_MPG + " REAL);";
        
        Log.d("Creating...", TABLE_CREATE);
        db.execSQL(TABLE_CREATE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    
    Car getProfile() {
    	if (getProfileCount() < 1) {
    		return null;
    	}
    	
    	SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, new String[]
        		 {PROFILE_ID, PROFILE_YEAR, PROFILE_MAKE, PROFILE_MODEL, PROFILE_MPG});
        if (cursor != null)
        	cursor.moveToFirst();
 
        Car car = new Car(Integer.parseInt(cursor.getString(0)),
        		Integer.parseInt(cursor.getString(1)), cursor.getString(2),
        		cursor.getString(2), Double.parseDouble(cursor.getString(3)));
        db.close();
        
        return car;
    }

    public int getProfileCount() {    	
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    
    public boolean addProfile(int year, String make, String model, double mpg) { 	
    	if (getProfileCount() >= 1) {
    		return false;
    	}
     
    	SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROFILE_YEAR, year);
        values.put(PROFILE_MAKE, make);
        values.put(PROFILE_MODEL, model);
        values.put(PROFILE_MPG, mpg);

        db.insert(TABLE_NAME, null, values);
        db.close();
        
        return true;
    }
    
    public void deleteProfile() {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("ProfileCarDB", "Delete database");
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
