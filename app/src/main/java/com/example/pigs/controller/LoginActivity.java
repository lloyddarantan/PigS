package com.example.pigs.controller;

import android.content.Intent; // Added missing import for Intent
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.example.pigs.R;
import com.example.pigs.MainActivity;
import com.example.pigs.model.User;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Button btnContinue;
    private TextView tvForgotPassword;
    private TextView tvCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Initialize Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnContinue = findViewById(R.id.btnContinue);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);

        // 2. Set up Listeners (Controller Logic)
        btnContinue.setOnClickListener(v -> handleLoginAttempt());

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
        });

        tvCreateAccount.setOnClickListener(v -> {
            Toast.makeText(this, "Create Account clicked", Toast.LENGTH_SHORT).show();
        });
    }

    // Kept only the version that handles the navigation to MainActivity
    private void handleLoginAttempt() {
        String emailInput = etEmail.getText().toString().trim();
        String passwordInput = etPassword.getText().toString().trim();

        User currentUser = new User(emailInput, passwordInput);

        if (currentUser.isValid()) {
            // SUCCESS: Navigate to MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

            // Pass the email data to the next screen
            intent.putExtra("USER_EMAIL", currentUser.getEmail());

            startActivity(intent);
            finish(); // Closes LoginActivity so the back button doesn't return to it

        } else {
            Toast.makeText(this, "Please enter a valid email and a 6+ char password.", Toast.LENGTH_LONG).show();
        }
    }
}