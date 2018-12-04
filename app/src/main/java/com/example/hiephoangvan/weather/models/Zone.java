package com.example.hiephoangvan.weather.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Zone {

    @SerializedName("sunrise")
    @Expose
    private String sunrise;
    @SerializedName("lng")
    @Expose
    private float lng;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("gmtOffset")
    @Expose
    private float gmtOffset;
    @SerializedName("rawOffset")
    @Expose
    private float rawOffset;
    @SerializedName("sunset")
    @Expose
    private String sunset;
    @SerializedName("timezoneId")
    @Expose
    private String timezoneId;
    @SerializedName("dstOffset")
    @Expose
    private float dstOffset;
    @SerializedName("countryName")
    @Expose
    private String countryName;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("lat")
    @Expose
    private float lat;

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public float getGmtOffset() {
        return gmtOffset;
    }

    public void setGmtOffset(float gmtOffset) {
        this.gmtOffset = gmtOffset;
    }

    public float getRawOffset() {
        return rawOffset;
    }

    public void setRawOffset(float rawOffset) {
        this.rawOffset = rawOffset;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getTimezoneId() {
        return timezoneId;
    }

    public void setTimezoneId(String timezoneId) {
        this.timezoneId = timezoneId;
    }

    public float getDstOffset() {
        return dstOffset;
    }

    public void setDstOffset(float dstOffset) {
        this.dstOffset = dstOffset;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

}
