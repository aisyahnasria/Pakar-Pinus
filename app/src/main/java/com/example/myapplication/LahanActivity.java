package com.example.myapplication;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.location.Location;


public class LahanActivity extends AppCompatActivity {
    private static final String TAG = "LahanActivity";
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";
    private static final String API_KEY = "6b60ed3eb5e142ffb5672146242609"; // Ganti dengan API key Anda
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lahan);

        progressBar = findViewById(R.id.progressBar);

        // Inisialisasi UI
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.lahan_tlbr);
        setSupportActionBar(toolbar);



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Periksa izin lokasi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        getWeatherData(latitude, longitude);
                    } else {
                        Log.e(TAG, "Gagal mendapatkan lokasi");

                    }
                }
            });
        }
    }

    private void getWeatherData(double latitude, double longitude) {

        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        String location = latitude + "," + longitude;

        Call<WeatherResponse> call = service.getForecast(API_KEY, location, 1);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                progressBar.setVisibility(View.GONE);


                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Log.e(TAG, "Respon Gagal" + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable throwable) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Kesalahan: " + throwable.getMessage());
            }
        });
    }

    private void setupRecyclerView(List<WeatherHour> hourlyWeatherList) {
        RecyclerView recyclerView = findViewById(R.id.weatherView);
        WeatherHourlyAdapter adapter = new WeatherHourlyAdapter(hourlyWeatherList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }



    private void updateUI(WeatherResponse weather) {
        TextView temperatureTextview = findViewById(R.id.temperatureTextView);
        TextView dateTextview = findViewById(R.id.DateTextView);
        TextView locationTextview = findViewById(R.id.LocationtextView);
        TextView windTextview = findViewById(R.id.WindTextView);
        TextView humidityTextview = findViewById(R.id.HumidityTextview);
        TextView curahTextview = findViewById(R.id.curahHujanTextview);
        TextView uvTextview = findViewById(R.id.uvTextview);
        DecimalFormat decimalFormat = new DecimalFormat("#");

        String locationName = weather.getLocation().getName();
        String localTime = weather.getLocation().getLocaltime();
        String tempt = decimalFormat.format(weather.getCurrent().getTemp_c());
        String windSpeed = decimalFormat.format(weather.getCurrent().getWind_kph());
        String humidity = decimalFormat.format(weather.getCurrent().getHumidity());
        String precip_mm = decimalFormat.format(weather.getCurrent().getPrecip_mm());
        double uvIndex = weather.getCurrent().getUv();

        String uvCategory;
        if (uvIndex <= 2) {
            uvCategory = "Rendah";
        } else if (uvIndex <= 5) {
            uvCategory = "Sedang";
        } else if (uvIndex <= 7) {
            uvCategory = "Tinggi";
        } else if (uvIndex <= 10) {
            uvCategory = "Sangat Tinggi";
        } else {
            uvCategory = "Ekstrem";
        }

        // Tampilkan data di UI
        temperatureTextview.setText(tempt + " °C");
        locationTextview.setText(locationName);
        windTextview.setText((windSpeed) + " km/jam");
        humidityTextview.setText(humidity + " %");
        curahTextview.setText(precip_mm + " mm");
        uvTextview.setText(uvCategory);

        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = originalFormat.parse(localTime);
            SimpleDateFormat newFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
            String formattedDate = newFormat.format(date);
            dateTextview.setText(formattedDate);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
        }

        List<WeatherHour> hourlyWeatherList = new ArrayList<>();
        if (weather.getForecast() != null && !weather.getForecast().getForecastday().isEmpty()) {
            List<WeatherResponse.Hour> hours = weather.getForecast().getForecastday().get(0).getHour();
            for (WeatherResponse.Hour hour : hours) {
                String time = hour.getTime().substring(11); // Ambil waktu HH:MM
                String temp = decimalFormat.format((hour.getTemp_c()));
                int weatherCode  = getIconResourceByCode(hour.getCondition().getCode());

                hourlyWeatherList.add(new WeatherHour(time, temp, weatherCode));
            }
            setupRecyclerView(hourlyWeatherList);
        }



    }

    private int getIconResourceByCode(int weatherCode) {
        Log.d("WeatherCondition", "Condition: " + weatherCode);


        switch (weatherCode) {
            // Cuaca cerah atau sebagian berawan
            case 1000:  // Sunny
            case 1003:  // Partly cloudy
                return R.drawable.ic_partly_cloudly;

            // Cuaca berawan dan mendung
            case 1006:  // Cloudy
            case 1009:  // Overcast
                return R.drawable.ic_cloudly;

            // Kabut dan kabut beku
            case 1030:  // Mist
            case 1135:  // Fog
            case 1147:  // Freezing fog
                return R.drawable.ic_fog;

            // Hujan ringan dan gerimis
            case 1063:  // Patchy rain possible
            case 1150:  // Patchy light drizzle
            case 1153:  // Light drizzle
            case 1180:  // Patchy light rain
            case 1183:  // Light rain
            case 1240:  // Light rain shower
                return R.drawable.ic_light_rain;

            // Hujan sedang hingga lebat
            case 1186:  // Moderate rain at times
            case 1189:  // Moderate rain
            case 1192:  // Heavy rain at times
            case 1195:  // Heavy rain
            case 1243:  // Moderate or heavy rain shower
            case 1246:  // Torrential rain shower
                return R.drawable.ic_rain;

            // Salju ringan hingga sedang
            case 1066:  // Patchy snow possible
            case 1210:  // Patchy light snow
            case 1213:  // Light snow
            case 1216:  // Patchy moderate snow
            case 1219:  // Moderate snow
            case 1255:  // Light snow showers
            case 1258:  // Moderate or heavy snow showers
                return R.drawable.ic_snow;

            // Badai salju dan kondisi badai
            case 1114:  // Blowing snow
            case 1117:  // Blizzard
            case 1222:  // Patchy heavy snow
            case 1225:  // Heavy snow
            case 1279:  // Patchy light snow with thunder
            case 1282:  // Moderate or heavy snow with thunder
                return R.drawable.ic_snowstorm;

            // Hujan es dan peluru es
            case 1237:  // Ice pellets
            case 1261:  // Light showers of ice pellets
            case 1264:  // Moderate or heavy showers of ice pellets
                return R.drawable.ic_ice_pellets;

            // Hujan es atau hujan beku
            case 1069:  // Patchy sleet possible
            case 1204:  // Light sleet
            case 1207:  // Moderate or heavy sleet
            case 1249:  // Light sleet showers
            case 1252:  // Moderate or heavy sleet showers
                return R.drawable.ic_sleet;

            // Badai petir
            case 1087:  // Thundery outbreaks possible
            case 1273:  // Patchy light rain with thunder
            case 1276:  // Moderate or heavy rain with thunder
                return R.drawable.ic_thunderstorm;

            // Cuaca campuran hujan atau salju
            case 1201:  // Moderate or heavy freezing rain
            case 1198:  // Light freezing rain
            case 1171:  // Heavy freezing drizzle
                return R.drawable.ic_freezing_rain;

            // Default icon untuk cuaca yang tidak dikenali
            default:
                return R.drawable.ic_cloud;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Log.e(TAG, "Izin lokasi ditolak");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

