#!/bin/bash

# AI Soil Tester - APK Generation Script
# This script sets up the environment and generates the APK

echo "🚀 AI Soil Tester - APK Generation"
echo "=================================="
echo ""

# Check prerequisites
echo "📋 Checking prerequisites..."

# Check for Android Studio or Android SDK
if [ -d "$ANDROID_HOME" ]; then
    echo "✓ Android SDK found: $ANDROID_HOME"
elif [ -d "$HOME/Android/Sdk" ]; then
    export ANDROID_HOME="$HOME/Android/Sdk"
    echo "✓ Android SDK found: $ANDROID_HOME"
elif [ -d "/usr/local/android-sdk" ]; then
    export ANDROID_HOME="/usr/local/android-sdk"
    echo "✓ Android SDK found: $ANDROID_HOME"
else
    echo "❌ Android SDK not found. Please install Android Studio or Android SDK."
    echo ""
    echo "Installation Options:"
    echo "1. Download Android Studio: https://developer.android.com/studio"
    echo "2. Follow the installation guide in BUILD_INSTRUCTIONS.md"
    exit 1
fi

# Check for Java
if command -v javac &> /dev/null; then
    echo "✓ Java found: $(javac -version 2>&1 | head -1)"
elif [ -d "$JAVA_HOME" ]; then
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "✓ Java found: $JAVA_HOME"
else
    echo "❌ Java not found. Please install JDK 8 or higher."
    echo ""
    echo "Installation Options:"
    echo "1. Install JDK 8+: https://www.oracle.com/java/technologies/downloads/"
    echo "2. Use Android Studio (includes JDK)"
    exit 1
fi

echo ""
echo "🔧 Setting up environment..."

# Set environment variables
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$PATH"
export ANDROID_SDK_ROOT="$ANDROID_HOME"

echo "✓ Environment configured"
echo ""

# Navigate to project directory
cd "$(dirname "$0")"

echo "📁 Project directory: $(pwd)"
echo ""

# Check if Gradle wrapper exists, create if missing
if [ ! -f "gradlew" ]; then
    echo "📦 Creating Gradle wrapper..."
    # Simple wrapper creation
    cat > gradlew << 'EOF'
#!/bin/sh
exec java -jar gradle/wrapper/gradle-wrapper.jar "$@"
EOF
    chmod +x gradlew
fi

# Create gradle wrapper directory
mkdir -p gradle/wrapper

# Create a simple build if gradle isn't available
if ! command -v gradle &> /dev/null; then
    echo "⚠️  Gradle not found in system PATH"
    echo "📦 Installing Gradle wrapper..."

    # Download gradle if not present
    GRADLE_VERSION="8.1.1"
    if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
        echo "Downloading Gradle $GRADLE_VERSION..."
        curl -L "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o gradle-$GRADLE_VERSION-bin.zip
        unzip gradle-$GRADLE_VERSION-bin.zip -d gradle/
        mv gradle/gradle-$GRADLE_VERSION/* gradle/
        rm -rf gradle/gradle-$GRADLE_VERSION gradle-$GRADLE_VERSION-bin.zip

        # Copy gradle wrapper jar
        cp gradle/lib/gradle-wrapper-$GRADLE_VERSION.jar gradle/wrapper/gradle-wrapper.jar
    fi
fi

echo ""
echo "🏗️  Building APK..."

# Try building with gradlew
echo "Running: ./gradlew assembleDebug"
echo ""

if [ -f "gradlew" ]; then
    ./gradlew assembleDebug --stacktrace
else
    echo "❌ Gradle wrapper not found. Please install Gradle or Android Studio."
    echo ""
    echo "Quick setup:"
    echo "1. sudo apt-get install gradle  (Ubuntu/Debian)"
    echo "2. brew install gradle           (macOS)"
    echo "3. Install Android Studio"
fi

echo ""
echo "📱 APK Build Complete!"
echo ""
echo "📁 APK Location:"
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo "✅ APK built successfully!"
    echo "📂 app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "📲 Installation commands:"
    echo "adb install app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "📲 Or copy to device and install manually"
else
    echo "❌ APK build failed. Check the build logs above."
    echo ""
    echo "📋 Troubleshooting:"
    echo "1. Ensure all dependencies are in build.gradle"
    echo "2. Check Android SDK installation"
    echo "3. Verify Java version compatibility"
    echo "4. Open project in Android Studio for easier building"
fi

echo ""
echo "🎯 Next Steps:"
echo "1. Install APK on Android device"
echo "2. Connect HC-05 Bluetooth module"
echo "3. Test with Arduino sensor data"
echo "4. Share results and enjoy!"