# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Bluetooth-related classes
-keep class android.bluetooth.** { *; }
-keep class com.ai.soiltester.bluetooth.** { *; }

# Keep Crop recommendation classes
-keep class com.ai.soiltester.ai.** { *; }

# Keep model classes
-keep class com.ai.soiltester.models.** { *; }