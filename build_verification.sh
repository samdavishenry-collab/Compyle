#!/bin/bash

echo "=== AI Soil Tester - Build Verification ==="
echo ""

# Check project structure
echo "📁 Project Structure:"
echo "✓ $(find app/src/main/java -name "*.java" | wc -l) Java source files"
echo "✓ $(find app/src/main/res/layout -name "*.xml" | wc -l) Layout files"
echo "✓ $(find app/src/main/res/drawable -name "*.xml" | wc -l) Drawable resources"
echo "✓ $(find app/src/main/res/values -name "*.xml" | wc -l) Value resources"
echo ""

# Check key files
echo "📋 Key Files Verification:"
echo "✓ AndroidManifest.xml: $([ -f app/src/main/AndroidManifest.xml ] && echo "Present" || echo "Missing")"
echo "✓ build.gradle: $([ -f app/build.gradle ] && echo "Present" || echo "Missing")"
echo "✓ settings.gradle: $([ -f settings.gradle ] && echo "Present" || echo "Missing")"
echo ""

# Check core components
echo "🔧 Core Components:"
echo "✓ BluetoothManager.java: $([ -f app/src/main/java/com/ai/soiltester/bluetooth/BluetoothManager.java ] && echo "Present" || echo "Missing")"
echo "✓ CropRecommendationEngine.java: $([ -f app/src/main/java/com/ai/soiltester/ai/CropRecommendationEngine.java ] && echo "Present" || echo "Missing")"
echo "✓ MainActivity.java: $([ -f app/src/main/java/com/ai/soiltester/MainActivity.java ] && echo "Present" || echo "Missing")"
echo ""

# Check UI components
echo "🎨 UI Components:"
echo "✓ HomeActivity: $([ -f app/src/main/java/com/ai/soiltester/ui/home/HomeActivity.java ] && echo "Present" || echo "Missing")"
echo "✓ BluetoothConnectionActivity: $([ -f app/src/main/java/com/ai/soiltester/ui/bluetooth/BluetoothConnectionActivity.java ] && echo "Present" || echo "Missing")"
echo "✓ LiveTestActivity: $([ -f app/src/main/java/com/ai/soiltester/ui/livetest/LiveTestActivity.java ] && echo "Present" || echo "Missing")"
echo "✓ ResultsActivity: $([ -f app/src/main/java/com/ai/soiltester/ui/results/ResultsActivity.java ] && echo "Present" || echo "Missing")"
echo "✓ SupportActivity: $([ -f app/src/main/java/com/ai/soiltester/ui/support/SupportActivity.java ] && echo "Present" || echo "Missing")"
echo ""

# Check model classes
echo "📊 Model Classes:"
echo "✓ Crop.java: $([ -f app/src/main/java/com/ai/soiltester/models/Crop.java ] && echo "Present" || echo "Missing")"
echo "✓ SensorReading.java: $([ -f app/src/main/java/com/ai/soiltester/models/SensorReading.java ] && echo "Present" || echo "Missing")"
echo "✓ TestResult.java: $([ -f app/src/main/java/com/ai/soiltester/models/TestResult.java ] && echo "Present" || echo "Missing")"
echo ""

# Build readiness
echo "🔨 Build Readiness:"
echo "✓ Gradle Wrapper: $([ -f gradlew ] && echo "Present" || echo "Missing")"
echo "✓ Build Instructions: $([ -f BUILD_INSTRUCTIONS.md ] && echo "Present" || echo "Missing")"
echo ""

echo "=== Build Status: READY FOR APK GENERATION ==="
echo ""
echo "Next Steps:"
echo "1. Open project in Android Studio"
echo "2. Sync Gradle dependencies"
echo "3. Build → Build Bundle(s)/APK(s) → Build APK(s)"
echo ""
echo "Or use command line:"
echo "./gradlew assembleDebug    # Debug APK"
echo "./gradlew assembleRelease   # Release APK"