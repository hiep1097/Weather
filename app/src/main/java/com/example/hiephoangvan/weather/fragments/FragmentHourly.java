package com.example.hiephoangvan.weather.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.hiephoangvan.weather.adapters.HourlyAdapter;
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

public class FragmentHourly extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.tv_currently_temp) TextView mCurrentTemp;
    @BindView(R.id.image_weather) ImageView mCurrentImageWeather;
    @BindView(R.id.tv_description) TextView mCurrentDescription;
    @BindView(R.id.tv_currently_time) TextView mCurrentTime;
    @BindView(R.id.recyclerViewHourly) RecyclerView mRecyclerView;
    @BindView(R.id.refreshlayoutHourly) SwipeRefreshLayout mRefreshLayout;
    HourlyAdapter mHourlyAdapter;
    java.util.List<List> list = new ArrayList<>();
    private DateFormat dateFormat1;
    public static FragmentHourly instance;

    public FragmentHourly() {
        instance = this;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hourly,container,false);
        ButterKnife.bind(this,view);
        dateFormat1 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        mHourlyAdapter = new HourlyAdapter(list);
        mRecyclerView.setAdapter(mHourlyAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setNestedScrollingEnabled(false);
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

    public void getHourlyWeather(){
        Service service = RetrofitInstance.getRetrofitInstance().create(Service.class);
        Observable<HourlyWeather> observable = service.getHourlyWeather(
                UtilPref.getFloat(getContext(),"lat",0),
                UtilPref.getFloat(getContext(),"lon",0),UtilPref.getString(getContext(),"unit","metric"),"vi", Config.API_KEY);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->updateList(response),
                        error->Toast.makeText(this.getContext(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show());

    }

    public void updateView(CurrentlyWeather currentlyWeather){
        mCurrentTemp.setText(Math.round(currentlyWeather.getMain().getTemp())+"");
        mCurrentImageWeather.setImageDrawable(getDrawable(getContext(),"ic_"+currentlyWeather.getWeather().get(0).getIcon()));
        StringBuilder sb = new StringBuilder(currentlyWeather.getWeather().get(0).getDescription());
        sb.setCharAt(0,Character.toUpperCase(sb.charAt(0)));
        mCurrentDescription.setText(sb.toString());
        mCurrentTime.setText("Cập nhật gần nhất: "+dateFormat1.format(new Date(currentlyWeather.getDt()*1000L)));
        }

    public Drawable getDrawable(Context context, String name){
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        Drawable drawable = resources.getDrawable(resourceId);
        return drawable;
    }

    public void updateList(HourlyWeather hourlyWeather) {
        this.list.clear();
        this.list.addAll(hourlyWeather.getList());
        this.mHourlyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(false);
        getCurrentWeather();
        getHourlyWeather();
    }
}
