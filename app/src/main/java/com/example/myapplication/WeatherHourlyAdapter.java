package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeatherHourlyAdapter extends RecyclerView.Adapter<WeatherHourlyAdapter.WeatherViewHolder> {
    private List<WeatherHour> weatherHourList;

    public WeatherHourlyAdapter(List<WeatherHour> weatherHourList) {
        this.weatherHourList = weatherHourList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflasi layout ViewHolder dari XML yang sudah kamu buat
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_viewholderourly, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        // Bind data ke ViewHolder
        WeatherHour weatherHour = weatherHourList.get(position);
        holder.jamTextView.setText(weatherHour.getTime());
        holder.suhuTextView.setText(weatherHour.getTemp() + "°C");

        holder.weatherIcon.setImageResource(weatherHour.getWeatherCode());
    }


    @Override
    public int getItemCount() {
        return weatherHourList.size(); // Jumlah item yang ditampilkan
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView jamTextView, suhuTextView;
        ImageView weatherIcon;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            jamTextView = itemView.findViewById(R.id.jam);
            suhuTextView = itemView.findViewById(R.id.suhu);
            weatherIcon = itemView.findViewById(R.id.imageView10);
        }
    }
}
