
package com.example.pigs;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TutorialActivity extends AppCompatActivity {

    LinearLayout pigTutorials, appTutorials;
    Button btnPig, btnApp;
    TextView tvCategoryTitle;
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // Back button
        TextView tvBack = findViewById(R.id.tvBack);

        tvBack.setOnClickListener(v -> {
            finish();
        });

        // Tutorial sections
        pigTutorials = findViewById(R.id.pigTutorials);
        appTutorials = findViewById(R.id.appTutorials);

        // Category buttons
        btnPig = findViewById(R.id.btnPig);
        btnApp = findViewById(R.id.btnApp);

        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        etSearch = findViewById(R.id.etSearchTutorial);

        btnPig.setOnClickListener(v -> showPigTutorials());
        btnApp.setOnClickListener(v -> showAppTutorials());

        // Pig tutorial cards
        findViewById(R.id.cardPigSensor).setOnClickListener(v ->
                openTutorial("HOW TO USE PIGS"));

        findViewById(R.id.cardRFID).setOnClickListener(v ->
                openTutorial("RFID TUTORIAL"));

        findViewById(R.id.cardSensor).setOnClickListener(v ->
                openTutorial("SENSOR TUTORIAL"));

        // App tutorial cards
        findViewById(R.id.cardApp).setOnClickListener(v ->
                openTutorial("GETTING STARTED"));

        findViewById(R.id.cardReadings).setOnClickListener(v ->
                openTutorial("VIEW TEMPERATURE READINGS"));

        findViewById(R.id.cardSupport).setOnClickListener(v ->
                openTutorial("GET HELP AND SUPPORT"));

        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start, int before, int count) {
                filterTutorials(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.navbot);
        bottomNav.setSelectedItemId(R.id.tutorial_id);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.pig_dash) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;

            } else if (id == R.id.temp_id) {
                startActivity(new Intent(this, TempReadingActivity.class));
                finish();
                return true;

            } else if (id == R.id.tutorial_id) {
                return true;

            } else if (id == R.id.support_id) {
                startActivity(new Intent(this, SupportActivity.class));
                finish();
                return true;
            }

            return false;
        });

        showPigTutorials();
    }

    private void showPigTutorials() {
        pigTutorials.setVisibility(View.VISIBLE);
        appTutorials.setVisibility(View.GONE);

        tvCategoryTitle.setText("Pig Care Tutorials");

        btnPig.setBackgroundTintList(
                ColorStateList.valueOf(Color.rgb(255, 82, 82)));

        btnApp.setBackgroundTintList(
                ColorStateList.valueOf(Color.rgb(221, 170, 170)));

        etSearch.setText("");
    }

    private void showAppTutorials() {
        pigTutorials.setVisibility(View.GONE);
        appTutorials.setVisibility(View.VISIBLE);

        tvCategoryTitle.setText("App Tutorials");

        btnApp.setBackgroundTintList(
                ColorStateList.valueOf(Color.rgb(255, 82, 82)));

        btnPig.setBackgroundTintList(
                ColorStateList.valueOf(Color.rgb(221, 170, 170)));

        etSearch.setText("");
    }

    private void openTutorial(String title) {
        Intent intent = new Intent(
                TutorialActivity.this,
                TutorialDetailActivity.class);

        intent.putExtra("tutorial_title", title);
        startActivity(intent);
    }

    private void filterTutorials(String query) {
        String search = query.toLowerCase().trim();

        LinearLayout currentList =
                pigTutorials.getVisibility() == View.VISIBLE
                        ? pigTutorials : appTutorials;

        for (int i = 0; i < currentList.getChildCount(); i++) {
            View card = currentList.getChildAt(i);

            String cardText = getAllText(card).toLowerCase();

            card.setVisibility(
                    cardText.contains(search) ? View.VISIBLE : View.GONE);
        }
    }

    private String getAllText(View view) {
        StringBuilder result = new StringBuilder();

        if (view instanceof TextView) {
            result.append(((TextView) view).getText()).append(" ");
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); i++) {
                result.append(getAllText(group.getChildAt(i)));
            }
        }

        return result.toString();
    }
}
