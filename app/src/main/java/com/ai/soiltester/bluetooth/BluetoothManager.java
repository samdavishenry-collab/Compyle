package com.ai.soiltester.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager {
    private static final String TAG = "BluetoothManager";

    // HC-05 SPP UUID
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Connection constants
    private static final int CONNECTION_TIMEOUT = 10000; // 10 seconds
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY = 2000; // 2 seconds

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private ConnectedThread connectedThread;
    private DataReceiver dataReceiver;

    private boolean isScanning = false;
    private boolean isConnected = false;
    private List<BluetoothDevice> discoveredDevices = new ArrayList<>();

    public interface DataReceiver {
        void onDataReceived(String data);
        void onConnectionStateChanged(boolean connected);
        void onError(String error);
    }

    public BluetoothManager(Context context, DataReceiver dataReceiver) {
        this.context = context;
        this.dataReceiver = dataReceiver;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isBluetoothSupported() {
        return bluetoothAdapter != null;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public void requestEnableBluetooth(Activity activity) {
        if (!isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 1);
        }
    }

    public boolean hasPermissions() {
        int bluetoothScanPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN);
        int bluetoothConnectPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT);
        int locationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

        return bluetoothScanPermission == PackageManager.PERMISSION_GRANTED &&
               bluetoothConnectPermission == PackageManager.PERMISSION_GRANTED &&
               locationPermission == PackageManager.PERMISSION_GRANTED;
    }

    public List<BluetoothDevice> getPairedHC05Devices() {
        List<BluetoothDevice> hc05Devices = new ArrayList<>();

        if (bluetoothAdapter == null) {
            return hc05Devices;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName() != null && device.getName().contains("HC-05")) {
                hc05Devices.add(device);
            }
        }

        return hc05Devices;
    }

    public void startDiscovery() {
        if (bluetoothAdapter == null || isScanning) {
            return;
        }

        // Clear previously discovered devices
        discoveredDevices.clear();

        // Register for device discovery broadcasts
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(discoveryReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(discoveryReceiver, filter);

        isScanning = bluetoothAdapter.startDiscovery();

        if (!isScanning) {
            dataReceiver.onError("Failed to start Bluetooth discovery");
        }
    }

    public void stopDiscovery() {
        if (bluetoothAdapter != null && isScanning) {
            bluetoothAdapter.cancelDiscovery();
            isScanning = false;
        }
    }

    public List<BluetoothDevice> getDiscoveredDevices() {
        return new ArrayList<>(discoveredDevices);
    }

    public List<BluetoothDevice> getDiscoveredHC05Devices() {
        List<BluetoothDevice> hc05Devices = new ArrayList<>();

        for (BluetoothDevice device : discoveredDevices) {
            if (device.getName() != null && device.getName().contains("HC-05")) {
                hc05Devices.add(device);
            }
        }

        return hc05Devices;
    }

    public boolean connectToDevice(BluetoothDevice device) {
        return connectToDeviceWithRetry(device, 0);
    }

    private boolean connectToDeviceWithRetry(BluetoothDevice device, int attempt) {
        if (attempt >= MAX_RETRY_ATTEMPTS) {
            dataReceiver.onError("Failed to connect after " + MAX_RETRY_ATTEMPTS + " attempts");
            return false;
        }

        try {
            // Cancel any ongoing discovery
            if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }

            // Create socket
            bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);

            // Connect with timeout
            bluetoothSocket.connect();

            // Update connection state
            isConnected = true;
            dataReceiver.onConnectionStateChanged(true);

            // Start data listening thread
            connectedThread = new ConnectedThread(bluetoothSocket);
            connectedThread.start();

            Log.d(TAG, "Connected to " + device.getName());
            return true;

        } catch (IOException e) {
            Log.e(TAG, "Connection attempt " + (attempt + 1) + " failed", e);

            // Close socket if connection failed
            if (bluetoothSocket != null) {
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Failed to close socket", closeException);
                }
                bluetoothSocket = null;
            }

            // Retry connection
            if (attempt < MAX_RETRY_ATTEMPTS - 1) {
                try {
                    Thread.sleep(RETRY_DELAY);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                return connectToDeviceWithRetry(device, attempt + 1);
            }

            dataReceiver.onError("Connection failed: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        isConnected = false;

        // Stop data listening thread
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Close socket
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing socket", e);
            }
            bluetoothSocket = null;
        }

        // Stop discovery
        stopDiscovery();

        dataReceiver.onConnectionStateChanged(false);
    }

    public boolean isConnected() {
        return isConnected && bluetoothSocket != null && bluetoothSocket.isConnected();
    }

    public void sendData(String data) {
        if (connectedThread != null) {
            connectedThread.write(data.getBytes());
        }
    }

    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    discoveredDevices.add(device);
                    Log.d(TAG, "Found device: " + device.getName() + " - " + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                isScanning = false;
                Log.d(TAG, "Discovery finished. Found " + discoveredDevices.size() + " devices");
            }
        }
    };

    public void unregisterReceivers() {
        try {
            context.unregisterReceiver(discoveryReceiver);
        } catch (IllegalArgumentException e) {
            // Receiver not registered
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private final Handler handler;

        public ConnectedThread(BluetoothSocket socket) throws IOException {
            this.socket = socket;
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            this.handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        String receivedData = new String(buffer, 0, bytes).trim();
                        handler.post(() -> dataReceiver.onDataReceived(receivedData));
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Connection lost", e);
                    handler.post(() -> {
                        isConnected = false;
                        dataReceiver.onConnectionStateChanged(false);
                    });
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Error writing to output stream", e);
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing socket in thread", e);
            }
        }
    }
}