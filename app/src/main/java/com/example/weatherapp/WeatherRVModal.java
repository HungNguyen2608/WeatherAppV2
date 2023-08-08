package com.example.weatherapp;

public class WeatherRVModal {

    private int time;
    private String temperature;
    private String icon;
    private double windSpeed;
    private String timeZone;
    private String isday;

    public int getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getTimeZone() {
        return timeZone;
    }
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getIsday() {
        return isday;
    }
    public void setIsday(String isday) {
        this.isday = isday;
    }
    public WeatherRVModal(int time, String temperature, String icon, double windSpeed, String timeZone,String isday) {
        this.time = time;
        this.temperature = temperature;
        this.icon = icon;
        this.windSpeed = windSpeed;
        this.timeZone = timeZone;
        this.isday = isday;
    }
}
