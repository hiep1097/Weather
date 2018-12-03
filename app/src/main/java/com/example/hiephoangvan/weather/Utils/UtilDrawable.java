package com.example.hiephoangvan.weather.Utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.example.hiephoangvan.weather.application.App;

public class UtilDrawable {
    private static UtilDrawable instance;
    private Resources resources;
    private UtilDrawable(){

    }
    public static UtilDrawable getInstance(){
        if (instance==null){
            instance = new UtilDrawable();
        }
        return instance;
    }
    public Drawable getDrawable(String name){
        resources = App.getContext().getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                App.getContext().getPackageName());
        Drawable drawable = resources.getDrawable(resourceId);
        return drawable;
    }
}
