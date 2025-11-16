# 🤖 GitHub Actions - Auto APK Building

Create this file in your repository: `.github/workflows/build.yml`

```yaml
name: Build AI Soil Tester APK

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'

    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Build Debug APK
      run: ./gradlew assembleDebug

    - name: Build Release APK
      run: ./gradlew assembleRelease

    - name: Upload Debug APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk

    - name: Upload Release APK
      uses: actions/upload-artifact@v3
      with:
        name: app-release
        path: app/build/outputs/apk/release/app-release.apk

    - name: Upload Build Info
      uses: actions/upload-artifact@v3
      with:
        name: build-info
        path: app/build/outputs/apk/
```

---

## 🚀 **HOW TO USE:**

### **1. Add to Repository:**
1. Create `.github/workflows/` folder
2. Add `build.yml` file with content above
3. Commit and push to GitHub

### **2. Automatic APK Building:**
- **Triggers**: On every push to main/master branch
- **Debug APK**: Built and uploaded as `app-debug` artifact
- **Release APK**: Built and uploaded as `app-release` artifact
- **Build time**: 2-3 minutes

### **3. Download APK:**
1. Go to your repository on GitHub
2. Click **"Actions"** tab
3. Click on the latest build run
4. Download APK from **"Artifacts"** section

---

## 🎯 **BENEFITS:**

### **Automatic Building:**
- ✅ **APK generated** on every code change
- ✅ **Debug and Release** versions
- ✅ **Build history** for tracking
- ✅ **No manual work** required
- ✅ **Always available** from GitHub

### **Integration Ready:**
- ✅ **CD/CD Pipeline** ready
- ✅ **Release automation** possible
- ✅ **Testing integration** available
- ✅ **Version tracking** included

---

## 📱 **EXAMPLE WORKFLOW:**

```yaml
# Example of how your builds will look:
# Commit code → GitHub → Actions triggered → APK built → Ready for download

# Build artifacts:
# 📂 app-debug/app-debug.apk
# 📂 app-release/app-release.apk
```

---

## 💡 **SETUP INSTRUCTIONS:**

### **Quick Setup:**
```bash
# 1. Create directory structure
mkdir -p .github/workflows

# 2. Create workflow file
cat > .github/workflows/build.yml << 'EOF'
# [Paste the YAML content from above]
EOF

# 3. Add and commit
git add .github/workflows/build.yml
git commit -m "Add GitHub Actions workflow for APK building"
git push origin main
```

### **Verification:**
1. After pushing, check **"Actions"** tab on GitHub
2. Watch the build process
3. Download generated APKs when complete

---

## 🎉 **RESULT:**

With GitHub Actions set up:
- **Push code** → **APK built automatically**
- **Pull requests** → **Test APKs generated**
- **Releases** → **Production APKs ready**
- **No manual building** required

**Your AI Soil Tester app will be ready for CI/CD!** 🚀