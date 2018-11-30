package com.example.hiephoangvan.weather.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Zone {

    @SerializedName("sunrise")
    @Expose
    private String sunrise;
    @SerializedName("lng")
    @Expose
    private Integer lng;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("gmtOffset")
    @Expose
    private Integer gmtOffset;
    @SerializedName("rawOffset")
    @Expose
    private Integer rawOffset;
    @SerializedName("sunset")
    @Expose
    private String sunset;
    @SerializedName("timezoneId")
    @Expose
    private String timezoneId;
    @SerializedName("dstOffset")
    @Expose
    private Integer dstOffset;
    @SerializedName("countryName")
    @Expose
    private String countryName;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("lat")
    @Expose
    private Integer lat;

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public Integer getLng() {
        return lng;
    }

    public void setLng(Integer lng) {
        this.lng = lng;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getGmtOffset() {
        return gmtOffset;
    }

    public void setGmtOffset(Integer gmtOffset) {
        this.gmtOffset = gmtOffset;
    }

    public Integer getRawOffset() {
        return rawOffset;
    }

    public void setRawOffset(Integer rawOffset) {
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

    public Integer getDstOffset() {
        return dstOffset;
    }

    public void setDstOffset(Integer dstOffset) {
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

    public Integer getLat() {
        return lat;
    }

    public void setLat(Integer lat) {
        this.lat = lat;
    }

}
