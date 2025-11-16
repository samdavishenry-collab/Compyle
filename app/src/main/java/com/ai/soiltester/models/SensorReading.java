package com.ai.soiltester.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SensorReading {
    private float moisture;
    private float temperature;
    private float humidity;
    private long timestamp;
    private String deviceId;
    private String rawData;

    public SensorReading() {
        this.timestamp = System.currentTimeMillis();
    }

    public SensorReading(float moisture, float temperature, float humidity) {
        this.moisture = moisture;
        this.temperature = temperature;
        this.humidity = humidity;
        this.timestamp = System.currentTimeMillis();
    }

    public SensorReading(float moisture, float temperature, float humidity, String rawData) {
        this.moisture = moisture;
        this.temperature = temperature;
        this.humidity = humidity;
        this.rawData = rawData;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public float getMoisture() { return moisture; }
    public float getTemperature() { return temperature; }
    public float getHumidity() { return humidity; }
    public long getTimestamp() { return timestamp; }
    public String getDeviceId() { return deviceId; }
    public String getRawData() { return rawData; }

    // Setters
    public void setMoisture(float moisture) { this.moisture = moisture; }
    public void setTemperature(float temperature) { this.temperature = temperature; }
    public void setHumidity(float humidity) { this.humidity = humidity; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public void setRawData(String rawData) { this.rawData = rawData; }

    // Helper methods
    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getShortTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public boolean isValid() {
        return moisture >= 0 && moisture <= 100 &&
               temperature >= -20 && temperature <= 60 &&
               humidity >= 0 && humidity <= 100;
    }

    public String getMoistureText() {
        return String.format(Locale.getDefault(), "%.1f%%", moisture);
    }

    public String getTemperatureText() {
        return String.format(Locale.getDefault(), "%.1f°C", temperature);
    }

    public String getHumidityText() {
        return String.format(Locale.getDefault(), "%.1f%%", humidity);
    }

    public String getSummaryText() {
        return String.format("M: %.1f%% | T: %.1f°C | H: %.1f%%", moisture, temperature, humidity);
    }

    // Factory method to parse Arduino data
    public static SensorReading parseArduinoData(String rawData) {
        if (rawData == null || rawData.trim().isEmpty()) {
            return null;
        }

        try {
            // Expected format: "MOISTURE:65,TEMP:28,HUMIDITY:72"
            String[] parts = rawData.split(",");
            float moisture = 0;
            float temperature = 0;
            float humidity = 0;

            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("MOISTURE:")) {
                    moisture = Float.parseFloat(part.substring(9));
                } else if (part.startsWith("TEMP:")) {
                    temperature = Float.parseFloat(part.substring(5));
                } else if (part.startsWith("HUMIDITY:")) {
                    humidity = Float.parseFloat(part.substring(9));
                }
            }

            SensorReading reading = new SensorReading(moisture, temperature, humidity, rawData);

            if (reading.isValid()) {
                return reading;
            } else {
                return null;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "SensorReading{" +
                "moisture=" + moisture +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", timestamp=" + timestamp +
                ", summary='" + getSummaryText() + '\'' +
                '}';
    }
}