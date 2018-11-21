package com.example.hiephoangvan.weather.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.hiephoangvan.weather.models.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dbplace";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "place";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    public PlaceDatabase(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_place_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s FLOAT, %s FLOAT)",
                TABLE_NAME, KEY_ID, KEY_NAME, KEY_ADDRESS, KEY_LAT, KEY_LON);
        db.execSQL(create_place_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_place_table = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(drop_place_table);
        onCreate(db);
    }

    public void addPlace(Place place) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, place.getId());
        values.put(KEY_NAME, place.getName());
        values.put(KEY_ADDRESS, place.getAddress());
        values.put(KEY_LAT, place.getLat());
        values.put(KEY_LON, place.getLon());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Place> getAllPlaces() {
        List<Place>  studentList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false) {
            Place place = new Place(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getFloat(3), cursor.getFloat(4));
            studentList.add(place);
            cursor.moveToNext();
        }
        return studentList;
    }

    public void deletePlace(int placeID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?", new String[] { String.valueOf(placeID) });
        db.close();
    }
}
