package com.seidemann.climaricardo.ui;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.seidemann.climaricardo.R;
import com.seidemann.climaricardo.util.Prefs;

import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private GoogleMap gmap;
    private String lastCityMoved = null;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (map != null) map.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gmap = googleMap;
        // Primeira carga com a cidade atual salva
        moveToCity(Prefs.getCity(requireContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recarrega quando o usuário volta para a aba
        requireContext().getSharedPreferences(Prefs.PREFS, 0)
                .registerOnSharedPreferenceChangeListener(this);
        moveToCity(Prefs.getCity(requireContext()));
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().getSharedPreferences(Prefs.PREFS, 0)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Prefs.CITY.equals(key)) {
            // Cidade mudou na tela de Previsão -> atualiza o mapa
            moveToCity(Prefs.getCity(requireContext()));
        }
    }

    private void moveToCity(String city) {
        if (gmap == null || city == null || city.trim().isEmpty()) return;

        // Evita refazer geocoding desnecessário
        if (city.equalsIgnoreCase(lastCityMoved)) return;
        lastCityMoved = city;

        LatLng pos = new LatLng(-23.5505, -46.6333); // fallback SP
        try {
            Geocoder gc = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> list = gc.getFromLocationName(city, 1);
            if (list != null && !list.isEmpty()) {
                Address a = list.get(0);
                pos = new LatLng(a.getLatitude(), a.getLongitude());
            }
        } catch (Exception ignored) {}

        gmap.clear();
        gmap.addMarker(new MarkerOptions().position(pos).title(city));
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 11f));
    }
}
