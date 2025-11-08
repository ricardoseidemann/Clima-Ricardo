package com.seidemann.climaricardo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.seidemann.climaricardo.ui.ForecastFragment;
import com.seidemann.climaricardo.ui.MapFragment;

public class HomeActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setSupportActionBar(findViewById(R.id.toolbar));
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull @Override
            public Fragment createFragment(int position) {
                if (position == 0) return new ForecastFragment();
                return new MapFragment();
            }
            @Override
            public int getItemCount() { return 2; }
        });
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(position == 0 ? "Previsão" : "Mapa")).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
