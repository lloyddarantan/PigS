package com.example.pigs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SupportActivity extends AppCompatActivity {

    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        ratingBar = findViewById(R.id.ratingBar);

        if (ratingBar != null) {
            ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
                if (fromUser) {
                    Toast.makeText(SupportActivity.this, "Thank you! You rated: " + rating + " stars", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Setup Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // Highlight the Support icon as active
        bottomNav.setSelectedItemId(R.id.support_id);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.pig_dash) {
                // Navigate back to Dashboard
                Intent intent = new Intent(SupportActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.temp_id) {
                Intent intent = new Intent(SupportActivity.this, TempReadingActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.tutorial_id) {
                Toast.makeText(this, "Tutorial Module", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.support_id) {
                // Already on Support Page
                return true;
            }

            return false;
        });
    }
}