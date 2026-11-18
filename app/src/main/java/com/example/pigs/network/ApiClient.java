
package com.example.pigs.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiClient {

    // ==========================================
    // API CONFIGURATION
    // MERGED FROM ApiConfig.java
    // ==========================================

    private static final String BASE_URL =
            "http://10.0.2.2/pigsensor_api/";

    private static final String PREFS_NAME = "PigTechPrefs";
    private static final String KEY_TOKEN = "token";

    private static final String DEBUG_TOKEN = "a1b2c3d4e5f60718293a4b5c6d7e8f90a1b2c3d4e5f60718293a4b5c6d7e8f90";


    private static Context appContext;

    // ==========================================
    // EXECUTOR AND MAIN HANDLER
    // ==========================================

    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private static final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    // ==========================================
    // API CALLBACK
    // ==========================================

    public interface ApiCallback {
        void onSuccess(JSONObject response);

        void onError(String message);
    }

    // ==========================================
    // INITIALIZE API CLIENT
    // ==========================================

    public static void initialize(Context context) {
        if (context != null) {
            appContext = context.getApplicationContext();
        }
    }

    // ==========================================
    // BASE URL ACCESS
    // Used by TicketApi
    // ==========================================

    public static String getBaseUrl() {
        return BASE_URL;
    }

    // ==========================================
    // TOKEN MANAGEMENT
    // ==========================================

    // Save authentication token
    public static void saveToken(String token) {

        if (appContext == null || token == null || token.trim().isEmpty()) {
            Log.e("ApiClient", "Cannot save token. Context or token is missing.");
            return;
        }

        SharedPreferences preferences =
                appContext.getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                );

        preferences.edit()
                .putString(KEY_TOKEN, token)
                .apply();

        Log.d("ApiClient", "Authentication token saved.");
    }

    // Save token using an explicit Context if needed
    public static void saveToken(Context context, String token) {

        if (context == null || token == null || token.trim().isEmpty()) {
            return;
        }

        Context app = context.getApplicationContext();

        app.getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                ).edit()
                .putString(KEY_TOKEN, token)
                .apply();
    }

    // Retrieve token using Context
    // Compatible with ApiClient.getToken(this)
    public static String getToken(Context context) {

        if (context == null) {
            return "";
        }

        SharedPreferences preferences =
                context.getApplicationContext().getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                );

        return preferences.getString(KEY_TOKEN, "");
    }

    // Retrieve token using initialized application context
    private static String getToken() {
        return getToken(appContext);
    }

    // Check whether a user has a saved token
    public static boolean isLoggedIn(Context context) {
        String token = getToken(context);
        return !token.isEmpty();
    }

    // Clear token during logout
    public static void clearToken() {

        if (appContext == null) {
            return;
        }

        appContext.getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                ).edit()
                .remove(KEY_TOKEN)
                .apply();

        Log.d("ApiClient", "Authentication token cleared.");
    }

    // Clear token using an explicit Context
    public static void clearToken(Context context) {

        if (context == null) {
            return;
        }

        context.getApplicationContext().getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                ).edit()
                .remove(KEY_TOKEN)
                .apply();
    }

    // ==========================================
    // REGISTER
    // ==========================================

    public static void register(
            String firstName,
            String lastName,
            String email,
            String phone,
            String password,
            ApiCallback callback
    ) {

        JSONObject data = new JSONObject();

        try {
            data.put("first_name", firstName);
            data.put("last_name", lastName);
            data.put("email", email);
            data.put("phone_number", phone);
            data.put("password", password);

        } catch (Exception e) {
            postError(callback,
                    "Unable to prepare registration data.");
            return;
        }

        request("register.php", data, false, callback);
    }

    // ==========================================
    // LOGIN
    // ==========================================

    public static void login(
            String email,
            String password,
            ApiCallback callback
    ) {

        JSONObject data = new JSONObject();

        try {
            data.put("email", email);
            data.put("password", password);

        } catch (Exception e) {
            postError(callback,
                    "Unable to prepare login data.");
            return;
        }

        request("login.php", data, false, callback);
    }

    // ==========================================
    // AUTHENTICATED POST REQUEST
    // ==========================================

    public static void post(
            String endpoint,
            JSONObject data,
            ApiCallback callback
    ) {

        request(endpoint, data, true, callback);
    }

    // ==========================================
    // GET REQUEST
    // ==========================================

    public static void get(
            String endpoint,
            ApiCallback callback
    ) {

        requestGet(endpoint, callback);
    }

    // ==========================================
    // POST REQUEST HANDLER
    // ==========================================

    private static void request(
            String endpoint,
            JSONObject data,
            boolean requiresAuth,
            ApiCallback callback
    ) {

        if (appContext == null) {
            postError(callback, "ApiClient is not initialized.");
            return;
        }

        if (data == null) {
            postError(callback, "Request data is empty.");
            return;
        }

        if (endpoint == null || endpoint.trim().isEmpty()) {
            postError(callback, "API endpoint is missing.");
            return;
        }

        executor.execute(() -> {

            HttpURLConnection connection = null;

            try {

                URL url = new URL(BASE_URL + endpoint);

                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setDoOutput(true);

                connection.setRequestProperty(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );

                connection.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                // Add authentication only when required
                if (requiresAuth) {

                    String token = getToken();

                    if (token.isEmpty()) {
                        throw new Exception(
                                "You're not logged in. Please log in again."
                        );
                    }

                    connection.setRequestProperty(
                            "Authorization",
                            "Bearer " + token
                    );
                }

                // Convert JSON to bytes
                byte[] requestData =
                        data.toString().getBytes(StandardCharsets.UTF_8);

                try (OutputStream output = connection.getOutputStream()) {

                    output.write(requestData);
                    output.flush();
                }

                handleResponse(connection, callback);

            } catch (Exception e) {

                Log.e("API_ERROR", "POST request failed", e);

                postError(callback, e.getMessage());

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    // ==========================================
    // GET REQUEST HANDLER
    // ==========================================

    private static void requestGet(
            String endpoint,
            ApiCallback callback
    ) {

        if (appContext == null) {
            postError(callback, "ApiClient is not initialized.");
            return;
        }

        if (endpoint == null || endpoint.trim().isEmpty()) {
            postError(callback, "API endpoint is missing.");
            return;
        }

        executor.execute(() -> {

            HttpURLConnection connection = null;

            try {

                String token = getToken();

                if (token.isEmpty()) {
                    throw new Exception(
                            "You're not logged in. Please log in again."
                    );
                }

                URL url = new URL(BASE_URL + endpoint);

                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                connection.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                connection.setRequestProperty(
                        "Authorization",
                        "Bearer " + token
                );

                handleResponse(connection, callback);

            } catch (Exception e) {

                Log.e("API_ERROR", "GET request failed", e);

                postError(callback, e.getMessage());

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    // ==========================================
    // HANDLE SERVER RESPONSE
    // ==========================================

    private static void handleResponse(
            HttpURLConnection connection,
            ApiCallback callback
    ) throws Exception {

        int responseCode = connection.getResponseCode();

        InputStream inputStream =
                responseCode >= 200 && responseCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();

        if (inputStream == null) {

            throw new Exception(
                    "No response from server. HTTP " + responseCode
            );
        }

        StringBuilder response = new StringBuilder();

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     inputStream,
                                     StandardCharsets.UTF_8
                             )
                     )) {

            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        String responseText = response.toString().trim();

        Log.d(
                "API_RESPONSE",
                "URL: " + connection.getURL()
                        + " | HTTP: " + responseCode
        );

        JSONObject jsonResponse;

        try {

            jsonResponse = new JSONObject(responseText);

        } catch (Exception e) {

            String preview = responseText.length() > 500
                    ? responseText.substring(0, 500)
                    : responseText;

            throw new Exception(
                    "Server returned invalid JSON. HTTP "
                            + responseCode
                            + ". Response: "
                            + preview
            );
        }

        // Check success response
        if (responseCode >= 200
                && responseCode < 300
                && jsonResponse.optBoolean("success", false)) {

            if (callback != null) {
                mainHandler.post(() ->
                        callback.onSuccess(jsonResponse)
                );
            }

        } else {

            String message = jsonResponse.optString(
                    "message",
                    "Request failed. HTTP " + responseCode
            );

            postError(callback, message);
        }
    }

    // ==========================================
    // ERROR HANDLER
    // ==========================================

    private static void postError(
            ApiCallback callback,
            String message
    ) {

        if (callback == null) {
            return;
        }

        mainHandler.post(() ->
                callback.onError(
                        message == null || message.isEmpty()
                                ? "Unable to connect to the server."
                                : message
                )
        );
    }
}
