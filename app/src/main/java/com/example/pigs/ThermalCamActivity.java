package com.example.pigs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.temp_id);
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.pig_dash) {
                    Intent intent = new Intent(ThermalCamActivity.this, MainActivity.class);
                    // Clear the back stack or just finish this activity
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.temp_id) {
                    // Already here
                    return true;
                } else if (id == R.id.tutorial_id) {
                    Toast.makeText(this, "Tutorial Module", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.support_id) {
                    Intent intent = new Intent(ThermalCamActivity.this, SupportActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            });
        }
    }
}