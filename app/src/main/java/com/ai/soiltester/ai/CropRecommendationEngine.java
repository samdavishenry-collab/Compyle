package com.ai.soiltester.ai;

import android.content.Context;
import android.util.Log;

import com.ai.soiltester.models.Crop;
import com.ai.soiltester.models.SensorReading;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CropRecommendationEngine {
    private static final String TAG = "CropRecommendationEngine";

    public List<Crop> getCropRecommendations(float moisture, float temperature, float humidity) {
        Log.d(TAG, String.format("Getting recommendations for moisture: %.1f%%, temp: %.1f°C, humidity: %.1f%%",
                moisture, temperature, humidity));

        List<Crop> candidates = new ArrayList<>();

        // Primary classification by moisture
        if (moisture > 70) {
            // Water-loving crops
            candidates.add(new Crop("Rice", "🌾", "Thrives in flooded conditions", 120, 150, "High", 70, 100, 20, 35));
            candidates.add(new Crop("Sugarcane", "🌿", "Requires abundant water", 300, 365, "High", 70, 100, 20, 35));
            candidates.add(new Crop("Jute", "🌱", "Best in high moisture soil", 100, 120, "High", 70, 100, 20, 35));
        } else if (moisture >= 40 && moisture <= 70) {
            // Moderate moisture crops
            candidates.add(new Crop("Wheat", "🌾", "Ideal moisture range", 120, 150, "Medium", 40, 70, 15, 25));
            candidates.add(new Crop("Maize", "🌽", "Versatile moisture needs", 60, 90, "Medium", 40, 70, 18, 30));
            candidates.add(new Crop("Tomato", "🍅", "Balanced watering required", 70, 80, "Medium", 40, 70, 18, 28));
        } else {
            // Drought-tolerant crops
            candidates.add(new Crop("Groundnut", "🥜", "Low water requirement", 90, 110, "Low", 20, 40, 25, 35));
            candidates.add(new Crop("Cotton", "☁️", "Drought resistant", 150, 180, "Low", 20, 40, 20, 35));
            candidates.add(new Crop("Millet", "🌾", "Excellent drought tolerance", 60, 90, "Low", 20, 40, 15, 30));
        }

        // Filter by temperature suitability
        List<Crop> temperatureFiltered = candidates.stream()
                .filter(crop -> temperature >= crop.getMinTemp() && temperature <= crop.getMaxTemp())
                .collect(Collectors.toList());

        // If no crops match temperature, use all candidates but mark as temperature warning
        List<Crop> finalCandidates = temperatureFiltered.isEmpty() ? candidates : temperatureFiltered;

        // Sort by suitability score
        List<Crop> sortedCrops = finalCandidates.stream()
                .sorted(Comparator.comparingDouble(crop -> calculateSuitabilityScore(crop, moisture, temperature, humidity)))
                .collect(Collectors.toList());

        // Return top 3 crops
        List<Crop> topCrops = sortedCrops.stream()
                .limit(3)
                .collect(Collectors.toList());

        // Add suitability information
        for (Crop crop : topCrops) {
            crop.setSuitabilityScore(calculateSuitabilityScore(crop, moisture, temperature, humidity));
            crop.setTemperatureSuitable(temperature >= crop.getMinTemp() && temperature <= crop.getMaxTemp());
        }

        return topCrops;
    }

    private double calculateSuitabilityScore(Crop crop, float moisture, float temperature, float humidity) {
        double score = 100.0;

        // Moisture suitability (40% weight)
        double moistureScore = calculateMoistureScore(crop, moisture);
        score -= (100 - moistureScore) * 0.4;

        // Temperature suitability (35% weight)
        double temperatureScore = calculateTemperatureScore(crop, temperature);
        score -= (100 - temperatureScore) * 0.35;

        // Humidity suitability (25% weight)
        double humidityScore = calculateHumidityScore(crop, humidity);
        score -= (100 - humidityScore) * 0.25;

        return Math.max(0, Math.min(100, score));
    }

    private double calculateMoistureScore(Crop crop, float moisture) {
        float optimalMoisture = (crop.getMinMoisture() + crop.getMaxMoisture()) / 2.0f;
        double range = crop.getMaxMoisture() - crop.getMinMoisture();
        double deviation = Math.abs(moisture - optimalMoisture);

        if (deviation <= range / 2.0) {
            return 100.0;
        } else {
            return Math.max(0, 100.0 - (deviation - range / 2.0) * 2.0);
        }
    }

    private double calculateTemperatureScore(Crop crop, float temperature) {
        if (temperature >= crop.getMinTemp() && temperature <= crop.getMaxTemp()) {
            return 100.0;
        } else {
            double minDeviation = Math.abs(temperature - crop.getMinTemp());
            double maxDeviation = Math.abs(temperature - crop.getMaxTemp());
            double deviation = Math.min(minDeviation, maxDeviation);
            return Math.max(0, 100.0 - deviation * 5.0);
        }
    }

    private double calculateHumidityScore(Crop crop, float humidity) {
        // Most crops prefer 40-70% humidity
        if (humidity >= 40 && humidity <= 70) {
            return 100.0;
        } else if (humidity < 40) {
            return Math.max(20, 60.0 - (40 - humidity));
        } else {
            return Math.max(20, 60.0 - (humidity - 70));
        }
    }

    public String getMoistureCategory(float moisture) {
        if (moisture > 70) {
            return "High Moisture - Water-loving crops";
        } else if (moisture >= 40 && moisture <= 70) {
            return "Moderate Moisture - Balanced crops";
        } else {
            return "Low Moisture - Drought-tolerant crops";
        }
    }

    public String getTemperatureCategory(float temperature) {
        if (temperature < 15) {
            return "Cool - Cold-tolerant crops preferred";
        } else if (temperature <= 35) {
            return "Optimal - Most crops thrive";
        } else {
            return "Hot - Heat-tolerant crops preferred";
        }
    }

    public String getHumidityCategory(float humidity) {
        if (humidity < 40) {
            return "Dry - Drought-resistant varieties";
        } else if (humidity <= 70) {
            return "Optimal - Ideal growing conditions";
        } else {
            return "Humid - Disease-resistant varieties";
        }
    }
}