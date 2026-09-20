package com.example.pigs.controller;

import android.content.Intent; // Added missing import for Intent
import android.os.Bundle;
import android.util.Patterns;
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

    private void handleLoginAttempt() {
        String emailInput = etEmail.getText().toString().trim();
        String passwordInput = etPassword.getText().toString().trim();

        if (emailInput.isEmpty()) {
            Toast.makeText(this, "Email address cannot be empty.", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        if (passwordInput.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }

        if (passwordInput.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }

        User currentUser = new User(emailInput, passwordInput);

        if (currentUser.isValid()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USER_EMAIL", currentUser.getEmail());
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Login failed. Please check your credentials.", Toast.LENGTH_LONG).show();
        }
    }
}