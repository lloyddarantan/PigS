
package com.example.pigs;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationsActivity extends Activity {

    TextView btnBack, btnDashboard, btnMarkRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        btnBack = findViewById(R.id.btnBack);
        btnDashboard = findViewById(R.id.btnDashboard);
        btnMarkRead = findViewById(R.id.btnMarkRead);

        btnBack.setOnClickListener(v -> finish());

        btnDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(
                    NotificationsActivity.this,
                    MainActivity.class
            );
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );
            startActivity(intent);
            finish();
        });

        btnMarkRead.setOnClickListener(v -> {
            Toast.makeText(
                    NotificationsActivity.this,
                    "All notifications marked as read",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
