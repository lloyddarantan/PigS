package com.example.pigs;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoboflowApiClient {

    private static final String TAG = "RoboflowApiClient";
    private static final String API_URL = "https://serverless.roboflow.com/usls/workflows/pig-detection-and-count-1794525668993";
    private static final String API_KEY = "CHJfJzqrE6tlSb28KCb4";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface RoboflowCallback {
        void onSuccess(String jsonResponse);
        void onError(Exception e);
    }

    /**
     * Send an image URL to the Roboflow Pig Detection & Count Workflow.
     */
    public void detectPigsFromUrl(String imageUrl, RoboflowCallback callback) {
        executorService.execute(() -> {
            try {
                JSONObject imageObj = new JSONObject();
                imageObj.put("type", "url");
                imageObj.put("value", imageUrl);

                JSONObject inputsObj = new JSONObject();
                inputsObj.put("image", imageObj);

                JSONObject requestBody = new JSONObject();
                requestBody.put("inputs", inputsObj);

                sendPostRequest(requestBody.toString(), callback);
            } catch (Exception e) {
                notifyError(callback, e);
            }
        });
    }

    /**
     * Send a local Bitmap image (encoded to Base64) to the Roboflow Workflow.
     */
    public void detectPigsFromBitmap(Bitmap bitmap, RoboflowCallback callback) {
        executorService.execute(() -> {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                byte[] imageBytes = outputStream.toByteArray();
                String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

                JSONObject imageObj = new JSONObject();
                imageObj.put("type", "base64");
                imageObj.put("value", base64Image);

                JSONObject inputsObj = new JSONObject();
                inputsObj.put("image", imageObj);

                JSONObject requestBody = new JSONObject();
                requestBody.put("inputs", inputsObj);

                sendPostRequest(requestBody.toString(), callback);
            } catch (Exception e) {
                notifyError(callback, e);
            }
        });
    }

    private void sendPostRequest(String jsonPayload, RoboflowCallback callback) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(API_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            InputStream is = (responseCode >= 200 && responseCode < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
            }

            if (responseCode >= 200 && responseCode < 300) {
                String result = response.toString();
                Log.d(TAG, "Roboflow Response: " + result);
                notifySuccess(callback, result);
            } else {
                Exception error = new Exception("HTTP " + responseCode + ": " + response.toString());
                Log.e(TAG, "Roboflow API Error", error);
                notifyError(callback, error);
            }

        } catch (Exception e) {
            Log.e(TAG, "Network Error", e);
            notifyError(callback, e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void notifySuccess(RoboflowCallback callback, String result) {
        if (callback != null) {
            mainHandler.post(() -> callback.onSuccess(result));
        }
    }

    private void notifyError(RoboflowCallback callback, Exception e) {
        if (callback != null) {
            mainHandler.post(() -> callback.onError(e));
        }
    }
}