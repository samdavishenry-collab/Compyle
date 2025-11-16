# AI Soil Tester - Build and Deployment Instructions

## Prerequisites

### Required Software:
1. **Android Studio** (latest version recommended)
2. **Java Development Kit (JDK)** - Version 8 or higher
3. **Android SDK** - API 21 to API 34
4. **Android Build Tools** - Version 33.0.0 or higher

## Build Steps

### Method 1: Using Android Studio (Recommended)

1. **Import Project:**
   ```
   File → Open → Navigate to the AI Soil Tester project folder
   ```

2. **Sync Project:**
   ```
   Click "Sync Now" when prompted (gradle sync)
   ```

3. **Build APK:**
   ```
   Build → Build Bundle(s)/APK(s) → Build APK(s)
   ```

4. **Choose Build Variant:**
   - **Debug**: For testing (app-debug.apk)
   - **Release**: For production (app-release.apk)

### Method 2: Command Line (Gradle)

1. **Navigate to Project Directory:**
   ```bash
   cd /path/to/AI_Soil_Tester
   ```

2. **Build Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Build Release APK:**
   ```bash
   ./gradlew assembleRelease
   ```

## Output Locations

After successful build, APKs will be found at:

### Debug APK:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Release APK:
```
app/build/outputs/apk/release/app-release.apk
```

## Signing

### Debug APK:
- Automatically signed with debug keystore
- Ready for immediate testing

### Release APK:
- Can be signed with release keystore in Android Studio
- Upload ready for Google Play Store

## Testing Instructions

### Before Testing:
1. **Enable Developer Options** on Android device
2. **Enable USB Debugging** in Developer Options
3. **Install Debug APK** using:
   - `adb install app-debug.apk` OR
   - Copy APK to device and tap to install

### Testing Scenarios:
1. **Bluetooth Connection Test:**
   - Pair HC-05 with device
   - Test scanning and connection
   - Verify connection persistence

2. **Arduino Integration Test:**
   - Connect Arduino with HC-05
   - Send test data format: `MOISTURE:65,TEMP:28,HUMIDITY:72`
   - Verify real-time data display

3. **AI Recommendations Test:**
   - Test different sensor ranges
   - Verify moisture-based crop suggestions
   - Check temperature and humidity refinements

4. **Offline Functionality Test:**
   - Disable internet connection
   - Verify all features work offline

## Arduino Setup

### Required Components:
1. **Arduino Board** (Uno/Nano compatible)
2. **HC-05 Bluetooth Module**
3. **Soil Moisture Sensor**
4. **Temperature Sensor** (DHT11/DHT22)
5. **Humidity Sensor** (DHT11/DHT22)

### Arduino Code Requirements:
```cpp
void setup() {
  Serial.begin(9600); // Required baud rate
  // Initialize sensors
}

void loop() {
  float moisture = readSoilMoisture();
  float temp = readTemperature();
  float humidity = readHumidity();

  // Required format for app
  Serial.print("MOISTURE:");
  Serial.print(moisture);
  Serial.print(",TEMP:");
  Serial.print(temp);
  Serial.print(",HUMIDITY:");
  Serial.println(humidity);

  delay(2000); // Send data every 2 seconds
}
```

## Deployment

### Google Play Store:
1. **Create Developer Account** on Google Play Console
2. **Upload Release APK** to internal testing
3. **Fill Store Listing:**
   - App name: AI Soil Tester
   - Description: Professional soil testing app
   - Category: Education/Tools
4. **Submit for Review**

### Direct Distribution:
1. **Host APK** on website/server
2. **Enable "Unknown Sources"** on Android devices
3. **Download and Install** APK directly

## Troubleshooting

### Build Issues:
- **Sync Errors:** Check internet connection, rebuild project
- **SDK Issues:** Install required SDK versions in Android Studio
- **Gradle Issues:** Clean project and rebuild

### Runtime Issues:
- **Bluetooth Permissions:** Grant when prompted
- **HC-05 Connection:** Ensure device is paired and powered
- **Data Format:** Verify Arduino output format matches expected

### Performance:
- **Large Text:** All text is designed to be readable
- **Memory Usage:** App uses minimal memory
- **Battery Usage:** Bluetooth scanning is optimized

## Support

For deployment issues or questions:
- **Email:** samdavishenry@gmail.com
- **Phone:** +91 8939290969

---

The AI Soil Tester app is now ready for production deployment and testing with real Arduino hardware.