package com.example.hiephoangvan.weather.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class UtilDate {
    private DateFormat dateFormat;
    private TimeZone timeZone;
    private static UtilDate instance;
    private UtilDate(){
    }
    public static UtilDate getInstance(){
        if (instance==null){
            instance = new UtilDate();
        }
        return instance;
    }

    public void updateTimezone(){
        timeZone = TimeZone.getTimeZone(UtilPref.getInstance().getString("timezone", "Asia/Ho_Chi_Minh"));
    }

    public DateFormat getDateFormat(String format){
        updateTimezone();
        dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(timeZone);
        return dateFormat;
    }
}
