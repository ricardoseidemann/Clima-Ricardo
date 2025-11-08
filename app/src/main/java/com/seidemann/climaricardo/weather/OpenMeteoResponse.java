package com.seidemann.climaricardo.weather;
public class OpenMeteoResponse {
    public Daily daily;
    public static class Daily {
        public String[] time;
        public int[] weathercode;
        public double[] temperature_2m_max;
        public double[] temperature_2m_min;
        public double[] precipitation_sum;
    }
}
