# APK Output Directory

This directory will contain the generated APK files after building:

## Expected Files:
- `app-debug.apk` - Debug APK (signed with debug keystore)
- `output.json` - Build metadata
- `apk-listing.txt` - APK file listing

## Build Commands:

**In Android Studio:**
1. Build → Build Bundle(s)/APK(s) → Build APK(s)
2. APKs will appear here

**Command Line:**
```bash
./gradlew assembleDebug
```

## After Build:

**Install Debug APK:**
```bash
adb install app-debug.apk
```

**Or manually:**
1. Copy app-debug.apk to Android device
2. Enable "Unknown Sources" in Android settings
3. Tap APK file to install

**Ready for:**
- Testing on Android 5.0+ devices
- HC-05 Bluetooth connectivity testing
- Arduino sensor data integration
- Offline AI crop recommendations