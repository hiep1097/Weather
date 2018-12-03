package com.example.hiephoangvan.weather.api;

import com.example.hiephoangvan.weather.models.CurrentlyWeather;
import com.example.hiephoangvan.weather.models.HourlyWeather;
import com.example.hiephoangvan.weather.models.Zone;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface Service {
    @GET("weather")
    Observable<CurrentlyWeather> getCurrentWeather(@QueryMap Map<String,String> options);
    @GET("forecast")
    Observable<HourlyWeather> getHourlyWeather(@QueryMap Map<String,String> options);
    @GET("timezoneJSON")
    Observable<Zone> getTimeZone(@QueryMap Map<String,String> options);
}
