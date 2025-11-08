package com.seidemann.climaricardo;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("Sobre");
        ImageView photo = findViewById(R.id.photo);
        TextView name = findViewById(R.id.txtName);
        TextView ra = findViewById(R.id.txtRa);
        TextView course = findViewById(R.id.txtCourse);
        photo.setImageResource(R.drawable.ricardo);
        name.setText("Ricardo Seidemann Martins");
        ra.setText("RA: 09049939");
        course.setText("Análise e Desenvolvimento de Sistemas");
    }
}
