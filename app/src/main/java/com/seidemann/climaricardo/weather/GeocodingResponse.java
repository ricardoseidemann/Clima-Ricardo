package com.seidemann.climaricardo.weather;
public class GeocodingResponse {
    public java.util.List<Result> results;
    public static class Result {
        public String name;
        public String country;
        public String admin1;
        public double latitude;
        public double longitude;
    }
}
