package com.example.pigs.controller;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pigs.R;
import com.example.pigs.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout layoutFirstName, layoutLastName, layoutEmail, layoutPhone, layoutPassword, layoutConfirmPassword;
    private TextInputEditText etFirstName, etLastName, etEmail, etPhone, etPassword, etConfirmPassword;
    private AutoCompleteTextView spinnerRole;
    private MaterialButton btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
        setupRoleDropdown();

        btnCreate.setOnClickListener(v -> performRegistration());
    }

    private void initViews() {
        layoutFirstName = findViewById(R.id.layoutFirstName);
        layoutLastName = findViewById(R.id.layoutLastName);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutPassword = findViewById(R.id.layoutPassword);
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        spinnerRole = findViewById(R.id.spinnerRole);
        btnCreate = findViewById(R.id.btnCreate);
    }

    private void setupRoleDropdown() {
        String[] roles = new String[]{"User", "Admin", "Farm Technician"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        spinnerRole.setAdapter(adapter);
    }

    private void performRegistration() {
        clearErrors();

        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String role = spinnerRole.getText().toString().trim();

        if (!validateInputs(firstName, lastName, email, phone, password, confirmPassword)) {
            return;
        }

        User newUser = new User(firstName, lastName, email, phone, password, role);

        Toast.makeText(this, "Account created successfully for " + newUser.getFirstName() + "!", Toast.LENGTH_SHORT).show();

        finish();
    }

    private boolean validateInputs(String firstName, String lastName, String email, String phone, String password, String confirmPassword) {
        boolean isValid = true;

        if (firstName.isEmpty()) {
            layoutFirstName.setError("First name required");
            isValid = false;
        }

        if (lastName.isEmpty()) {
            layoutLastName.setError("Last name required");
            isValid = false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError("Valid email required");
            isValid = false;
        }

        if (phone.isEmpty()) {
            layoutPhone.setError("Phone number required");
            isValid = false;
        }

        if (password.length() < 6) {
            layoutPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (!confirmPassword.equals(password)) {
            layoutConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    private void clearErrors() {
        layoutFirstName.setError(null);
        layoutLastName.setError(null);
        layoutEmail.setError(null);
        layoutPhone.setError(null);
        layoutPassword.setError(null);
        layoutConfirmPassword.setError(null);
    }
}
