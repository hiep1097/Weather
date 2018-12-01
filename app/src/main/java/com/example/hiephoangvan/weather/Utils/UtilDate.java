package com.example.hiephoangvan.weather.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class UtilDate {
    private static DateFormat dateFormat1;
    private static DateFormat dateFormat2;
    private static DateFormat dateFormat3;
    private static TimeZone timeZone;

    private static UtilDate instance;
    private UtilDate(){

    }
    public static UtilDate getInstance(){
        if (instance==null){
            instance = new UtilDate();
            timeZone = TimeZone.getTimeZone(UtilPref.getInstance().getString("timezone", "Asia/Ho_Chi_Minh"));
            dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            dateFormat2 = new SimpleDateFormat("kk:mm");
            dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat1.setTimeZone(timeZone);
            dateFormat2.setTimeZone(timeZone);
            dateFormat3.setTimeZone(timeZone);
        }
        return instance;
    }

    public void updateTimezone(){
        timeZone = TimeZone.getTimeZone(UtilPref.getInstance().getString("timezone", "Asia/Ho_Chi_Minh"));
    }

    public DateFormat getDateFormat1(){
        updateTimezone();
        dateFormat1.setTimeZone(timeZone);
        return dateFormat1;
    }
    public DateFormat getDateFormat2(){
        updateTimezone();
        dateFormat2.setTimeZone(timeZone);
        return dateFormat2;
    }
    public DateFormat getDateFormat3(){
        updateTimezone();
        dateFormat3.setTimeZone(timeZone);
        return dateFormat3;
    }
}
