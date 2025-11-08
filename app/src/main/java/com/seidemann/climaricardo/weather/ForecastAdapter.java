package com.seidemann.climaricardo.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.seidemann.climaricardo.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.Holder> {
    public static class DayItem {
        public String date;
        public String weekday;
        public String desc;
        public int code;
        public double tmax;
        public double tmin;
        public double rain;
    }
    private final List<DayItem> data = new ArrayList<>();

    public void setData(List<DayItem> d) { data.clear(); if (d!=null) data.addAll(d); notifyDataSetChanged(); }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false);
        return new Holder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        DayItem f = data.get(position);
        h.txtDate.setText(f.weekday + " - " + formatBr(f.date));
        h.txtDesc.setText(f.desc);
        h.txtTemp.setText(String.format(Locale.getDefault(), "%.0f° / %.0f°  •  Chuva: %.0f mm", f.tmax, f.tmin, f.rain));
        h.icon.setImageResource(iconForCode(f.code));
    }
    @Override public int getItemCount() { return data.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView txtDate, txtDesc, txtTemp;
        ImageView icon;
        Holder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            txtTemp = itemView.findViewById(R.id.txtTemp);
            icon = itemView.findViewById(R.id.iconWeather);
            CardView card = itemView.findViewById(R.id.card);
        }
    }
    private static String formatBr(String ymd) {
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date d = in.parse(ymd);
            SimpleDateFormat out = new SimpleDateFormat("dd/MM", new Locale("pt","BR"));
            return out.format(d);
        } catch (ParseException e) { return ymd; }
    }
    private int iconForCode(int code) {
        if (code == 0) return R.drawable.ic_weather_sun;
        if (code == 1 || code == 2 || code == 3) return R.drawable.ic_weather_cloud;
        if (code == 45 || code == 48) return R.drawable.ic_weather_fog;
        if (code == 51 || code == 53 || code == 55) return R.drawable.ic_weather_drizzle;
        if (code == 61 || code == 63 || code == 65 || code == 80 || code == 81 || code == 82) return R.drawable.ic_weather_rain;
        if (code == 71 || code == 73 || code == 75 || code == 77 || code == 85 || code == 86) return R.drawable.ic_weather_snow;
        if (code == 95 || code == 96 || code == 99) return R.drawable.ic_weather_thunder;
        return R.drawable.ic_weather_cloud;
    }
}
