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
import android.widget.TextClock;
import android.widget.Toast;
import android.net.Uri;
import android.content.Intent;

import android.widget.FrameLayout;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.example.pigs.controller.LoginActivity;
import com.example.pigs.controller.LoginActivity;


import android.content.Intent;
import com.example.pigs.network.SessionManager;
import com.example.pigs.network.ProfileApi;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ImageView imgLogo;
    private TableLayout tableTemperature;
    private MaterialCardView btnVaccination;
    private TextView tvWelcomeMessage;
    private LinearLayout cardHeatIndex;
    private ImageView imgThermalCam;
    private MaterialCardView btnNews;

    private final String[][] temperatureData = {
            {"001", "Normal"},
            {"002", "Normal"},
            {"003", "High"},
            {"004", "Normal"},
            {"005", "Normal"},
            {"008", "Normal"},
            {"011", "Normal"}
    };

    private int userId;
    private String userEmail;
    private String firstName;
    private String lastName;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if the user is logged in
        if (!checkLoginSession()) {
            return;
        }

        setupNotificationsButton();


        imgLogo = findViewById(R.id.imgLogo);
        tableTemperature = findViewById(R.id.tableTemperature);
        btnVaccination = findViewById(R.id.btnVaccination);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        cardHeatIndex = findViewById(R.id.cardHeatIndex);
        imgThermalCam = findViewById(R.id.imgThermalCam);
        btnNews = findViewById(R.id.btnNews);



        populateTemperatureTable();

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
                Intent intent = new Intent(
                        MainActivity.this,
                        ProfileActivity.class
                );
                startActivity(intent);
            });
        }

// Open Vaccination Dashboard
        if (btnVaccination != null) {
            btnVaccination.setOnClickListener(v -> {

                Intent intent = new Intent(
                        MainActivity.this,
                        VaccinationActivity.class
                );

                startActivity(intent);

            });
        }
        if (btnNews != null) {
            btnNews.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.philstar.com/nation/2026/08/05/2547027/negros-asf-hit-areas-now-17"));
                startActivity(intent);
            });
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
                Intent intent = new Intent(MainActivity.this, TempReadingActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.tutorial_id) {
                Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.support_id) {
                // Launches SupportActivity containing the RatingBar
                Intent intent = new Intent(MainActivity.this, SupportActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }


    // NOTIF BUTTON

    private void setupNotificationsButton() {

        // Find the Notifications button from activity_main.xml
        FrameLayout btnNotifications =
                findViewById(R.id.btnNotifications);

        if (btnNotifications != null) {

            // Open Notifications Page when clicked
            btnNotifications.setOnClickListener(v -> {

                Intent intent = new Intent(
                        MainActivity.this,
                        NotificationsActivity.class
                );

                startActivity(intent);
            });
        }
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


    private boolean checkLoginSession() {

        if (SessionManager.token(this).isEmpty()) {

            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();

            return false;
        }

        return true;
    }


    private void refreshDashboardProfile() {

        if (SessionManager.token(this).isEmpty()) {
            return;
        }

        ProfileApi.post(
                this,
                "get_profile.php",
                new JSONObject(),
                new ProfileApi.Callback() {

                    @Override
                    public void success(JSONObject response) {

                        JSONObject user =
                                response.optJSONObject("user");

                        if (user == null) {
                            return;
                        }

                        String firstName =
                                user.optString("first_name");

                        String lastName =
                                user.optString("last_name");

                        String email =
                                user.optString("email");

                        // Update stored dashboard information
                        getIntent().putExtra(
                                "USER_FIRST_NAME",
                                firstName
                        );

                        getIntent().putExtra(
                                "USER_LAST_NAME",
                                lastName
                        );

                        getIntent().putExtra(
                                "USER_EMAIL",
                                email
                        );

                        // Update welcome message
                        TextView welcome =
                                findViewById(R.id.tvWelcomeMessage);

                        if (welcome != null) {
                            welcome.setText(
                                    "Welcome,\n" + email
                            );
                        }
                    }

                    @Override
                    public void error(String message) {
                        // Keep existing dashboard information
                        // if the server is temporarily unavailable
                    }
                }
        );
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (checkLoginSession()) {
            refreshDashboardProfile();
        }
    }
}


