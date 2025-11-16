# AI Soil Tester Android App

A professional Android mobile app for soil testing and crop recommendations using Arduino HC-05 Bluetooth connectivity.

## Features

- **Complete Offline Functionality**: Works without internet connection
- **HC-05 Bluetooth Connectivity**: Connects to Arduino for real-time sensor data
- **Live Sensor Monitoring**: Displays soil moisture, temperature, and humidity in real-time
- **AI Crop Recommendations**: Offline rule-based AI suggests suitable crops based on sensor data
- **Professional UI**: Clean, modern green/white theme with large readable text
- **Share Results**: Share test results via messaging apps
- **Support Integration**: Built-in contact options and app sharing

## Technical Architecture

- **Platform**: Android (Java)
- **Minimum SDK**: API 21 (Android 5.0)
- **Target SDK**: API 34 (Android 14)
- **Architecture**: MVVM pattern
- **Bluetooth**: Classic Bluetooth with HC-05 SPP profile
- **AI Engine**: Rule-based crop recommendation system
- **Storage**: SharedPreferences for settings, SQLite for test history

## Build Instructions

1. Open project in Android Studio
2. Connect Android device or start emulator
3. Click Run or use Gradle commands:
   ```bash
   ./gradlew assembleDebug    # For debug APK
   ./gradlew assembleRelease   # For release APK
   ```

## Arduino Data Format

The app expects data in this format from Arduino:
```
MOISTURE:65,TEMP:28,HUMIDITY:72
```

## Crop Recommendation Logic

### Moisture-based Classification:
- **> 70%**: Water-loving crops (Rice, Sugarcane, Jute)
- **40-70%**: Moderate moisture crops (Wheat, Maize, Tomato)
- **< 40%**: Drought-tolerant crops (Groundnut, Cotton, Millet)

### Additional Factors:
- Temperature range consideration
- Humidity tolerance
- Growing period requirements
- Water needs classification

## Contact Information

- **Phone**: +91 8939290969
- **Email**: samdavishenry@gmail.com

## Permissions Required

- BLUETOOTH & BLUETOOTH_ADMIN - For device connectivity
- BLUETOOTH_SCAN & BLUETOOTH_CONNECT - Modern Android Bluetooth permissions
- ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION - Required for Bluetooth scanning
- WRITE_EXTERNAL_STORAGE & READ_EXTERNAL_STORAGE - For sharing functionality

## APK Output

After build, APK files will be located in:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

The app is ready for deployment and testing with Arduino HC-05 modules.