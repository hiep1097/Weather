package com.example.hiephoangvan.weather.databases;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "place")
public class Places {
    @PrimaryKey(autoGenerate = true) private int id;
    private String name;
    private String address;
    private float lat;
    private float lon;
    @ColumnInfo(name = "is_home") private int isHome;

    public Places(String name, String address, float lat, float lon) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public int getIsHome() {
        return isHome;
    }

    public void setIsHome(int isHome) {
        this.isHome = isHome;
    }
}
