package com.example.pigs;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.example.pigs.controller.LoginActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ImageView imgLogo;
    private TableLayout tableTemperature;
    private MaterialCardView btnVaccination;
    private TextView tvWelcomeMessage;
    private LinearLayout cardHeatIndex;
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

        // Bind all views
        imgLogo = findViewById(R.id.imgLogo);
        tableTemperature = findViewById(R.id.tableTemperature);
        btnVaccination = findViewById(R.id.btnVaccination);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        cardHeatIndex = findViewById(R.id.cardHeatIndex);
        imgThermalCam = findViewById(R.id.imgThermalCam);

        // Populate table data
        populateTemperatureTable();

        // Handle User Email Intent
        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail != null && tvWelcomeMessage != null) {
            tvWelcomeMessage.setText("Welcome, \n" + userEmail);
        }

        // Open Heat Index Calendar Dialog
        if (cardHeatIndex != null) {
            cardHeatIndex.setOnClickListener(v -> showCalendarDialog());
        }

        // Logout listener
        if (imgLogo != null) {
            imgLogo.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // Vaccination button listener
        if (btnVaccination != null) {
            btnVaccination.setOnClickListener(v ->
                    Toast.makeText(MainActivity.this, "Vaccination Module Clicked", Toast.LENGTH_SHORT).show()
            );
        }

        // Thermal Camera image listener
        if (imgThermalCam != null) {
            imgThermalCam.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ThermalCamActivity.class);
                startActivity(intent);
            });
        }
    }

    private void showCalendarDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                R.style.CustomDatePickerTheme,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                    Toast.makeText(MainActivity.this, "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
                },
                year, month, day
        );

        datePickerDialog.show();
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