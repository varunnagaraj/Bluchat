package com.example.varunnagaraj.bluchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Varun Nagaraj on 27-02-2017.
 */

public class DeviceDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ScanDevices.db";
    public static final String TABLE_NAME = "DevicesInRange";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DEVICENAME = "DeviceName";
    public static final String COLUMN_DEVICEADDRESS = "DeviceAddress";
    public static final String COLUMN_DEVICERSSI = "DeviceRSSI";

    public DeviceDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE" + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DEVICENAME + " TEXT, "+
                COLUMN_DEVICEADDRESS + " TEXT, "+
                COLUMN_DEVICERSSI +" INTEGER " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    //Add a new row
    public void addDevice(Devices devices){
        ContentValues values = new ContentValues();
        values.put(COLUMN_DEVICENAME, devices.get_deviceName());
        values.put(COLUMN_DEVICEADDRESS, devices.get_deviceAddress());
        values.put(COLUMN_DEVICERSSI, devices.get_deviceRSSI());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    //Delete a product from the database
    public void deleteDevice(String deviceName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM" + TABLE_NAME + "WHERE" + COLUMN_DEVICENAME + "=\"" + deviceName + "\";");
    }

    //Printing database
    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT" + "FROM" + TABLE_NAME + "WHERE 1";

        //CURSOR POINT TO A LOCATION IN RESULTS
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("DeviceName"))!=null){
                dbString += c.getString(c.getColumnIndex("DeviceName"));
                dbString += "\n";
            }
        }
        db.close();
        return dbString;
    }

}
