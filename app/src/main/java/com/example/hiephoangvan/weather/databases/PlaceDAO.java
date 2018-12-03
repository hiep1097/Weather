package com.example.hiephoangvan.weather.databases;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PlaceDAO {
    @Query("SELECT * FROM place")
    List<Places> getAllPlaces();
    @Insert
    void addPlace(Places... places);
    @Delete
    void deletePlace(Places places);
}
