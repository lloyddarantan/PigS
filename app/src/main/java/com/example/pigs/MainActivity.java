package com.example.pigs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.example.pigs.controller.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView imgLogo;
    private TableLayout tableTemperature;
    private MaterialCardView btnVaccination;
    private TextView tvWelcomeMessage;
    private ImageView imgThermalCam;

    private final String[][] temperatureData = {
            {"001", "Normal"},
            {"002", "Normal"},
            {"003", "High"},
            {"004", "Normal"},
            {"005", "Normal"},
            {"008", "Normal"},
            {"011", "Normal"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        imgLogo = findViewById(R.id.imgLogo);
        tableTemperature = findViewById(R.id.tableTemperature);
        btnVaccination = findViewById(R.id.btnVaccination);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        imgThermalCam = findViewById(R.id.imgThermalCam);

        populateTemperatureTable();

        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail != null && tvWelcomeMessage != null) {
            tvWelcomeMessage.setText("Welcome, \n" + userEmail);
        }

        //logout na di
        if (imgLogo != null) {
            imgLogo.setOnClickListener(v -> showLogoutConfirmationDialog());
        }

        if (btnVaccination != null) {
            btnVaccination.setOnClickListener(v ->
                    Toast.makeText(MainActivity.this, "Vaccination Module Clicked", Toast.LENGTH_SHORT).show()
            );
        }

        if (imgThermalCam != null) {
            imgThermalCam.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ThermalCamActivity.class);
                startActivity(intent);
            });
        }
//still no functionality for bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.pig_dash) {
                // Already on Dashboard
                return true;
            } else if (id == R.id.temp_id) {
                Intent intent = new Intent(MainActivity.this, ThermalCamActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.tutorial_id) {
                Toast.makeText(this, "Tutorial Module", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.support_id) {
                Toast.makeText(this, "Support Module", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out of PigSensor?")
                .setPositiveButton("Confirm", (d, which) -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    // Clear the back stack so the user can't press back to return here
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        // Apply theme colors to the modal buttons after showing the dialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FA5656")); // pig_red
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF8A8A")); // pig_pink
    }

    private void populateTemperatureTable() {
        if (tableTemperature == null) return;

        for (String[] data : temperatureData) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            );
            row.setLayoutParams(rowParams);

            // Left RFID Column
            TextView tvRfid = new TextView(this);
            TableRow.LayoutParams paramRfid = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
            tvRfid.setLayoutParams(paramRfid);
            tvRfid.setText(data[0]);
            tvRfid.setTextSize(12);
            tvRfid.setTextColor(Color.parseColor("#1A1A1A"));
            tvRfid.setTypeface(null, android.graphics.Typeface.BOLD);

            // Right Temp Status Column
            TextView tvStatus = new TextView(this);
            TableRow.LayoutParams paramStatus = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
            tvStatus.setLayoutParams(paramStatus);
            tvStatus.setText(data[1]);
            tvStatus.setTextSize(12);
            tvStatus.setGravity(Gravity.END);
            tvStatus.setTypeface(null, android.graphics.Typeface.BOLD);

            if ("High".equalsIgnoreCase(data[1])) {
                tvStatus.setTextColor(Color.parseColor("#B71C1C")); // Red
            } else {
                tvStatus.setTextColor(Color.parseColor("#2E7D32")); // Green
            }

            row.addView(tvRfid);
            row.addView(tvStatus);
            tableTemperature.addView(row);
        }
    }
}