package com.example.pigs;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TempReadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the tempreading layout
        setContentView(R.layout.activity_tempreading);
        // Optional back button handler from header layout
        if (findViewById(R.id.tvBack) != null) {
            findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        }

        // Setup BottomNavigationView inside TempReadingActivity
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.temp_id); // Highlight temperature icon

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.pig_dash) {
                    Intent intent = new Intent(TempReadingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.temp_id) {
                    // Already on Temperature Monitor screen
                    return true;
                } else if (id == R.id.tutorial_id) {
                    // Handle tutorial navigation
                    return true;
                } else if (id == R.id.support_id) {
                    // Handle support navigation
                    return true;
                }
                return false;
            });
        }
    }
}
