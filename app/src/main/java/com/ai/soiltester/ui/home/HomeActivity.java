package com.ai.soiltester.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ai.soiltester.R;
import com.ai.soiltester.bluetooth.BluetoothManager;
import com.ai.soiltester.databinding.ActivityHomeBinding;
import com.ai.soiltester.ui.bluetooth.BluetoothConnectionActivity;
import com.ai.soiltester.ui.livetest.LiveTestActivity;
import com.ai.soiltester.ui.results.ResultsActivity;
import com.ai.soiltester.ui.support.SupportActivity;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private SharedPreferences sharedPreferences;
    private ToneGenerator toneGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize preferences and tone generator
        sharedPreferences = getSharedPreferences("ai_soil_tester_prefs", MODE_PRIVATE);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 50);

        // Initialize UI
        initUI();

        // Set up click listeners
        setupClickListeners();
    }

    private void initUI() {
        // Set title and subtitle
        binding.textTitle.setText(R.string.app_title);
        binding.textSubtitle.setText(R.string.app_subtitle);
        binding.textVersion.setText(R.string.version_info);

        // Check if Bluetooth device is connected
        String deviceAddress = sharedPreferences.getString("bluetooth_device_address", null);
        if (deviceAddress != null) {
            binding.buttonConnectBluetooth.setText("Connected to HC-05");
            binding.buttonConnectBluetooth.setBackgroundColor(getResources().getColor(R.color.success, null));
        }
    }

    private void setupClickListeners() {
        // Connect Bluetooth button
        binding.buttonConnectBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButtonClickSound();
                startActivity(new Intent(HomeActivity.this, BluetoothConnectionActivity.class));
            }
        });

        // Start Test button
        binding.buttonStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButtonClickSound();
                checkBluetoothAndStartTest();
            }
        });

        // View Results button
        binding.buttonViewResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButtonClickSound();
                startActivity(new Intent(HomeActivity.this, ResultsActivity.class));
            }
        });

        // Support & Share button
        binding.buttonSupportShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButtonClickSound();
                startActivity(new Intent(HomeActivity.this, SupportActivity.class));
            }
        });
    }

    private void checkBluetoothAndStartTest() {
        String deviceAddress = sharedPreferences.getString("bluetooth_device_address", null);
        boolean isConnected = sharedPreferences.getBoolean("bluetooth_connected", false);

        if (deviceAddress == null || !isConnected) {
            Toast.makeText(this, "Please connect to Bluetooth device first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, BluetoothConnectionActivity.class));
        } else {
            startActivity(new Intent(HomeActivity.this, LiveTestActivity.class));
        }
    }

    private void playButtonClickSound() {
        if (toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update UI when returning from other activities
        updateConnectionStatus();
    }

    private void updateConnectionStatus() {
        boolean isConnected = sharedPreferences.getBoolean("bluetooth_connected", false);
        String deviceName = sharedPreferences.getString("bluetooth_device_name", "");

        if (isConnected && !deviceName.isEmpty()) {
            binding.buttonConnectBluetooth.setText("Connected to " + deviceName);
            binding.buttonConnectBluetooth.setBackgroundColor(getResources().getColor(R.color.success, null));
        } else {
            binding.buttonConnectBluetooth.setText(R.string.connect_bluetooth);
            binding.buttonConnectBluetooth.setBackgroundColor(getResources().getColor(R.color/green_primary, null));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toneGenerator != null) {
            toneGenerator.release();
        }
        binding = null;
    }
}