
package com.example.pigs;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pigs.controller.LoginActivity;
import com.example.pigs.network.ProfileApi;
import com.example.pigs.network.SessionManager;

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private EditText first;
    private EditText last;
    private EditText email;

    private TextView heading;

    private Button save;
    private Button delete;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check login session
        if (SessionManager.token(this).isEmpty()) {
            leaveAccount();
            return;
        }

        setContentView(R.layout.activity_profile);

        first = findViewById(R.id.profileFirst);
        last = findViewById(R.id.profileLast);
        email = findViewById(R.id.profileEmail);

        heading = findViewById(R.id.profileHeading);

        save = findViewById(R.id.profileSave);
        delete = findViewById(R.id.profileDelete);
        logout = findViewById(R.id.profileLogout);

        findViewById(R.id.profileBack)
                .setOnClickListener(v -> finish());

        save.setOnClickListener(v -> saveProfile());

        delete.setOnClickListener(v -> confirmDelete());

        logout.setOnClickListener(v -> confirmLogout());

        loadProfile();
    }

    private void toast(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    private void loadProfile() {

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
                            toast("Invalid profile response.");
                            return;
                        }

                        first.setText(
                                user.optString("first_name")
                        );

                        last.setText(
                                user.optString("last_name")
                        );

                        email.setText(
                                user.optString("email")
                        );

                        heading.setText(
                                user.optString("first_name")
                                        + " "
                                        + user.optString("last_name")
                        );
                    }

                    @Override
                    public void error(String message) {
                        toast(message);
                    }
                }
        );
    }

    private void saveProfile() {

        String firstName =
                first.getText().toString().trim();

        String lastName =
                last.getText().toString().trim();

        String emailAddress =
                email.getText().toString().trim();

        if (firstName.isEmpty()
                || lastName.isEmpty()
                || !Patterns.EMAIL_ADDRESS
                .matcher(emailAddress).matches()) {

            toast("Enter a valid name and email.");
            return;
        }

        try {
            JSONObject data = new JSONObject();

            data.put("first_name", firstName);
            data.put("last_name", lastName);
            data.put("email", emailAddress);

            save.setEnabled(false);

            ProfileApi.post(
                    this,
                    "update_profile.php",
                    data,
                    new ProfileApi.Callback() {

                        @Override
                        public void success(JSONObject response) {
                            save.setEnabled(true);

                            heading.setText(
                                    firstName + " " + lastName
                            );

                            setResult(RESULT_OK);

                            toast("Profile updated successfully.");
                        }

                        @Override
                        public void error(String message) {
                            save.setEnabled(true);
                            toast(message);
                        }
                    }
            );

        } catch (Exception e) {
            save.setEnabled(true);
            toast("Unable to prepare request.");
        }
    }

    private void confirmLogout() {

        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton(
                        "Log Out",
                        (dialog, which) -> logoutUser()
                )
                .show();
    }

    private void logoutUser() {

        logout.setEnabled(false);

        // If the token is missing, still allow local logout.
        if (SessionManager.token(this).isEmpty()) {
            leaveAccount();
            return;
        }

        // Ask PHP to invalidate the server-side token.
        ProfileApi.post(
                this,
                "logout.php",
                new JSONObject(),
                new ProfileApi.Callback() {

                    @Override
                    public void success(JSONObject response) {
                        leaveAccount();
                    }

                    @Override
                    public void error(String message) {

                        // Clear the local session and return
                        // to Sign In even if the server fails.
                        leaveAccount();
                    }
                }
        );
    }

    private void confirmDelete() {

        EditText password = new EditText(this);

        password.setHint("Current password");

        password.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        password.setPadding(48, 24, 48, 24);

        new AlertDialog.Builder(this)
                .setTitle("Delete Account?")
                .setMessage(
                        "This action is permanent. "
                                + "Enter your password to confirm."
                )
                .setView(password)
                .setNegativeButton("Cancel", null)
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            String enteredPassword =
                                    password.getText().toString();

                            if (enteredPassword.isEmpty()) {
                                toast("Password is required.");
                                return;
                            }

                            deleteUser(enteredPassword);
                        }
                )
                .show();
    }

    private void deleteUser(String password) {

        try {
            JSONObject data = new JSONObject();

            data.put("password", password);

            delete.setEnabled(false);

            ProfileApi.post(
                    this,
                    "delete_account.php",
                    data,
                    new ProfileApi.Callback() {

                        @Override
                        public void success(JSONObject response) {
                            toast("Account deleted successfully.");
                            leaveAccount();
                        }

                        @Override
                        public void error(String message) {
                            delete.setEnabled(true);
                            toast(message);
                        }
                    }
            );

        } catch (Exception e) {
            delete.setEnabled(true);
            toast("Unable to prepare request.");
        }
    }


    private void leaveAccount() {

        // Clear saved login session
        SessionManager.clear(this);

        // Redirect to Sign In page
        Intent intent = new Intent(
                ProfileActivity.this,
                LoginActivity.class
        );

        // Prevent returning to Profile/Dashboard using Back
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
