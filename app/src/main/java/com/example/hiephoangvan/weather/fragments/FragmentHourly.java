package com.example.hiephoangvan.weather.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hiephoangvan.weather.R;
import com.example.hiephoangvan.weather.Utils.Config;
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

public class FragmentHourly extends Fragment {
    @BindView(R.id.recyclerViewHourly)
    RecyclerView mRecyclerView;
    HourlyAdapter mHourlyAdapter;
    java.util.List<List> list = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hourly,container,false);
        ButterKnife.bind(this,view);
        mHourlyAdapter = new HourlyAdapter(list);
        mRecyclerView.setAdapter(mHourlyAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        getHourlyWeather();
        return view;
    }

    public void getHourlyWeather(){
        Service service = RetrofitInstance.getRetrofitInstance().create(Service.class);
        Observable<HourlyWeather> observable = service.getHourlyWeather(Config.ID_HANOI,"metric",Config.API_KEY);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->updateList(response),
                        error->Toast.makeText(this.getContext(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show());

    }

    public void updateList(HourlyWeather hourlyWeather) {
        this.list.clear();
        this.list.addAll(hourlyWeather.getList());
        this.mHourlyAdapter.notifyDataSetChanged();
    }
}
