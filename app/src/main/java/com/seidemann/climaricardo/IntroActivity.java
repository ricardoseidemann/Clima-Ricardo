package com.seidemann.climaricardo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }, 1500);
    }
}
