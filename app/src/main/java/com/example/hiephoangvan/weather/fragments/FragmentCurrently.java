package com.example.hiephoangvan.weather.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentCurrently extends Fragment {
    @BindView(R.id.tv_currently_temp)
    TextView mCurrentTemp;
    @BindView(R.id.tv_currently_time)
    TextView mCurrentTime;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currently,container,false);
        ButterKnife.bind(this,view);
        getCurrentWeather();
        return view;
    }
    public void getCurrentWeather(){
        Service service = RetrofitInstance.getRetrofitInstance().create(Service.class);
        Observable<CurrentlyWeather> observable = service.getCurrentWeather(Config.ID_HANOI,"metric",Config.API_KEY);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->updateView(response),
                        error->Toast.makeText(this.getContext(),error.getLocalizedMessage(),Toast.LENGTH_SHORT).show());
    }
    public void updateView(CurrentlyWeather currentlyWeather){
        mCurrentTemp.setText(currentlyWeather.getMain().getTemp().toString());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        mCurrentTime.setText(dateFormat.format(new Date(currentlyWeather.getDt()*1000L)));
        Log.d("DT",System.currentTimeMillis()+"");
    }
}
