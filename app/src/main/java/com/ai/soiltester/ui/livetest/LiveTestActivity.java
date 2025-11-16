package com.ai.soiltester.ui.livetest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ai.soiltester.R;
import com.ai.soiltester.ai.CropRecommendationEngine;
import com.ai.soiltester.bluetooth.BluetoothManager;
import com.ai.soiltester.databinding.ActivityLiveTestBinding;
import com.ai.soiltester.models.Crop;
import com.ai.soiltester.models.SensorReading;
import com.ai.soiltester.models.TestResult;
import com.ai.soiltester.ui.results.ResultsActivity;

import java.util.ArrayList;
import java.util.List;

public class LiveTestActivity extends AppCompatActivity implements BluetoothManager.DataReceiver {

    private static final int MAX_READINGS = 50; // Maximum readings to store
    private static final int SAVE_INTERVAL = 10; // Save data every 10 readings

    private ActivityLiveTestBinding binding;
    private BluetoothManager bluetoothManager;
    private SharedPreferences sharedPreferences;
    private ToneGenerator toneGenerator;
    private Handler handler;
    private CropRecommendationEngine cropEngine;

    private List<SensorReading> sensorReadings;
    private SensorReading currentReading;
    private TestResult currentTestResult;
    private boolean isTestRunning = false;
    private int readingCount = 0;
    private long testStartTime;

    // Animation for running indicator
    private AlphaAnimation blinkAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize components
        sharedPreferences = getSharedPreferences("ai_soil_tester_prefs", MODE_PRIVATE);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 50);
        handler = new Handler(Looper.getMainLooper());
        cropEngine = new CropRecommendationEngine();
        sensorReadings = new ArrayList<>();

        // Setup UI and animations
        setupUI();
        setupAnimations();

        // Initialize Bluetooth
        initializeBluetooth();
    }

    private void setupUI() {
        // Set title
        binding.toolbar.setTitle(getString(R.string.live_test_running));
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup click listeners
        binding.buttonStopTest.setOnClickListener(v -> showStopTestDialog());

        // Initialize display values
        updateSensorDisplay(null);

        // Start test automatically
        startTest();
    }

    private void setupAnimations() {
        // Blinking animation for "Test Running" text
        blinkAnimation = new AlphaAnimation(0.3f, 1.0f);
        blinkAnimation.setDuration(1000);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        blinkAnimation.setRepeatCount(Animation.INFINITE);
    }

    private void initializeBluetooth() {
        String deviceAddress = sharedPreferences.getString("bluetooth_device_address", null);

        if (deviceAddress == null) {
            showErrorAndFinish("No Bluetooth device connected. Please connect to HC-05 first.");
            return;
        }

        bluetoothManager = new BluetoothManager(this, this);

        if (!bluetoothManager.isBluetoothSupported()) {
            showErrorAndFinish("Bluetooth is not supported on this device");
            return;
        }

        if (!bluetoothManager.isBluetoothEnabled()) {
            showErrorAndFinish("Bluetooth is not enabled");
            return;
        }

        // Connect to saved device
        connectToSavedDevice(deviceAddress);
    }

    private void connectToSavedDevice(String deviceAddress) {
        updateStatus("Connecting to HC-05...");
        binding.progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            // Find paired HC-05 device by address
            for (android.bluetooth.BluetoothDevice device : bluetoothManager.getPairedHC05Devices()) {
                if (device.getAddress().equals(deviceAddress)) {
                    boolean connected = bluetoothManager.connectToDevice(device);
                    handler.post(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        if (connected) {
                            startTest();
                        } else {
                            showErrorAndFinish("Failed to connect to HC-05 device");
                        }
                    });
                    return;
                }
            }

            handler.post(() -> {
                binding.progressBar.setVisibility(View.GONE);
                showErrorAndFinish("HC-05 device not found. Please pair the device first.");
            });
        }).start();
    }

    private void startTest() {
        if (isTestRunning) {
            return;
        }

        isTestRunning = true;
        testStartTime = System.currentTimeMillis();
        readingCount = 0;
        sensorReadings.clear();

        // Update UI for test running
        updateStatus("Test Running... Connected to HC-05");
        binding.textRunningStatus.setText(R.string.test_running_connected);
        binding.textRunningStatus.startAnimation(blinkAnimation);
        binding.buttonStopTest.setEnabled(true);

        // Clear previous values
        updateSensorDisplay(null);
    }

    private void stopTest() {
        if (!isTestRunning) {
            return;
        }

        isTestRunning = false;

        // Stop animations
        binding.textRunningStatus.clearAnimation();

        // Generate final test result
        generateTestResult();

        // Navigate to results
        navigateToResults();
    }

    private void generateTestResult() {
        if (sensorReadings.isEmpty()) {
            return;
        }

        // Calculate averages from all readings
        float avgMoisture = 0;
        float avgTemperature = 0;
        float avgHumidity = 0;

        for (SensorReading reading : sensorReadings) {
            avgMoisture += reading.getMoisture();
            avgTemperature += reading.getTemperature();
            avgHumidity += reading.getHumidity();
        }

        int count = sensorReadings.size();
        avgMoisture /= count;
        avgTemperature /= count;
        avgHumidity /= count;

        // Create final reading
        SensorReading finalReading = new SensorReading(avgMoisture, avgTemperature, avgHumidity);

        // Get crop recommendations
        List<Crop> recommendations = cropEngine.getCropRecommendations(
                avgMoisture, avgTemperature, avgHumidity);

        // Create test result
        currentTestResult = new TestResult(finalReading, recommendations);
        currentTestResult.setTimestamp(testStartTime);
    }

    private void updateSensorDisplay(SensorReading reading) {
        if (reading == null) {
            binding.textMoistureValue.setText("--");
            binding.textTemperatureValue.setText("--");
            binding.textHumidityValue.setText("--");
            return;
        }

        binding.textMoistureValue.setText(String.format("%.1f%%", reading.getMoisture()));
        binding.textTemperatureValue.setText(String.format("%.1f°C", reading.getTemperature()));
        binding.textHumidityValue.setText(String.format("%.1f%%", reading.getHumidity()));

        // Update progress bars
        binding.progressBarMoisture.setProgress((int) reading.getMoisture());
        binding.progressBarTemperature.setProgress((int) Math.max(0, Math.min(100, (reading.getTemperature() + 20) * 100 / 80))); // -20 to 60 range
        binding.progressBarHumidity.setProgress((int) reading.getHumidity());
    }

    private void updateStatus(String status) {
        binding.textStatus.setText(status);
    }

    private void playDataReceivedSound() {
        if (toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 200);
        }
    }

    private void showStopTestDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Stop Test")
                .setMessage("Are you sure you want to stop the test and view results?")
                .setPositiveButton("Stop & View Results", (dialog, which) -> stopTest())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void navigateToResults() {
        if (currentTestResult != null) {
            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra("test_result", currentTestResult);
            startActivity(intent);
        }
        finish();
    }

    private void showErrorAndFinish(String message) {
        updateStatus(message);
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override
    public void onDataReceived(String data) {
        handler.post(() -> {
            // Parse Arduino data
            SensorReading reading = SensorReading.parseArduinoData(data);

            if (reading != null && reading.isValid()) {
                currentReading = reading;
                sensorReadings.add(reading);
                readingCount++;

                // Update display
                updateSensorDisplay(reading);

                // Play notification sound
                playDataReceivedSound();

                // Save data periodically
                if (readingCount % SAVE_INTERVAL == 0) {
                    // Could save to database here
                }

                // Limit stored readings
                if (sensorReadings.size() > MAX_READINGS) {
                    sensorReadings.remove(0);
                }
            }
        });
    }

    @Override
    public void onConnectionStateChanged(boolean connected) {
        handler.post(() -> {
            if (!connected && isTestRunning) {
                updateStatus("Connection lost! Please reconnect.");
                binding.textRunningStatus.clearAnimation();
                isTestRunning = false;
            }
        });
    }

    @Override
    public void onError(String error) {
        handler.post(() -> {
            updateStatus("Error: " + error);
            if (isTestRunning) {
                binding.textRunningStatus.clearAnimation();
                isTestRunning = false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop animations
        if (binding.textRunningStatus != null) {
            binding.textRunningStatus.clearAnimation();
        }

        // Release tone generator
        if (toneGenerator != null) {
            toneGenerator.release();
        }

        // Disconnect Bluetooth
        if (bluetoothManager != null) {
            bluetoothManager.disconnect();
            bluetoothManager.unregisterReceivers();
        }

        binding = null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isTestRunning) {
            showStopTestDialog();
        } else {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isTestRunning) {
            showStopTestDialog();
        } else {
            super.onBackPressed();
        }
    }
}