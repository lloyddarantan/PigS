
package com.example.pigs.network;

import android.content.Context;
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

public final class ProfileApi {

    private static final String BASE_URL =
            "http://10.0.2.2/pigsensor_api/";

    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private static final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    private ProfileApi() {}

    public interface Callback {
        void success(JSONObject response);
        void error(String message);
    }

    public static void post(
            Context context,
            String endpoint,
            JSONObject data,
            Callback callback
    ) {

        String token = SessionManager.token(context);

        if (token.isEmpty()) {
            mainHandler.post(() ->
                    callback.error("Please log in again.")
            );
            return;
        }

        executor.execute(() -> {

            HttpURLConnection connection = null;

            try {
                URL url = new URL(BASE_URL + endpoint);

                connection =
                        (HttpURLConnection) url.openConnection();

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

                connection.setRequestProperty(
                        "Authorization",
                        "Bearer " + token
                );

                byte[] body = data.toString()
                        .getBytes(StandardCharsets.UTF_8);

                try (OutputStream output =
                             connection.getOutputStream()) {
                    output.write(body);
                    output.flush();
                }

                int status = connection.getResponseCode();

                InputStream stream =
                        status >= 200 && status < 300
                                ? connection.getInputStream()
                                : connection.getErrorStream();

                if (stream == null) {
                    throw new Exception("Empty server response.");
                }

                StringBuilder responseText =
                        new StringBuilder();

                try (BufferedReader reader =
                             new BufferedReader(
                                     new InputStreamReader(
                                             stream,
                                             StandardCharsets.UTF_8
                                     )
                             )) {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        responseText.append(line);
                    }
                }

                JSONObject response = new JSONObject(
                        responseText.toString()
                );

                if (response.optBoolean("success", false)) {

                    mainHandler.post(() ->
                            callback.success(response)
                    );

                } else {

                    String message = response.optString(
                            "message",
                            "Request failed."
                    );

                    mainHandler.post(() ->
                            callback.error(message)
                    );
                }

            } catch (Exception e) {

                mainHandler.post(() ->
                        callback.error(
                                "Could not connect to the PHP API."
                        )
                );

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }
}
