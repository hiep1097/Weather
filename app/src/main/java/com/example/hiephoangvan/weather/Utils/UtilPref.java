package com.example.hiephoangvan.weather.Utils;

import android.content.SharedPreferences;

import com.example.hiephoangvan.weather.application.App;

public class UtilPref {
    private final static String PREF_FILE = "WEATHER";
    private static UtilPref instance;
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    private UtilPref(){

    }
    public static UtilPref getInstance(){
        if (instance==null){
            instance = new UtilPref();
            settings = App.getContext().getSharedPreferences(PREF_FILE,
                    App.getContext().MODE_PRIVATE);
            editor = settings.edit();
        }
        return instance;
    }

    public void setString(String key, String value){
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Set a integer shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    public void setInt(String key, int value){
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Set a float
     * @param key
     * @param value
     */
    public void setFloat(String key, float value){
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key,float defValue){
        return settings.getFloat(key,defValue);
    }
    /**
     * Set a Boolean shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    public void setBoolean(String key, boolean value){
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Get a string shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    public String getString(String key, String defValue){
        return settings.getString(key, defValue);
    }

    /**
     * Get a integer shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    public int getInt(String key, int defValue){
        return settings.getInt(key, defValue);
    }

    /**
     * Get a boolean shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    public boolean getBoolean(String key, boolean defValue){
        return settings.getBoolean(key, defValue);
    }
}
