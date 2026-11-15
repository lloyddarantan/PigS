
package com.example.pigs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TutorialDetailActivity extends AppCompatActivity {

    TextView tvDetailTitle, tvWrittenContent, btnBack;
    LinearLayout writtenSection, videoSection;
    WebView webViewVideo;
    String tutorialTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_detail);

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvWrittenContent = findViewById(R.id.tvWrittenContent);

        writtenSection = findViewById(R.id.writtenSection);
        videoSection = findViewById(R.id.videoSection);

        webViewVideo = findViewById(R.id.webViewVideo);

        // Back button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tutorialTitle = getIntent().getStringExtra("tutorial_title");

        if (tutorialTitle == null) {
            tutorialTitle = "Tutorial";
        }

        tvDetailTitle.setText(tutorialTitle);

        // Read Tutorial
        findViewById(R.id.cardRead).setOnClickListener(v -> {
            videoSection.setVisibility(View.GONE);
            writtenSection.setVisibility(View.VISIBLE);

            tvWrittenContent.setText(getWrittenTutorial(tutorialTitle));

            webViewVideo.onPause();
            webViewVideo.setVisibility(View.GONE);
        });

        // Watch Video
        findViewById(R.id.cardVideo).setOnClickListener(v -> {
            String videoId = getVideoId(tutorialTitle);

            if (videoId.isEmpty()) {
                Toast.makeText(this,
                        "Video is not available yet.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            writtenSection.setVisibility(View.GONE);
            videoSection.setVisibility(View.VISIBLE);

            webViewVideo.setVisibility(View.VISIBLE);
            playVideo(videoId);
        });

        // Bottom Navigation
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
                finish();
                return true;

            } else if (id == R.id.support_id) {
                startActivity(new Intent(this, SupportActivity.class));
                finish();
                return true;
            }

            return false;
        });
    }

    // Written tutorial contents
    private String getWrittenTutorial(String title) {
        switch (title) {

            case "HOW TO USE PIGS":
                return "WELCOME TO PIGS!\n\n"
                        + "Follow these steps to get started:\n\n"
                        + "1. Open the Pig Care application.\n\n"
                        + "2. Go to the main dashboard.\n\n"
                        + "3. Check the available pig monitoring information.\n\n"
                        + "4. Select the feature you want to use.\n\n"
                        + "5. Review the displayed information regularly.\n\n"
                        + "You can now start using PigS!";

            case "RFID TUTORIAL":
                return "HOW TO USE THE RFID SENSOR\n\n"
                        + "Follow these steps:\n\n"
                        + "1. Make sure the RFID reader is connected and powered on.\n\n"
                        + "2. Prepare the RFID tag assigned to the pig.\n\n"
                        + "3. Place the tag near the RFID reader.\n\n"
                        + "4. Wait for the reader to detect the tag.\n\n"
                        + "5. Check the application for the corresponding pig information.\n\n"
                        + "Note: Make sure the RFID reader and application are properly connected.";

            case "SENSOR TUTORIAL":
                return "HOW TO CHECK SENSOR READINGS\n\n"
                        + "Follow these steps:\n\n"
                        + "1. Make sure the sensor is properly connected.\n\n"
                        + "2. Open the temperature monitoring section.\n\n"
                        + "3. Wait for the sensor to collect data.\n\n"
                        + "4. Check the displayed temperature reading.\n\n"
                        + "5. Monitor the readings regularly.\n\n"
                        + "Note: Check the sensor connection if no readings appear.";

            case "GETTING STARTED":
                return "GETTING STARTED WITH THE APP\n\n"
                        + "1. Open the Pig Care application.\n\n"
                        + "2. Explore the main dashboard.\n\n"
                        + "3. Navigate through the bottom navigation menu.\n\n"
                        + "4. Open the temperature section to view readings.\n\n"
                        + "5. Visit the tutorial section whenever you need help.";

            case "VIEW TEMPERATURE READINGS":
                return "HOW TO VIEW TEMPERATURE READINGS\n\n"
                        + "1. Open the Pig Care application.\n\n"
                        + "2. Select the temperature section.\n\n"
                        + "3. Wait for the sensor data to load.\n\n"
                        + "4. Review the displayed temperature.\n\n"
                        + "5. Check the sensor status if data is unavailable.";

            case "GET HELP AND SUPPORT":
                return "HOW TO GET SUPPORT\n\n"
                        + "1. Open the Pig Care application.\n\n"
                        + "2. Navigate to the Support section.\n\n"
                        + "3. Review the available help information.\n\n"
                        + "4. Follow the provided instructions.\n\n"
                        + "5. Contact the application administrator if you still need assistance.";

            default:
                return "Tutorial instructions are not available yet.";
        }
    }

    // YouTube video IDs
    private String getVideoId(String title) {
        switch (title) {

            case "HOW TO USE PIGS":
                return "06sDgp3wZUc";

            case "RFID TUTORIAL":
                return "nxjeOEQ3HkM";

            case "SENSOR TUTORIAL":
                return "k82juHJGzkI";

            case "GETTING STARTED":
                return "iUbTO-VODX0";

            case "VIEW TEMPERATURE READINGS":
                return "bLaBofVtqgk";

            case "GET HELP AND SUPPORT":
                return "zYdkWdYiU4w";

            default:
                return "";
        }
    }

    // Play YouTube video inside the app
    private void playVideo(String videoId) {

        WebSettings settings = webViewVideo.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(true);

        webViewVideo.setWebChromeClient(new WebChromeClient());
        webViewVideo.setWebViewClient(new android.webkit.WebViewClient());

        // Identify the Android app to YouTube
        String appId = getPackageName();
        String referrer = "https://" + appId;

        // Add the required Referer header
        java.util.HashMap<String, String> headers =
                new java.util.HashMap<>();

        headers.put("Referer", referrer);

        // YouTube embed URL
        String videoUrl = "https://www.youtube.com/embed/"
                + videoId
                + "?playsinline=1&rel=0";

        // Load video with app identification
        webViewVideo.loadUrl(videoUrl, headers);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (webViewVideo != null) {
            webViewVideo.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        if (webViewVideo != null) {
            webViewVideo.stopLoading();
            webViewVideo.destroy();
        }

        super.onDestroy();
    }
}
