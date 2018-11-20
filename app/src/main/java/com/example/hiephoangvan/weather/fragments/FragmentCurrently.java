package com.example.hiephoangvan.weather.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.example.hiephoangvan.weather.api.RetrofitInstance;
import com.example.hiephoangvan.weather.api.Service;
import com.example.hiephoangvan.weather.models.CurrentlyWeather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currently,container,false);
        ButterKnife.bind(this,view);
        dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        dateFormat2 = new SimpleDateFormat("kk:mm");
        getCurrentWeather();
        mRefreshLayout.setOnRefreshListener(this::onRefresh);
        return view;
    }
    public void getCurrentWeather(){
        Service service = RetrofitInstance.getRetrofitInstance().create(Service.class);
        Observable<CurrentlyWeather> observable = service.getCurrentWeather(Config.ID_HANOI,"metric","vi", Config.API_KEY);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->updateView(response),
                        error->Toast.makeText(this.getContext(),error.getLocalizedMessage(),Toast.LENGTH_SHORT).show());
    }
    public void updateView(CurrentlyWeather currentlyWeather){
        mCurrentTemp.setText(Math.round(currentlyWeather.getMain().getTemp())+"");
        mCurrentImageWeather.setImageDrawable(getDrawable(getContext(),"ic_"+currentlyWeather.getWeather().get(0).getIcon()));
        mCurrentDescription.setText(currentlyWeather.getWeather().get(0).getDescription());
        mCurrentTempMin.setText(Math.round(currentlyWeather.getMain().getTempMin())+"");
        mCurrentTempMax.setText(Math.round(currentlyWeather.getMain().getTempMax())+"");
        mCurrentSunsetSunrise.setText(" Bình minh/Hoàng hôn  "+dateFormat2.format(new Date(currentlyWeather.getSys().getSunrise()*1000L))+"/"
                             +dateFormat2.format(new Date(currentlyWeather.getSys().getSunset()*1000L)));
        mCurrentTime.setText("Cập nhật gần nhất: "+dateFormat1.format(new Date(currentlyWeather.getDt()*1000L)));
        mCurrentHumidity.setText(currentlyWeather.getMain().getHumidity()+" %");
        mCurrentCloud.setText(currentlyWeather.getClouds().getAll()+" %");
        mCurrentVisibility.setText(currentlyWeather.getVisibility()/1000+" km");
        mCurrentPressure.setText(currentlyWeather.getMain().getPressure()+" hPa");
        try {
            mCurrentRain.setText(Math.round(currentlyWeather.getRain().get3h())+" mm");
        } catch (NullPointerException e){
            mCurrentRain.setText("0 mm");
        }

        mCurrentWindSpeed.setText(currentlyWeather.getWind().getSpeed()+" km/h");
        mCurrentWindDeg.setText("Deg: "+currentlyWeather.getWind().getDeg()+"");
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(false);
        getCurrentWeather();
    }

    public Drawable getDrawable(Context context, String name){
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        Drawable drawable = resources.getDrawable(resourceId);
        return drawable;
    }

}
