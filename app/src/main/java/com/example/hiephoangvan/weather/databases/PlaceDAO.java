package com.example.hiephoangvan.weather.databases;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface PlaceDAO {
    @Query("SELECT * FROM place")
    Flowable<List<Places>> getAllPlaces();
    @Insert
    void addPlace(Places places);
    @Delete
    void deletePlace(Places places);
    @Update
    void updatePlace(Places places);
}
