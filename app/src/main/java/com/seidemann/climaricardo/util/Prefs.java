package com.seidemann.climaricardo.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    public static final String PREFS = "clima_prefs";
    public static final String CITY = "city";

    public static void setCity(Context c, String city) {
        SharedPreferences sp = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putString(CITY, city).apply();
    }
    public static String getCity(Context c) {
        SharedPreferences sp = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sp.getString(CITY, "São Paulo");
    }
}
