
package com.example.pigs.network;

import android.content.Context;
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

public class TicketApi {

    // ==========================================
    // CALLBACK
    // ==========================================

    public interface Callback {
        void onSuccess(JSONObject json);
        void onError(String message);
    }

    // ==========================================
    // EXECUTOR AND MAIN HANDLER
    // ==========================================

    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private static final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    // ==========================================
    // GET REQUEST
    // ==========================================

    public static void get(
            Context context,
            String endpoint,
            Callback callback
    ) {
        request(context, endpoint, "GET", null, callback);
    }

    // ==========================================
    // POST REQUEST
    // ==========================================

    public static void post(
            Context context,
            String endpoint,
            JSONObject body,
            Callback callback
    ) {
        request(context, endpoint, "POST", body, callback);
    }

    // ==========================================
    // REQUEST HANDLER
    // ==========================================

    private static void request(
            Context context,
            String endpoint,
            String method,
            JSONObject body,
            Callback callback
    ) {

        if (context == null) {
            postError(callback, "Application context is missing.");
            return;
        }

        if (endpoint == null || endpoint.trim().isEmpty()) {
            postError(callback, "API endpoint is missing.");
            return;
        }

        executor.execute(() -> {

            HttpURLConnection conn = null;

            try {

                String baseUrl = ApiClient.getBaseUrl();

                URL url = new URL(baseUrl + endpoint);

                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod(method);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                conn.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                // Get saved authentication token from ApiClient
                String token = ApiClient.getToken(
                        context.getApplicationContext()
                );

                if (token == null || token.isEmpty()) {
                    throw new Exception(
                            "Authentication token missing. Please log in again."
                    );
                }

                conn.setRequestProperty(
                        "Authorization",
                        "Bearer " + token
                );

                // Send JSON body for POST requests
                if (body != null) {

                    conn.setDoOutput(true);

                    conn.setRequestProperty(
                            "Content-Type",
                            "application/json; charset=UTF-8"
                    );

                    byte[] requestData = body.toString()
                            .getBytes(StandardCharsets.UTF_8);

                    try (OutputStream output =
                                 conn.getOutputStream()) {

                        output.write(requestData);
                        output.flush();
                    }
                }

                // Read server response
                int responseCode = conn.getResponseCode();

                InputStream stream =
                        responseCode >= 200 && responseCode < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream();

                String response = readAll(stream);

                Log.d(
                        "TicketApi",
                        "URL: " + url
                                + " | HTTP: " + responseCode
                                + " | Response: " + response
                );

                // Parse JSON response
                JSONObject json;

                try {

                    json = new JSONObject(response);

                } catch (Exception e) {

                    String preview = response.length() > 500
                            ? response.substring(0, 500)
                            : response;

                    throw new Exception(
                            "Server returned invalid JSON. HTTP "
                                    + responseCode
                                    + ". Response: "
                                    + preview
                    );
                }

                // Check API response
                if (responseCode >= 200
                        && responseCode < 300
                        && json.optBoolean("success", false)) {

                    postSuccess(callback, json);

                } else {

                    String message = json.optString(
                            "message",
                            "Request failed. HTTP " + responseCode
                    );

                    postError(callback, message);
                }

            } catch (Exception e) {

                Log.e(
                        "TicketApi",
                        "Request failed",
                        e
                );

                postError(
                        callback,
                        e.getMessage() == null
                                ? "Cannot connect to the PHP server."
                                : e.getMessage()
                );

            } finally {

                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    // ==========================================
    // READ SERVER RESPONSE
    // ==========================================

    private static String readAll(InputStream stream)
            throws Exception {

        if (stream == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     stream,
                                     StandardCharsets.UTF_8
                             )
                     )) {

            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        return sb.toString();
    }

    // ==========================================
    // SUCCESS CALLBACK
    // ==========================================

    private static void postSuccess(
            Callback callback,
            JSONObject json
    ) {

        if (callback == null) return;

        mainHandler.post(() ->
                callback.onSuccess(json)
        );
    }

    // ==========================================
    // ERROR CALLBACK
    // ==========================================

    private static void postError(
            Callback callback,
            String message
    ) {

        if (callback == null) return;

        mainHandler.post(() ->
                callback.onError(
                        message == null
                                ? "Unable to connect to the server."
                                : message
                )
        );
    }
}
