package com.ai.soiltester.models;

public class Crop {
    private String name;
    private String emoji;
    private String description;
    private int minDays;
    private int maxDays;
    private String waterNeed;
    private int minMoisture;
    private int maxMoisture;
    private int minTemp;
    private int maxTemp;
    private double suitabilityScore;
    private boolean temperatureSuitable;

    public Crop() {
    }

    public Crop(String name, String emoji, String description, int minDays, int maxDays,
                String waterNeed, int minMoisture, int maxMoisture, int minTemp, int maxTemp) {
        this.name = name;
        this.emoji = emoji;
        this.description = description;
        this.minDays = minDays;
        this.maxDays = maxDays;
        this.waterNeed = waterNeed;
        this.minMoisture = minMoisture;
        this.maxMoisture = maxMoisture;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.suitabilityScore = 0.0;
        this.temperatureSuitable = false;
    }

    // Getters
    public String getName() { return name; }
    public String getEmoji() { return emoji; }
    public String getDescription() { return description; }
    public int getMinDays() { return minDays; }
    public int getMaxDays() { return maxDays; }
    public String getWaterNeed() { return waterNeed; }
    public int getMinMoisture() { return minMoisture; }
    public int getMaxMoisture() { return maxMoisture; }
    public int getMinTemp() { return minTemp; }
    public int getMaxTemp() { return maxTemp; }
    public double getSuitabilityScore() { return suitabilityScore; }
    public boolean isTemperatureSuitable() { return temperatureSuitable; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public void setDescription(String description) { this.description = description; }
    public void setMinDays(int minDays) { this.minDays = minDays; }
    public void setMaxDays(int maxDays) { this.maxDays = maxDays; }
    public void setWaterNeed(String waterNeed) { this.waterNeed = waterNeed; }
    public void setMinMoisture(int minMoisture) { this.minMoisture = minMoisture; }
    public void setMaxMoisture(int maxMoisture) { this.maxMoisture = maxMoisture; }
    public void setMinTemp(int minTemp) { this.minTemp = minTemp; }
    public void setMaxTemp(int maxTemp) { this.maxTemp = maxTemp; }
    public void setSuitabilityScore(double suitabilityScore) { this.suitabilityScore = suitabilityScore; }
    public void setTemperatureSuitable(boolean temperatureSuitable) { this.temperatureSuitable = temperatureSuitable; }

    // Helper methods
    public String getGrowingPeriodText() {
        return minDays + "-" + maxDays + " days";
    }

    public String getMoistureRangeText() {
        return minMoisture + "-" + maxMoisture + "%";
    }

    public String getTemperatureRangeText() {
        return minTemp + "-" + maxTemp + "°C";
    }

    public String getSuitabilityText() {
        if (suitabilityScore >= 80) {
            return "Excellent";
        } else if (suitabilityScore >= 60) {
            return "Good";
        } else if (suitabilityScore >= 40) {
            return "Fair";
        } else {
            return "Poor";
        }
    }

    @Override
    public String toString() {
        return "Crop{" +
                "name='" + name + '\'' +
                ", emoji='" + emoji + '\'' +
                ", suitabilityScore=" + suitabilityScore +
                '}';
    }
}