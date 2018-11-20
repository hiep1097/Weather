package com.example.hiephoangvan.weather.api;

import com.example.hiephoangvan.weather.models.CurrentlyWeather;
import com.example.hiephoangvan.weather.models.HourlyWeather;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {
    @GET("weather")
    Observable<CurrentlyWeather> getCurrentWeather(@Query("id") int id, @Query("units") String units, @Query("lang") String lang, @Query("APPID") String APPID);
    @GET("forecast")
    Observable<HourlyWeather> getHourlyWeather(@Query("id") int id, @Query("units") String units, @Query("lang") String lang, @Query("APPID") String APPID);
}
