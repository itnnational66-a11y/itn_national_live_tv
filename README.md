# ITN National Live TV — Native Android & Android TV App

Native Android TV & Mobile streaming application for **ITN National** built with **Kotlin**, **Jetpack Compose**, and **Android Media3 ExoPlayer 1.5.1**.

Stream URL: `https://newsliveindia.com:4433/itn/index.m3u8`

---

## 🚀 Key Features

- **Media3 ExoPlayer HLS**: Hardware-accelerated low-latency playback of live HLS m3u8 streams.
- **Pure Full-Screen Landscape**: Immersive sticky mode locks to landscape orientation without letterboxing disruptions.
- **Instant Autoplay**: Stream starts immediately when app is launched.
- **Robust Error Handling**: Automatic reconnect with exponential backoff on buffer underflows or Wi-Fi drops.
- **Android TV Remote Ready**: Native D-Pad navigation (DPAD_CENTER, MEDIA_PLAY_PAUSE, BACK) with high-contrast glowing TV focus indicators.
- **Dual Launcher**: Appears on both standard Android Phone launchers and Android TV Leanback home screens.
- **Distraction-Free**: Zero login, zero advertisements, pure live stream.

---

## 🛠️ Prerequisites

- **Android Studio Ladybug (2024.2+)** or newer
- **JDK 17** or **JDK 21**
- **Android SDK Platform 35** (Android 15) / Min SDK 24 (Android 7.0+)

---

## 📦 How to Build the APK

### 1. Build via Terminal (Fastest)

Clone or extract the project directory and run:

```bash
# For Linux / macOS
chmod +x ./gradlew
./gradlew assembleDebug

# For Windows PowerShell
.\gradlew.bat assembleDebug
```

The compiled APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 2. Build Release (Signed APK)

```bash
./gradlew assembleRelease
```

The release APK will be located at:
```
app/build/outputs/apk/release/app-release.apk
```

---

## 📺 How to Install on Android TV / Fire TV Stick

1. Enable **Developer Options** and **ADB Debugging** on your Android TV:
   - Go to **Settings** > **Device Preferences** > **About** > Click **Build** 7 times.
   - Go to **Developer Options** > Turn on **USB Debugging** & **Network Debugging**.
2. Find the TV's IP address (e.g. `192.168.1.105`).
3. Connect from your computer via ADB:
   ```bash
   adb connect 192.168.1.105:5555
   adb devices
   ```
4. Install the APK:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
5. The **ITN National** banner will appear on your TV Home screen under Apps!
