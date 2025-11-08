package com.seidemann.climaricardo.weather;

public class WeatherResponse {
    public Results results;

    public static class Results {
        public String city;
        public java.util.List<Forecast> forecast;
    }

    public static class Forecast {
        public String date;
        public String weekday;
        public int max;
        public int min;
        public String description;
        public String condition;
    }
}
