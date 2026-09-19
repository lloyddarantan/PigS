package com.example.pigs;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private ImageView imgLogo;
    private TableLayout tableTemperature;
    private MaterialCardView btnVaccination;

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
        showMainScreen();
    }

    // Load and bind activity_main.xml
    private void showMainScreen() {
        setContentView(R.layout.activity_main);

        imgLogo = findViewById(R.id.imgLogo);
        tableTemperature = findViewById(R.id.tableTemperature);
        btnVaccination = findViewById(R.id.btnVaccination);

        populateTemperatureTable();

        imgLogo.setOnClickListener(v -> showLoginScreen());

        btnVaccination.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Vaccination Module Clicked", Toast.LENGTH_SHORT).show()
        );
    }

    // Load and bind activity_login.xml
    private void showLoginScreen() {
        setContentView(R.layout.activity_login);

        AppCompatButton btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> showMainScreen());
    }

    private void populateTemperatureTable() {
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