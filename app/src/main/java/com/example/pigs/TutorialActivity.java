package com.example.pigs; // Replace with your actual package name

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TutorialActivity extends AppCompatActivity {

    private WebView webViewVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // Initialize WebView
        webViewVideo = findViewById(R.id.webViewVideo);

        if (webViewVideo != null) {
            WebSettings webSettings = webViewVideo.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webViewVideo.setWebViewClient(new WebViewClient());

            // YouTube Video ID
            String videoId = "ezAxnDvlqd8";
            String html = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"
                    + videoId + "\" frameborder=\"0\" allowfullscreen></iframe>";

            webViewVideo.loadData(html, "text/html", "utf-8");
        }

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.navbot);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.tutorial_id);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.pig_dash) {
                    startActivity(new Intent(TutorialActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.temp_id) {
                    startActivity(new Intent(TutorialActivity.this, TempReadingActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.tutorial_id) {
                    return true;
                } else if (id == R.id.support_id) {
                    startActivity(new Intent(TutorialActivity.this, SupportActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }
}