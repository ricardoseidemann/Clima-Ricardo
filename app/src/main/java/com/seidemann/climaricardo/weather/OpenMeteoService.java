package com.seidemann.climaricardo.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenMeteoService {
    @GET("v1/search")
    Call<GeocodingResponse> geocode(
        @Query("name") String name,
        @Query("count") int count,
        @Query("language") String language,
        @Query("format") String format
    );
    @GET("v1/forecast")
    Call<OpenMeteoResponse> forecast(
        @Query("latitude") double lat,
        @Query("longitude") double lon,
        @Query("daily") String daily,
        @Query("timezone") String timezone,
        @Query("forecast_days") int days
    );
}
