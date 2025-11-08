package com.seidemann.climaricardo.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.seidemann.climaricardo.R;
import com.seidemann.climaricardo.util.Prefs;
import com.seidemann.climaricardo.weather.ForecastAdapter;
import com.seidemann.climaricardo.weather.GeocodingResponse;
import com.seidemann.climaricardo.weather.OpenMeteoResponse;
import com.seidemann.climaricardo.weather.OpenMeteoService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView recycler;
    private ForecastAdapter adapter;
    private OpenMeteoService geoApi, meteoApi;
    private TextView txtCity;
    private EditText inputCity;
    private Button btnSearch;
    private ListView listSuggestions;
    private ArrayAdapter<String> suggestAdapter;
    private List<GeocodingResponse.Result> lastResults = new ArrayList<>();

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    String contents = result.getContents().trim();
                    Prefs.setCity(requireContext(), contents);
                }
            });

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forecast, container, false);
        txtCity = v.findViewById(R.id.txtCity);
        recycler = v.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ForecastAdapter();
        recycler.setAdapter(adapter);
        inputCity = v.findViewById(R.id.inputCity);
        btnSearch = v.findViewById(R.id.btnSearch);
        listSuggestions = v.findViewById(R.id.listSuggestions);
        suggestAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        listSuggestions.setAdapter(suggestAdapter);
        listSuggestions.setVisibility(View.GONE);

        Retrofit geoRetrofit = new Retrofit.Builder()
                .baseUrl("https://geocoding-api.open-meteo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Retrofit meteoRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        geoApi = geoRetrofit.create(OpenMeteoService.class);
        meteoApi = meteoRetrofit.create(OpenMeteoService.class);

        btnSearch.setOnClickListener(view -> applyCity(inputCity.getText().toString().trim()));

        inputCity.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String q = s.toString().trim();
                if (q.length() < 2) { listSuggestions.setVisibility(View.GONE); return; }
                geoApi.geocode(q, 5, "pt", "json").enqueue(new Callback<GeocodingResponse>() {
                    @Override public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                        if (!response.isSuccessful() || response.body()==null || response.body().results==null || response.body().results.isEmpty()) {
                            listSuggestions.setVisibility(View.GONE);
                            return;
                        }
                        lastResults = response.body().results;
                        ArrayList<String> names = new ArrayList<>();
                        for (GeocodingResponse.Result r : lastResults) {
                            String label = r.name + (r.admin1!=null? (", " + r.admin1) : "") + (r.country!=null? (" - " + r.country) : "");
                            names.add(label);
                        }
                        suggestAdapter.clear();
                        suggestAdapter.addAll(names);
                        suggestAdapter.notifyDataSetChanged();
                        listSuggestions.setVisibility(View.VISIBLE);
                    }
                    @Override public void onFailure(Call<GeocodingResponse> call, Throwable t) { listSuggestions.setVisibility(View.GONE); }
                });
            }
        });

        listSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < lastResults.size()) {
                    GeocodingResponse.Result r = lastResults.get(position);
                    String chosen = r.name + (r.admin1!=null? (", " + r.admin1) : "");
                    inputCity.setText(chosen);
                    listSuggestions.setVisibility(View.GONE);
                    applyCity(chosen);
                }
            }
        });

        loadWeather();
        return v;
    }

    @Override public void onResume() {
        super.onResume();
        requireContext().getSharedPreferences(Prefs.PREFS, 0).registerOnSharedPreferenceChangeListener(this);
    }
    @Override public void onPause() {
        super.onPause();
        requireContext().getSharedPreferences(Prefs.PREFS, 0).unregisterOnSharedPreferenceChangeListener(this);
    }
    @Override public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if (Prefs.CITY.equals(key)) loadWeather();
    }

    private void applyCity(String city) {
        if (city.isEmpty()) { Toast.makeText(requireContext(), "Digite uma cidade", Toast.LENGTH_SHORT).show(); return; }
        Prefs.setCity(requireContext(), city);
    }

    private void loadWeather() {
        String city = Prefs.getCity(requireContext());
        txtCity.setText("Cidade: " + city);
        queryGeocode(city, true);
    }

    private void queryGeocode(String city, boolean allowFallback) {
        geoApi.geocode(city, 1, "pt", "json").enqueue(new Callback<GeocodingResponse>() {
            @Override public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> res) {
                if (res.isSuccessful() && res.body()!=null && res.body().results!=null && !res.body().results.isEmpty()) {
                    GeocodingResponse.Result r = res.body().results.get(0);
                    String canonical = r.name + (r.admin1!=null? (", " + r.admin1) : "") + (r.country!=null? (" - " + r.country) : "");
                    txtCity.setText("Cidade: " + canonical);
                    requestForecast(r.latitude, r.longitude);
                    return;
                }

                if (allowFallback) {
                    String noUf = city.contains(",") ? city.split(",")[0].trim() : city.trim();
                    if (!noUf.equalsIgnoreCase(city)) { queryGeocode(noUf, false); return; }
                    String[] parts = noUf.split("\s+");
                    if (parts.length > 1) { queryGeocode(parts[0], false); return; }
                    txtCity.setText("Cidade: São Paulo - BR (fallback)");
                    requestForecast(-23.5505, -46.6333);
                    return;
                }

                Toast.makeText(getContext(), "Cidade não encontrada: " + city, Toast.LENGTH_SHORT).show();
            }
            @Override public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Falha geocoding: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestForecast(double lat, double lon) {
        String daily = "weathercode,temperature_2m_max,temperature_2m_min,precipitation_sum";
        meteoApi.forecast(lat, lon, daily, "auto", 7).enqueue(new Callback<OpenMeteoResponse>() {
            @Override public void onResponse(Call<OpenMeteoResponse> call, Response<OpenMeteoResponse> response) {
                if (!response.isSuccessful() || response.body()==null || response.body().daily==null) {
                    Toast.makeText(getContext(), "Erro na previsão", Toast.LENGTH_SHORT).show();
                    return;
                }
                OpenMeteoResponse.Daily d = response.body().daily;
                java.util.List<ForecastAdapter.DayItem> out = new java.util.ArrayList<>();
                for (int i = 0; i < d.time.length; i++) {
                    ForecastAdapter.DayItem it = new ForecastAdapter.DayItem();
                    it.date = d.time[i];
                    it.code = d.weathercode[i];
                    it.desc = codeToDesc(it.code);
                    it.tmax = d.temperature_2m_max[i];
                    it.tmin = d.temperature_2m_min[i];
                    it.rain = d.precipitation_sum[i];
                    it.weekday = weekday(it.date);
                    out.add(it);
                }
                adapter.setData(out);
            }
            @Override public void onFailure(Call<OpenMeteoResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Falha previsão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String weekday(String ymd) {
        try {
            java.time.LocalDate d = java.time.LocalDate.parse(ymd);
            return d.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, new java.util.Locale("pt","BR"));
        } catch (Exception e) { return ""; }
    }
    private String codeToDesc(int code) {
        switch (code) {
            case 0: return "Céu limpo";
            case 1: case 2: case 3: return "Parcialmente nublado";
            case 45: case 48: return "Nevoeiro";
            case 51: case 53: case 55: return "Garoa";
            case 61: case 63: case 65: return "Chuva";
            case 71: case 73: case 75: case 77: return "Neve";
            case 80: case 81: case 82: return "Aguaceiros";
            case 85: case 86: return "Aguaceiros de neve";
            case 95: case 96: case 99: return "Trovoadas";
            default: return "Condição " + code;
        }
    }
}
