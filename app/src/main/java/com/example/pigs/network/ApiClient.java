
package com.example.pigs.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

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

    private static final String BASE_URL =
            "http://10.0.2.2/pigsensor_api/";

    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private static final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    private static final String PREF_NAME = "PigSensorSession";
    private static final String TOKEN_KEY = "API_TOKEN";

    private static Context appContext;

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String message);
    }

    // Initialize ApiClient with application context.
    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    // Save the authentication token.
    public static void saveToken(String token) {

        if (appContext == null) return;

        SharedPreferences preferences =
                appContext.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        preferences.edit()
                .putString(TOKEN_KEY, token)
                .apply();
    }

    // Retrieve the saved authentication token.
    private static String getToken() {

        if (appContext == null) return "";

        SharedPreferences preferences =
                appContext.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        return preferences.getString(TOKEN_KEY, "");
    }

    // Clear the saved token during logout.
    public static void clearToken() {

        if (appContext == null) return;

        SharedPreferences preferences =
                appContext.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        preferences.edit()
                .remove(TOKEN_KEY)
                .apply();
    }

    // REGISTER
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
            callback.onError("Unable to prepare registration data.");
            return;
        }

        request("register.php", data, false, callback);
    }

    // LOGIN
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
            callback.onError("Unable to prepare login data.");
            return;
        }

        request("login.php", data, false, callback);
    }

    // AUTHENTICATED POST REQUEST
    public static void post(
            String endpoint,
            JSONObject data,
            ApiCallback callback
    ) {
        request(endpoint, data, true, callback);
    }

    // GET REQUEST
    public static void get(
            String endpoint,
            ApiCallback callback
    ) {
        requestGet(endpoint, callback);
    }

    // POST REQUEST HANDLER
    private static void request(
            String endpoint,
            JSONObject data,
            boolean requiresAuth,
            ApiCallback callback
    ) {

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
                        "Authorization",
                        "Bearer " + getToken()
                );

                if (requiresAuth) {

                    String token = getToken();

                    if (token.isEmpty()) {
                        throw new Exception(
                                "You are not logged in. Please log in again."
                        );
                    }

                    connection.setRequestProperty(
                            "Authorization",
                            "Bearer " + token
                    );
                }

                byte[] requestData =
                        data.toString().getBytes(StandardCharsets.UTF_8);

                try (OutputStream output = connection.getOutputStream()) {
                    output.write(requestData);
                    output.flush();
                }

                handleResponse(connection, callback);

            } catch (Exception e) {

                postError(callback, e.getMessage());

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    // GET REQUEST HANDLER
    private static void requestGet(
            String endpoint,
            ApiCallback callback
    ) {

        executor.execute(() -> {

            HttpURLConnection connection = null;

            try {

                String token = getToken();

                if (token.isEmpty()) {
                    throw new Exception(
                            "You are not logged in. Please log in again."
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

                postError(callback, e.getMessage());

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    // HANDLE SERVER RESPONSE
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
            throw new Exception("No response from server. HTTP " + responseCode);
        }

        StringBuilder response = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8
                ))) {

            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        String responseText = response.toString().trim();

        android.util.Log.d(
                "API_RESPONSE",
                "URL: " + connection.getURL()
                        + " | HTTP: " + responseCode
                        + " | Body: " + responseText
        );

        JSONObject jsonResponse;

        try {
            jsonResponse = new JSONObject(responseText);

        } catch (org.json.JSONException e) {

            String preview = responseText.length() > 500
                    ? responseText.substring(0, 500)
                    : responseText;

            throw new Exception(
                    "Server returned HTML or invalid JSON. HTTP "
                            + responseCode + ". Response: " + preview
            );
        }

        if (responseCode >= 200
                && responseCode < 300
                && jsonResponse.optBoolean("success", false)) {

            mainHandler.post(() ->
                    callback.onSuccess(jsonResponse)
            );

        } else {

            String message = jsonResponse.optString(
                    "message",
                    "Request failed. HTTP " + responseCode
            );

            postError(callback, message);
        }
    }

    private static void postError(
            ApiCallback callback,
            String message
    ) {

        mainHandler.post(() ->
                callback.onError(
                        message == null
                                ? "Unable to connect to the server."
                                : message
                )
        );
    }
}
