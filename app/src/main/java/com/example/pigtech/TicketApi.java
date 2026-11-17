package com.example.pigtech;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
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

/**
 * Talks to the PHP API. Network calls run on a background thread
 * (Android does not allow network on the main thread) and the result
 * is sent back on the main thread, so it is safe to touch views in the callbacks.
 */
public class TicketApi {

    public interface Callback {
        void onSuccess(JSONObject json);
        void onError(String message);
    }

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void get(Context context, String endpoint, Callback callback) {
        request(context, endpoint, "GET", null, callback);
    }

    public static void post(Context context, String endpoint, JSONObject body, Callback callback) {
        request(context, endpoint, "POST", body, callback);
    }

    private static void request(Context context, final String endpoint, final String method,
                                final JSONObject body, final Callback callback) {

        final String token = ApiConfig.getToken(context.getApplicationContext());

        executor.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ApiConfig.BASE_URL + endpoint);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(method);
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                if (body != null) {
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                    }
                }

                int code = conn.getResponseCode();
                InputStream stream = (code >= 400) ? conn.getErrorStream() : conn.getInputStream();
                String response = readAll(stream);

                JSONObject json = new JSONObject(response);
                if (json.optBoolean("success", false)) {
                    postSuccess(callback, json);
                } else {
                    postError(callback, json.optString("message", "Request failed (" + code + ")"));
                }

            } catch (JSONException e) {
                postError(callback, "Server sent an unexpected reply. Check the PHP files.");
            } catch (Exception e) {
                android.util.Log.e("TicketApi", "Request failed", e);
                postError(callback, "Cannot reach the server. Is XAMPP (Apache) running?");
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
    }

    private static String readAll(InputStream stream) throws Exception {
        if (stream == null) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private static void postSuccess(Callback cb, JSONObject json) {
        mainHandler.post(() -> cb.onSuccess(json));
    }

    private static void postError(Callback cb, String message) {
        mainHandler.post(() -> cb.onError(message));
    }
}
