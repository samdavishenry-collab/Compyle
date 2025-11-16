package com.ai.soiltester.ui.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ai.soiltester.R;
import com.ai.soiltester.bluetooth.BluetoothManager;
import com.ai.soiltester.databinding.ActivityBluetoothBinding;

import java.util.ArrayList;
import java.util.List;

public class BluetoothConnectionActivity extends AppCompatActivity implements BluetoothManager.DataReceiver {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int ENABLE_BLUETOOTH_REQUEST = 101;

    private ActivityBluetoothBinding binding;
    private BluetoothManager bluetoothManager;
    private ArrayAdapter<BluetoothDevice> devicesAdapter;
    private List<BluetoothDevice> deviceList;
    private SharedPreferences sharedPreferences;
    private Handler handler;
    private boolean isScanning = false;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBluetoothBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize components
        sharedPreferences = getSharedPreferences("ai_soil_tester_prefs", MODE_PRIVATE);
        handler = new Handler(Looper.getMainLooper());
        deviceList = new ArrayList<>();
        devicesAdapter = new DeviceAdapter(this, deviceList);

        // Setup UI
        setupUI();

        // Check Bluetooth permissions and availability
        checkBluetoothPermissions();
    }

    private void setupUI() {
        // Set title
        binding.toolbar.setTitle(getString(R.string.bluetooth_connection));
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup list view
        binding.listViewDevices.setAdapter(devicesAdapter);

        // Setup click listeners
        binding.buttonScan.setOnClickListener(v -> startScanning());
        binding.buttonRetry.setOnClickListener(v -> startScanning());
        binding.buttonDisconnect.setOnClickListener(v -> disconnectDevice());
        binding.listViewDevices.setOnItemClickListener((parent, view, position, id) -> {
            BluetoothDevice device = devicesAdapter.getItem(position);
            if (device != null) {
                connectToDevice(device);
            }
        });
    }

    private void checkBluetoothPermissions() {
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            initializeBluetooth();
        }
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                PERMISSION_REQUEST_CODE);
    }

    private void initializeBluetooth() {
        bluetoothManager = new BluetoothManager(this, this);

        if (!bluetoothManager.isBluetoothSupported()) {
            showError("Bluetooth is not supported on this device");
            return;
        }

        if (!bluetoothManager.isBluetoothEnabled()) {
            bluetoothManager.requestEnableBluetooth(this);
        } else {
            loadConnectedDevice();
            if (!isConnected) {
                startScanning();
            }
        }
    }

    private void loadConnectedDevice() {
        String deviceAddress = sharedPreferences.getString("bluetooth_device_address", null);
        boolean wasConnected = sharedPreferences.getBoolean("bluetooth_connected", false);

        if (deviceAddress != null && wasConnected) {
            updateConnectionUI(true, "Previously connected device");
        }
    }

    private void startScanning() {
        if (bluetoothManager == null || isScanning) {
            return;
        }

        updateScanningUI(true);
        isScanning = true;

        // Clear existing devices
        deviceList.clear();
        devicesAdapter.notifyDataSetChanged();

        // Start scanning
        bluetoothManager.startDiscovery();

        // Auto-stop scanning after 30 seconds
        handler.postDelayed(() -> {
            if (isScanning) {
                stopScanning();
            }
        }, 30000);
    }

    private void stopScanning() {
        if (bluetoothManager != null && isScanning) {
            bluetoothManager.stopDiscovery();
            isScanning = false;
            updateScanningUI(false);
        }
    }

    private void connectToDevice(BluetoothDevice device) {
        if (bluetoothManager == null) {
            return;
        }

        updateConnectionUI(false, "Connecting to " + device.getName() + "...");
        binding.progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            boolean success = bluetoothManager.connectToDevice(device);
            handler.post(() -> {
                binding.progressBar.setVisibility(View.GONE);
                if (success) {
                    saveConnectionInfo(device);
                    updateConnectionUI(true, "Connected to " + device.getName());
                    isConnected = true;
                } else {
                    updateConnectionUI(false, "Connection failed");
                }
            });
        }).start();
    }

    private void disconnectDevice() {
        if (bluetoothManager != null && isConnected) {
            bluetoothManager.disconnect();
            clearConnectionInfo();
            updateConnectionUI(false, "Disconnected");
            isConnected = false;
        }
    }

    private void saveConnectionInfo(BluetoothDevice device) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("bluetooth_device_address", device.getAddress());
        editor.putString("bluetooth_device_name", device.getName());
        editor.putBoolean("bluetooth_connected", true);
        editor.apply();
    }

    private void clearConnectionInfo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("bluetooth_device_address");
        editor.remove("bluetooth_device_name");
        editor.putBoolean("bluetooth_connected", false);
        editor.apply();
    }

    private void updateScanningUI(boolean scanning) {
        if (scanning) {
            binding.textStatus.setText("Scanning for HC-05 devices...");
            binding.buttonScan.setVisibility(View.GONE);
            binding.buttonRetry.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.textStatus.setText("HC-05 devices found:");
            binding.buttonScan.setVisibility(View.VISIBLE);
            binding.buttonRetry.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void updateConnectionUI(boolean connected, String status) {
        binding.textStatus.setText(status);
        binding.buttonDisconnect.setVisibility(connected ? View.VISIBLE : View.GONE);
        binding.listViewDevices.setVisibility(connected ? View.GONE : View.VISIBLE);

        if (connected) {
            binding.textStatus.setTextColor(getResources().getColor(R.color.success, null));
            updateScanningUI(false);
        } else {
            binding.textStatus.setTextColor(getResources().getColor(R.color.text_secondary, null));
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onDataReceived(String data) {
        // Handle data if needed (not used in connection screen)
    }

    @Override
    public void onConnectionStateChanged(boolean connected) {
        handler.post(() -> {
            isConnected = connected;
            if (connected) {
                finish(); // Go back to home screen when connected
            }
        });
    }

    @Override
    public void onError(String error) {
        handler.post(() -> {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeBluetooth();
            } else {
                showError("Bluetooth permissions are required to use this feature");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENABLE_BLUETOOTH_REQUEST) {
            if (resultCode == RESULT_OK) {
                initializeBluetooth();
            } else {
                showError("Bluetooth must be enabled to connect to HC-05 device");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothManager != null && !isConnected && !isScanning) {
            startScanning();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothManager != null) {
            if (!isConnected) {
                bluetoothManager.stopDiscovery();
            }
            bluetoothManager.unregisterReceivers();
        }
        binding = null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Custom ArrayAdapter for Bluetooth devices
    private static class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {
        public DeviceAdapter(BluetoothConnectionActivity context, List<BluetoothDevice> objects) {
            super(context, R.layout.item_device, objects);
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = android.view.LayoutInflater.from(getContext()).inflate(R.layout.item_device, parent, false);
            }

            BluetoothDevice device = getItem(position);
            if (device != null) {
                android.widget.TextView nameText = view.findViewById(R.id.device_name);
                android.widget.TextView addressText = view.findViewById(R.id.device_address);
                android.widget.ImageView iconView = view.findViewById(R.id.device_icon);

                nameText.setText(device.getName() != null ? device.getName() : "Unknown Device");
                addressText.setText(device.getAddress());

                if (device.getName() != null && device.getName().contains("HC-05")) {
                    iconView.setImageResource(R.drawable.ic_hc05);
                    nameText.setTextColor(getContext().getResources().getColor(R.color.green_primary, null));
                } else {
                    iconView.setImageResource(R.drawable.ic_bluetooth);
                    nameText.setTextColor(getContext().getResources().getColor(R.color.text_secondary, null));
                }
            }

            return view;
        }
    }
}