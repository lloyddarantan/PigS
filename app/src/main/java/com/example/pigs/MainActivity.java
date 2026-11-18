
package com.example.pigs;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pigs.controller.LoginActivity;
import com.example.pigs.network.ProfileApi;
import com.example.pigs.network.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

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

    private BottomNavigationView bottomNav;

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (!checkLoginSession()) {
            return;
        }

        imgLogo = findViewById(R.id.imgLogo);
        tableTemperature = findViewById(R.id.tableTemperature);
        btnVaccination = findViewById(R.id.btnVaccination);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        cardHeatIndex = findViewById(R.id.cardHeatIndex);
        imgThermalCam = findViewById(R.id.imgThermalCam);
        btnNews = findViewById(R.id.btnNews);

        setupNotificationsButton();
        populateTemperatureTable();
        setupDashboardClicks();
        setupBottomNavigation();

        userEmail = getIntent().getStringExtra("USER_EMAIL");

        if (userEmail != null && tvWelcomeMessage != null) {
            tvWelcomeMessage.setText("Welcome,\n" + userEmail);
        }
    }

    private void setupDashboardClicks() {

        if (cardHeatIndex != null) {
            cardHeatIndex.setOnClickListener(v -> showCalendarDialog());
        }

        if (imgLogo != null) {
            imgLogo.setOnClickListener(v -> {
                Intent intent = new Intent(
                        MainActivity.this,
                        ProfileActivity.class
                );
                startActivity(intent);
            });
        }

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
                intent.setData(Uri.parse(
                        "https://www.philstar.com/nation/2026/08/05/2547027/negros-asf-hit-areas-now-17"
                ));

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(
                            this,
                            "Unable to open news link",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }

        if (imgThermalCam != null) {
            imgThermalCam.setOnClickListener(v -> {
                Intent intent = new Intent(
                        MainActivity.this,
                        ThermalCamActivity.class
                );
                startActivity(intent);
            });
        }
    }

    private void setupBottomNavigation() {

        bottomNav = findViewById(R.id.bottomNavigationView);

        if (bottomNav == null) {
            return;
        }

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.pig_dash) {
                return true;

            } else if (id == R.id.temp_id) {

                startActivity(new Intent(
                        MainActivity.this,
                        TempReadingActivity.class
                ));

                return true;

            } else if (id == R.id.tutorial_id) {

                startActivity(new Intent(
                        MainActivity.this,
                        TutorialActivity.class
                ));

                return true;

            } else if (id == R.id.support_id) {

                Intent intent = new Intent(
                        MainActivity.this,
                        MainSupportActivity.class
                );

                startActivity(intent);

                overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                );

                return true;
            }

            return false;
        });
    }

    private void setupNotificationsButton() {

        FrameLayout btnNotifications =
                findViewById(R.id.btnNotifications);

        if (btnNotifications != null) {
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

                    Intent intent = new Intent(
                            MainActivity.this,
                            LoginActivity.class
                    );

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(Color.parseColor("#FA5656"));

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(Color.parseColor("#FF8A8A"));
    }

    private void populateTemperatureTable() {

        if (tableTemperature == null) {
            return;
        }

        tableTemperature.removeAllViews();

        for (String[] data : temperatureData) {

            TableRow row = new TableRow(this);

            TableRow.LayoutParams rowParams =
                    new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );

            row.setLayoutParams(rowParams);

            TextView tvRfid = new TextView(this);

            TableRow.LayoutParams paramRfid =
                    new TableRow.LayoutParams(
                            0,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            1.0f
                    );

            tvRfid.setLayoutParams(paramRfid);
            tvRfid.setText(data[0]);
            tvRfid.setTextSize(12);
            tvRfid.setTextColor(Color.parseColor("#1A1A1A"));
            tvRfid.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView tvStatus = new TextView(this);

            TableRow.LayoutParams paramStatus =
                    new TableRow.LayoutParams(
                            0,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            1.0f
                    );

            tvStatus.setLayoutParams(paramStatus);
            tvStatus.setText(data[1]);
            tvStatus.setTextSize(12);
            tvStatus.setGravity(Gravity.END);
            tvStatus.setTypeface(null, android.graphics.Typeface.BOLD);

            if ("High".equalsIgnoreCase(data[1])) {
                tvStatus.setTextColor(Color.parseColor("#B71C1C"));
            } else {
                tvStatus.setTextColor(Color.parseColor("#2E7D32"));
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

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        MainActivity.this,
                        R.style.CustomDatePickerTheme,
                        (view, selectedYear, selectedMonth, selectedDay) -> {

                            String selectedDate =
                                    (selectedMonth + 1) + "/" +
                                            selectedDay + "/" +
                                            selectedYear;

                            Toast.makeText(
                                    MainActivity.this,
                                    "Selected Date: " + selectedDate,
                                    Toast.LENGTH_SHORT
                            ).show();
                        },
                        year,
                        month,
                        day
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

                        firstName = user.optString("first_name");
                        lastName = user.optString("last_name");
                        userEmail = user.optString("email");

                        runOnUiThread(() -> {

                            TextView welcome =
                                    findViewById(R.id.tvWelcomeMessage);

                            if (welcome != null) {
                                welcome.setText(
                                        "Welcome,\n" + userEmail
                                );
                            }
                        });
                    }

                    @Override
                    public void error(String message) {
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
