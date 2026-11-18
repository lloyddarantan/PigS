package com.example.pigs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pigs.adapter.ThermalPigAdapter;
import com.example.pigs.model.Pig;
import com.example.pigs.viewmodel.DashboardViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ThermalCamActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final String TAG = "ThermalCamActivity";

    private ImageView btnBack;
    private PreviewView previewViewThermalFeed;
    private DetectionOverlayView detectionOverlayView;
    private MaterialButton btnDetectPig;
    private RoboflowApiClient roboflowApiClient;

    private RecyclerView recyclerViewPigs;
    private ThermalPigAdapter pigAdapter;
    private DashboardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermalcam);

        roboflowApiClient = new RoboflowApiClient();

        btnBack = findViewById(R.id.btnBack);
        previewViewThermalFeed = findViewById(R.id.imgThermalFeed);
        detectionOverlayView = findViewById(R.id.detectionOverlayView);
        btnDetectPig = findViewById(R.id.btnDetectPig);

        recyclerViewPigs = findViewById(R.id.recyclerViewPigs);
        if (recyclerViewPigs != null) {
            recyclerViewPigs.setLayoutManager(new LinearLayoutManager(this));
            pigAdapter = new ThermalPigAdapter();
            recyclerViewPigs.setAdapter(pigAdapter);
        }

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        viewModel.getPigs().observe(this, pigs -> {
            if (pigAdapter != null) {
                if (pigs == null || pigs.isEmpty()) {
                    List<Pig> defaultPigs = new ArrayList<>();
                    defaultPigs.add(new Pig(3, "003", "1", "40.9°C", "Possible ASF symptom - flag for vet", "A-0034", "♀", "14"));
                    defaultPigs.add(new Pig(11, "011", "2", "39.5°C", "Monitor closely • elevated", "A-0112", "♂", "9"));
                    defaultPigs.add(new Pig(1, "001", "1", "38.4°C", "Normal range • Healthy", "A-0011", "♀", "22"));
                    defaultPigs.add(new Pig(2, "002", "1", "38.7°C", "Normal range • Healthy", "A-0022", "♀", "18"));
                    defaultPigs.add(new Pig(4, "004", "1", "38.3°C", "Normal range • Healthy", "A-5432", "♂", "11"));
                    defaultPigs.add(new Pig(8, "008", "1", "38.5°C", "Normal range • Healthy", "A-0088", "♀", "16"));
                    pigAdapter.setPigs(defaultPigs);
                } else {
                    pigAdapter.setPigs(pigs);
                }
            }
        });

        // Back button navigation
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnDetectPig != null) {
            btnDetectPig.setOnClickListener(v -> runPigDetectionFromLiveCamera());
        }

        checkCameraPermissionAndStart();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.temp_id);
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.pig_dash) {
                    Intent intent = new Intent(ThermalCamActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.temp_id) {
                    return true;
                } else if (id == R.id.tutorial_id) {
                    Intent intent = new Intent(ThermalCamActivity.this, TutorialActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.support_id) {
                    Intent intent = new Intent(ThermalCamActivity.this, TechnicalSupportActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Capture the current frame from CameraX PreviewView and analyze it with Roboflow.
     */
    public void runPigDetectionFromLiveCamera() {
        if (previewViewThermalFeed == null) return;
        Bitmap currentFrame = previewViewThermalFeed.getBitmap();
        if (currentFrame == null) {
            Toast.makeText(this, "Camera frame not ready yet. Please ensure camera is active.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (detectionOverlayView != null) {
            detectionOverlayView.clear();
        }

        if (btnDetectPig != null) {
            btnDetectPig.setEnabled(false);
            btnDetectPig.setText(R.string.analyzing);
        }

        Toast.makeText(this, "Capturing live frame for Roboflow AI pig detection...", Toast.LENGTH_SHORT).show();

        roboflowApiClient.detectPigsFromBitmap(currentFrame, new RoboflowApiClient.RoboflowCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                if (btnDetectPig != null) {
                    btnDetectPig.setEnabled(true);
                    btnDetectPig.setText(R.string.detect_count_pigs);
                }

                Log.d(TAG, "Live Detection Result: " + jsonResponse);

                List<DetectionOverlayView.BoundingBox> boxes = parseBoundingBoxes(jsonResponse);
                if (detectionOverlayView != null) {
                    detectionOverlayView.setResults(boxes, currentFrame.getWidth(), currentFrame.getHeight());
                }

                showResultDialog(jsonResponse, boxes.size());
            }

            @Override
            public void onError(Exception e) {
                if (btnDetectPig != null) {
                    btnDetectPig.setEnabled(true);
                    btnDetectPig.setText(R.string.detect_count_pigs);
                }

                Log.e(TAG, "Live Detection Failed", e);
                Toast.makeText(ThermalCamActivity.this, "Detection Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showResultDialog(String rawJsonResponse, int boxCount) {
        String resultSummary = parseRoboflowSummary(rawJsonResponse);
        if (boxCount > 0) {
            resultSummary += "\n\nBounding boxes overlay updated with " + boxCount + " pig detection(s).";
        }

        new AlertDialog.Builder(this)
                .setTitle("Pig Detection & Count Result")
                .setMessage(resultSummary)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private List<DetectionOverlayView.BoundingBox> parseBoundingBoxes(String jsonResponse) {
        List<DetectionOverlayView.BoundingBox> boxes = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray predictions = null;

            if (root.has("predictions")) {
                Object obj = root.get("predictions");
                if (obj instanceof JSONArray) {
                    predictions = (JSONArray) obj;
                } else if (obj instanceof JSONObject) {
                    JSONObject predsObj = (JSONObject) obj;
                    if (predsObj.has("predictions")) {
                        predictions = predsObj.getJSONArray("predictions");
                    }
                }
            } else if (root.has("outputs")) {
                JSONArray outputs = root.getJSONArray("outputs");
                if (outputs.length() > 0) {
                    JSONObject firstOutput = outputs.getJSONObject(0);
                    if (firstOutput.has("predictions")) {
                        Object predsObj = firstOutput.get("predictions");
                        if (predsObj instanceof JSONArray) {
                            predictions = (JSONArray) predsObj;
                        } else if (predsObj instanceof JSONObject) {
                            JSONObject innerObj = (JSONObject) predsObj;
                            if (innerObj.has("predictions")) {
                                predictions = innerObj.getJSONArray("predictions");
                            }
                        }
                    }
                }
            }

            if (predictions != null) {
                for (int i = 0; i < predictions.length(); i++) {
                    JSONObject pred = predictions.getJSONObject(i);

                    float left, top, right, bottom;

                    if (pred.has("x") && pred.has("y") && pred.has("width") && pred.has("height")) {
                        float cx = (float) pred.getDouble("x");
                        float cy = (float) pred.getDouble("y");
                        float w = (float) pred.getDouble("width");
                        float h = (float) pred.getDouble("height");
                        left = cx - (w / 2f);
                        top = cy - (h / 2f);
                        right = cx + (w / 2f);
                        bottom = cy + (h / 2f);
                    } else if (pred.has("xmin") && pred.has("ymin") && pred.has("xmax") && pred.has("ymax")) {
                        left = (float) pred.getDouble("xmin");
                        top = (float) pred.getDouble("ymin");
                        right = (float) pred.getDouble("xmax");
                        bottom = (float) pred.getDouble("ymax");
                    } else {
                        continue;
                    }

                    String label = "pig";
                    if (pred.has("class")) {
                        label = pred.getString("class");
                    } else if (pred.has("label")) {
                        label = pred.getString("label");
                    }

                    float confidence = 1.0f;
                    if (pred.has("confidence")) {
                        confidence = (float) pred.getDouble("confidence");
                    } else if (pred.has("score")) {
                        confidence = (float) pred.getDouble("score");
                    }

                    boxes.add(new DetectionOverlayView.BoundingBox(new RectF(left, top, right, bottom), label, confidence));
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error parsing bounding boxes from Roboflow response", e);
        }
        return boxes;
    }

    private String parseRoboflowSummary(String jsonResponse) {
        try {
            JSONObject root = new JSONObject(jsonResponse);
            int detectedCount = -1;

            if (root.has("count")) {
                detectedCount = root.getInt("count");
            } else if (root.has("outputs")) {
                JSONArray outputs = root.getJSONArray("outputs");
                if (outputs.length() > 0) {
                    JSONObject firstOutput = outputs.getJSONObject(0);
                    if (firstOutput.has("count")) {
                        detectedCount = firstOutput.getInt("count");
                    } else if (firstOutput.has("predictions")) {
                        JSONObject predsObj = firstOutput.getJSONObject("predictions");
                        if (predsObj.has("predictions")) {
                            detectedCount = predsObj.getJSONArray("predictions").length();
                        }
                    }
                }
            }

            if (detectedCount >= 0) {
                return "Pigs Detected: " + detectedCount + "\n\nFull Response logged to Logcat.";
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not parse count from Roboflow response", e);
        }

        return "Response received successfully.\n\n" + jsonResponse;
    }

    private void checkCameraPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                if (previewViewThermalFeed != null) {
                    preview.setSurfaceProvider(previewViewThermalFeed.getSurfaceProvider());
                }

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera preview", e);
                Toast.makeText(this, "Failed to start camera feed", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to view live feed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}