package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;

    public WeatherRVAdapter(Context context,ArrayList<WeatherRVModal> weatherRVModalArrayList){
        this.context = context;
        this.weatherRVModalArrayList = weatherRVModalArrayList;
    }
    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
        WeatherRVModal modal = weatherRVModalArrayList.get(position);
        holder.temperatureTV.setText(modal.getTemperature()+"°C");
        String icon = modal.getIcon();
        Glide.with(holder.itemView.getContext())
                .load("https://openweathermap.org/img/w/"+icon+".png")
                .into(holder.conditionTV);
        Picasso.get().load("https:".concat(modal.getIcon())).into(holder.conditionTV);
        holder.windTV.setText(modal.getWindSpeed()+"m/s");
        String timeZ = modal.getTimeZone();
        int timezone_offset = Integer.parseInt(timeZ);
        int hours = timezone_offset / 3600;
        String sign = hours >= 0 ? "+" : "-";
        Date date = new Date(modal.getTime() * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"+sign+hours));
        holder.timeTV.setText(sdf.format(date));
        if (modal.getIsday().contains("d")) {
            holder.itemView.setBackgroundResource(R.drawable.background_day);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.background_night);
        }

    }

    @Override
    public int getItemCount() {
        return weatherRVModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView windTV,temperatureTV,timeTV;
        private ImageView conditionTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV =itemView.findViewById(R.id.idTVWindSpeed);
            temperatureTV =itemView.findViewById(R.id.idTVTemperature);
            timeTV =itemView.findViewById(R.id.idTVTime);
            conditionTV =itemView.findViewById(R.id.idTVCondition);
        }
    }
}
