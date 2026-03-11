package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("current.json")
    Call<WeatherResponse> getCurrentWeather(
            @Query("key") String apiKey,
            @Query("q") String location
    );

    @GET("forecast.json")
    Call<WeatherResponse> getForecast(
            @Query("key") String apiKey,
            @Query("q") String location,
            @Query("days") int days // Misalnya 1 hari
    );
}
