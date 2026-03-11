package com.example.myapplication;

public class WeatherHour {

    private String time;
    private String temp;
    private int weatherCode; // Resource ID untuk icon cuaca

    public WeatherHour(String time, String temp, int weatherCode) {
        this.time = time;
        this.temp = temp;
        this.weatherCode = weatherCode;
    }

    public String getTime() {
        return time;
    }

    public String getTemp() {
        return temp;
    }

    public int getWeatherCode() {
        return weatherCode;
    }


}
