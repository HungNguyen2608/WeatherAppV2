package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRL;
    private RecyclerView weatherRV;
    private ProgressBar loadingPB;
    private TextView cityNameTV,temperatureTV,conditionTV;
    private TextInputEditText cityEdt;
    private ImageView backIV,iconIV,searchIV;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private  WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);
        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this,weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                        this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            cityName = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(cityName);
        } else {
            Toast.makeText(this, "Location not available. Using default city.", Toast.LENGTH_SHORT).show();
            getWeatherInfo("Ho Chi Minh");
        }

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter city name",Toast.LENGTH_SHORT).show();
                }else {
                    cityNameTV.setText(cityName);
                    String transCity = removeAccents(city);
                    getWeatherInfo(transCity);
                }
            }
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this,"Permissions granted..",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this,"Please provide the permissions",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    private String getCityName(double longitude, double latitude){
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);
            for (Address adr : addresses){
                if (adr!= null){
                    String city = adr.getLocality();
                    if(city != null && !city.equals("")){
                        cityName = removeAccents(city);
                    } else {
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this,"User City Not Found, Set Default City..",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }
    private String removeAccents(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("\\p{M}", "");
        return str;
    }
    private void getWeatherInfo(String cityName) {
        cityNameTV.setText(cityName);
        getLanLot(cityName, new LocationCallback() {
            @Override
            public void onLocationReceived(String lon, String lat) {
                String url = "https://api.openweathermap.org/data/3.0/onecall?lat=" + lat + "&lon=" + lon + "&appid=bd2856391afb93c08024222279f9c410";
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loadingPB.setVisibility(View.GONE);
                            homeRL.setVisibility(View.VISIBLE);
                            weatherRVModalArrayList.clear();
                            if (response.has("current")) {
                                String Ktemperature = response.getJSONObject("current").getString("temp");
                                float KtoC = Float.parseFloat(Ktemperature) - 273.15f;
                                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                                String Ctemperature = decimalFormat.format(KtoC);
                                temperatureTV.setText(Ctemperature + "°C");

                                String isDay = response.getJSONObject("current")
                                        .getJSONArray("weather").getJSONObject(0)
                                        .getString("icon");
                                String condition = response.getJSONObject("current")
                                        .getJSONArray("weather").getJSONObject(0)
                                        .getString("description");
                                conditionTV.setText(condition);
                                Glide.with(MainActivity.this)
                                        .load("https://openweathermap.org/img/w/" + isDay + ".png")
                                        .into(iconIV);
                                if (isDay.contains("d")) {
                                    Glide.with(MainActivity.this)
                                            .load("https://img.lovepik.com/background/20211030/medium/lovepik-city-sky-mobile-wallpaper-background-image_400458919.jpg")
                                            .into(backIV);
                                } else {
                                    Glide.with(MainActivity.this)
                                            .load("https://i.pinimg.com/originals/0b/7a/c2/0b7ac22e4e856979255dd38a558d56bd.jpg")
                                            .into(backIV);
                                }
                                String timeZ = response.getString("timezone_offset");
                                if (response.has("hourly")) {
                                    JSONArray hourArray = response.getJSONArray("hourly");
                                    for (int i = 0; i < hourArray.length(); i++) {
                                        JSONObject hourObj = hourArray.getJSONObject(i);
                                        Integer time = hourObj.getInt("dt");
                                        Double tempK = hourObj.getDouble("temp");
                                        String tempC =decimalFormat.format(tempK - 273.15f);
                                        String img = hourObj.getJSONArray("weather").getJSONObject(0).getString("icon");
                                        Double wind = hourObj.getDouble("wind_speed");
                                        weatherRVModalArrayList.add(new WeatherRVModal(time, tempC, img, wind,timeZ,isDay));
                                    }
                                    weatherRVAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to retrieve weather data.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Please enter valid city name..", Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(jsonObjectRequest);
            }
        });
    }

    private void getLanLot(String cityName, LocationCallback callback) {
        String url = "https://api.openweathermap.org/geo/1.0/direct?q=" + cityName + "&limit=5&appid=bd2856391afb93c08024222279f9c410";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        JSONObject locationObject = response.getJSONObject(0);
                        String lon = locationObject.getString("lon");
                        String lat = locationObject.getString("lat");
                        callback.onLocationReceived(lon, lat);
                    } else {
                        Toast.makeText(MainActivity.this, "Location not found.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name..", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    public interface LocationCallback {
    void onLocationReceived(String lon, String lat);
    }
}