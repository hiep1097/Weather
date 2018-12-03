package com.example.hiephoangvan.weather.databases;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.example.hiephoangvan.weather.application.App;

@Database(entities = {Places.class},version = 1)
public abstract class PlaceDatabase extends RoomDatabase {
    private static PlaceDatabase instance;
    public abstract PlaceDAO placeDAO();
    public static PlaceDatabase getInstance(){
        if (instance==null){
            instance = Room.databaseBuilder(App.getContext(),PlaceDatabase.class,"place-database")
                            .build();
        }
        return instance;
    }
}
