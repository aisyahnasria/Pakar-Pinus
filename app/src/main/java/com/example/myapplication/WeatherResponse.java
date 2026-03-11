package com.example.myapplication;

import java.util.List;

// WeatherResponse.java
public class WeatherResponse {
    private Current current;
    private Location location;
    private Forecast forecast;

    public Current getCurrent() {
        return current;
    }

    public Location getLocation() {
        return location;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public class Forecast {
        private List<ForecastDay> forecastday;

        public List<ForecastDay> getForecastday() {
            return forecastday;
        }
    }

    public class ForecastDay {
        private List<Hour> hour;

        public List<Hour> getHour() {
            return hour;
        }
    }

    public class Hour {
        private String time;
        private float temp_c;
        private Condition condition;

        public String getTime() {
            return time;
        }

        public float getTemp_c() {
            return temp_c;
        }

        public Condition getCondition() {
            return condition;
        }
    }

    public class Condition {
        private String text;
        private String icon;
        private int code;

        public String getText() {
            return text;
        }

        public String getIcon() {
            return icon;
        }

        public int getCode() {
            return code;
        }
    }

    public class Current {
        private float temp_c;
        private Condition condition;
        private float wind_kph;
        private float humidity;
        private double uv;
        private double precip_mm;

        public float getTemp_c() {
            return temp_c;
        }

        public Condition getCondition() {
            return condition;
        }

        public float getWind_kph() {
            return wind_kph;
        }

        public float getHumidity() {
            return humidity;
        }

        public double getUv() {
            return uv;
        }

        public double getPrecip_mm() {
            return precip_mm;
        }
    }

    public class Location {
        private String name;
        private String localtime;
        private double latitude;
        private double longitude;

        public String getName() {
            return name;
        }

        public String getLocaltime() {
            return localtime;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}
