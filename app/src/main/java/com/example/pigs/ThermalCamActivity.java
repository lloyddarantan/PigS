package com.example.pigs;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ThermalCamActivity extends AppCompatActivity {

    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermalcam);

        btnBack = findViewById(R.id.btnBack);

        // Back button navigation
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}