
package com.example.pigs.network;

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

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String message);
    }

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

        request("register.php", data, callback);
    }

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

        request("login.php", data, callback);
    }

    private static void request(
            String endpoint,
            JSONObject data,
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
                        "Accept",
                        "application/json"
                );

                byte[] requestData =
                        data.toString().getBytes(StandardCharsets.UTF_8);

                try (OutputStream output = connection.getOutputStream()) {
                    output.write(requestData);
                    output.flush();
                }

                int responseCode = connection.getResponseCode();

                InputStream inputStream =
                        responseCode >= 200 && responseCode < 300
                                ? connection.getInputStream()
                                : connection.getErrorStream();

                if (inputStream == null) {
                    throw new Exception("No response from server.");
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

                JSONObject jsonResponse =
                        new JSONObject(response.toString());

                if (jsonResponse.optBoolean("success", false)) {

                    mainHandler.post(() ->
                            callback.onSuccess(jsonResponse)
                    );

                } else {

                    String message = jsonResponse.optString(
                            "message",
                            "Request failed."
                    );

                    mainHandler.post(() ->
                            callback.onError(message)
                    );
                }

            } catch (Exception e) {

                String errorMessage =
                        "Unable to connect to the server. "
                                + "Please check your internet connection and server.";

                mainHandler.post(() ->
                        callback.onError(errorMessage)
                );

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }
}
