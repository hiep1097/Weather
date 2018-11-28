package com.example.hiephoangvan.weather.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hiephoangvan.weather.R;
import com.example.hiephoangvan.weather.Utils.Config;
import com.example.hiephoangvan.weather.Utils.UtilPref;
import com.example.hiephoangvan.weather.activities.MainActivity;
import com.example.hiephoangvan.weather.api.RetrofitInstance;
import com.example.hiephoangvan.weather.api.Service;
import com.example.hiephoangvan.weather.models.CurrentlyWeather;
import com.example.hiephoangvan.weather.models.HourlyWeather;
import com.example.hiephoangvan.weather.models.List;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FragmentCurrently extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.tv_currently_temp) TextView mCurrentTemp;
    @BindView(R.id.image_weather) ImageView mCurrentImageWeather;
    @BindView(R.id.tv_description) TextView mCurrentDescription;
    @BindView(R.id.tv_temp_min) TextView mCurrentTempMin;
    @BindView(R.id.tv_temp_max) TextView mCurrentTempMax;
    @BindView(R.id.tv_sunset_sunrise) TextView mCurrentSunsetSunrise;
    @BindView(R.id.tv_currently_time) TextView mCurrentTime;
    @BindView(R.id.tv_value_humidity) TextView mCurrentHumidity;
    @BindView(R.id.tv_value_maychephu) TextView mCurrentCloud;
    @BindView(R.id.tv_value_tamnhin) TextView mCurrentVisibility;
    @BindView(R.id.tv_value_pressure) TextView mCurrentPressure;
    @BindView(R.id.tv_value_rain) TextView mCurrentRain;
    @BindView(R.id.tv_wind_deg) TextView mCurrentWindDeg;
    @BindView(R.id.tv_wind_speed) TextView mCurrentWindSpeed;
    @BindView(R.id.refreshlayoutCurrently) SwipeRefreshLayout mRefreshLayout;
    private DateFormat dateFormat1;
    private DateFormat dateFormat2;
    private DateFormat dateFormat3;
    java.util.List<List> list = new ArrayList<>();
    public static FragmentCurrently instance;
    private CurrentlyWeather currentlyWeather;
    public FragmentCurrently() {
        instance = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currently,container,false);
        ButterKnife.bind(this,view);
        dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        dateFormat2 = new SimpleDateFormat("kk:mm");
        dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
        getCurrentWeather();
        getHourlyWeather();
        mRefreshLayout.setOnRefreshListener(this::onRefresh);
        return view;
    }
    public void getCurrentWeather(){
        Service service = RetrofitInstance.getRetrofitInstance().create(Service.class);
        Observable<CurrentlyWeather> observable = service.getCurrentWeather(
                UtilPref.getFloat(getContext(),"lat",0),
                UtilPref.getFloat(getContext(),"lon",0),UtilPref.getString(getContext(),"unit","metric"),"vi", Config.API_KEY);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->updateView(response),
                        error->Toast.makeText(this.getContext(),error.getLocalizedMessage(),Toast.LENGTH_SHORT).show());
    }
    public void updateView(CurrentlyWeather currentlyWeather){
        this.currentlyWeather = currentlyWeather;
        mCurrentTemp.setText(Math.round(currentlyWeather.getMain().getTemp())+"");
        mCurrentImageWeather.setImageDrawable(getDrawable(getContext(),"ic_"+currentlyWeather.getWeather().get(0).getIcon()));
        StringBuilder sb = new StringBuilder(currentlyWeather.getWeather().get(0).getDescription());
        sb.setCharAt(0,Character.toUpperCase(sb.charAt(0)));
        mCurrentDescription.setText(sb.toString());
        mCurrentSunsetSunrise.setText(" Bình minh/Hoàng hôn  "+dateFormat2.format(new Date(currentlyWeather.getSys().getSunrise()*1000L))+"/"
                             +dateFormat2.format(new Date(currentlyWeather.getSys().getSunset()*1000L)));
        mCurrentTime.setText("Cập nhật gần nhất: "+dateFormat1.format(new Date(currentlyWeather.getDt()*1000L)));
        mCurrentHumidity.setText(currentlyWeather.getMain().getHumidity()+" %");
        mCurrentCloud.setText(currentlyWeather.getClouds().getAll()+" %");
        try {
            mCurrentVisibility.setText(currentlyWeather.getVisibility()/1000+" km");
        } catch (NullPointerException e){
            mCurrentVisibility.setText(0+" km");
        }

        mCurrentPressure.setText(currentlyWeather.getMain().getPressure()+" hPa");
        try {
            mCurrentRain.setText(Math.round(currentlyWeather.getRain().get3h())+" mm");
        } catch (NullPointerException e){
            mCurrentRain.setText("0 mm");
        }
        if (UtilPref.getString(getContext(),"unit","metric").compareTo("metric")==0){
            mCurrentWindSpeed.setText(currentlyWeather.getWind().getSpeed()+" m/s");
        } else {
            mCurrentWindSpeed.setText(currentlyWeather.getWind().getSpeed()+" dặm/h");
        }

        mCurrentWindDeg.setText("Deg: "+currentlyWeather.getWind().getDeg()+"");
    }
    public void getHourlyWeather(){
        Service service = RetrofitInstance.getRetrofitInstance().create(Service.class);
        Observable<HourlyWeather> observable = service.getHourlyWeather(
                UtilPref.getFloat(getContext(),"lat",0),
                UtilPref.getFloat(getContext(),"lon",0),UtilPref.getString(getContext(),"unit","metric"),"vi", Config.API_KEY);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->updateTempMaxMin(response),
                        error->Toast.makeText(this.getContext(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show());

    }

    public void updateTempMaxMin(HourlyWeather hourlyWeather) {
        this.list.clear();
        this.list.addAll(hourlyWeather.getList());
        String time = dateFormat3.format(new Date(currentlyWeather.getDt()*1000L));
        long tempMax = Math.round(currentlyWeather.getMain().getTempMax());
        long tempMin = Math.round(currentlyWeather.getMain().getTempMin());
        for (List h: list){
            if (h.getDtTxt().split(" ")[0].compareTo(time)==0){
                if (h.getMain().getTemp()>tempMax){
                    tempMax = Math.round(h.getMain().getTemp());
                }
                if (h.getMain().getTemp()<tempMin){
                    tempMin = Math.round(h.getMain().getTemp());
                }
            }
        }
        mCurrentTempMax.setText(tempMax+"");
        mCurrentTempMin.setText(tempMin+"");
    }


    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(true);
        getCurrentWeather();
        getHourlyWeather();
        mRefreshLayout.setRefreshing(false);
    }

    public Drawable getDrawable(Context context, String name){
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        Drawable drawable = resources.getDrawable(resourceId);
        return drawable;
    }

}
