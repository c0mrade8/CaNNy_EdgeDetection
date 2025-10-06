# 📸 Edge Detection App (Software Engineering Intern – R&D Assignment)

This project implements a **real-time edge detection pipeline** using **OpenCV**, **OpenGL ES**, and **Camera2 API** on Android, with a companion **TypeScript web viewer** for visualization.

---

## 🧱 Project Main Structure

/app/src/main
├──/java/com/example/edgedetectionapp/  
├── MainActivity.kt → Android entry point; handles camera feed, edge toggle, and FPS counter  
├── CameraHelper.kt → Camera2 API helper for capturing YUV frames  
├── ImageUtils.kt → Converts YUV_420_888 frames to RGBA (OpenCV Mat)  
├── GLRenderer.kt → OpenGL ES 2.0 renderer displaying processed frames  
│ └── -- this is the expected /gl file   
└── cpp/  
├── native-lib.cpp → C++ code for OpenCV edge detection (Canny), native C++ implementation file that runs image processing logic using OpenCV via the JNI bridge (Java ↔ C++)  
│ └── -- this is the expected /jni file  
├── CMakeLists.txt → tells CMake (the native build system) how to compile native-lib.cpp and link it with OpenCV and Android’s NDK    
│ └── -- this is the expected /jni file  
└── res/  
├── layout/activity_main.xml → UI layout with GLSurfaceView, FPS counter, and toggle button  
├── values/strings.xml → App text resources  
├── values/colors.xml → Color definitions 
└── AndroidManifest.xml → declares everything the Android system needs to know before running the app  
└── README.md  
└── build.gradle.kts → defines Android SDK versions, Dependencies (OpenCV, AppCompat, etc.), Build types (debug/release), Native CMake configuration  
└── settings.gradle.kts → tells Gradle which modules belong to the project  

/web  
├── index.html → Static web page showing a sample processed frame  
├── viewer.ts → TypeScript script updating FPS/resolution overlay  
├── viewer.js → Compiled JavaScript output from viewer.ts  
├── styles.css → Styling for the web viewer  
└── sample.png → Saved processed frame from the Android app  


>  **Note:**  
> The `/app`, `/jni`, `/gl`, and `/web` structure is represented conceptually.  
> Android Studio enforces its own `src/main/java` and `res` folder structure — this README maps those to the logical modules defined in the assignment.

---

## ⚙️ Features

✅ **Real-time camera processing** using Android’s `Camera2` API  
✅ **Edge detection** via OpenCV’s `Canny` operator in C++ (JNI)  
✅ **OpenGL ES rendering** for efficient frame display  
✅ **Toggle button** to switch between *Live Feed* and *Edge Detection*  
✅ **FPS overlay** for real-time performance monitoring  
✅ **TypeScript + Web viewer** displaying a static sample with simulated FPS  

---

## 🧩 Technical Overview

### 🔹 Android App
- Written in **Kotlin**
- Uses **OpenCV (JNI)** for native image processing
- Camera frames (YUV_420_888) are converted to RGBA using OpenCV
- OpenGL ES 2.0 displays frames efficiently via a `GLSurfaceView`
- `native-lib.cpp` performs:
  ```cpp
  cvtColor(input, output, COLOR_RGBA2GRAY);
  GaussianBlur(output, output, Size(5, 5), 1.5);
  Canny(output, output, 100, 200);
  cvtColor(output, output, COLOR_GRAY2RGBA);
  ```

- Also included, Live FPS counter updates dynamically on-screen

## Web Viewer

A minimal static TypeScript + HTML page that:
- Displays a saved processed frame (sample.png)
- Simulates FPS/resolution updates via DOM manipulation
- Demonstrates familiarity with modern TypeScript tooling

## How It Works

- Camera2 captures YUV frames → delivered via ImageReader.
- ImageUtils.imageToRgba() converts them into OpenCV Mat (RGBA).
- When “Show Edges” is pressed:
  -> JNI function processFrame() applies Canny edge detection.
- Result is sent to OpenGL renderer for display.
- FPS is measured and shown live on-screen.
- The processed frame can be exported for the /web viewer.

# Setup & Build Instructions
# Android App

## Requirements
- Android Studio (Giraffe or later) (used Narwhal 3 version 1.3.7)
- OpenCV Android SDK 4.x+ (used 4.12.0)
- Minimum SDK: 24 (Android 7.0)

## Steps
- Open the project in Android Studio.
- Sync Gradle to download dependencies.
- Connect an Android device. (via USB cable)
- Enable the developer mode on the android device and tick all necessary permissions(like install app through usb)
- Run the app.

## Permissions
- Camera permission is requested at runtime.
- Also, enable file transfer.

# Web Viewer

## Requirements
- Node.js + npm

## Steps
- Navigate to the web/ folder:
```bash
cd web
```
- Compile TypeScript to JavaScript:

```bash
npx tsc viewer.ts --target ES6 --outFile viewer.js
```
- Open index.html in your browser.

# Output
## Live Feed: Grayscale camera preview
## Edge Detection: Canny edge output (white edges on black background)
## Web Viewer:	Displays saved frame + simulated FPS overlay

# Technologies Used
- Kotlin, C++, TypeScript, HTML, CSS, Camera2, GLSurfaceView, JNI, OpenCV-Android 4.12.0, OpenGL ES 2.0.

> * Note*
> The Android project uses the default app/src/main/java and res layout enforced by Android Studio.
> The logical structure is as follows:
> /app → Kotlin + Android logic
> /jni → C++ OpenCV native processing
> /gl → OpenGL rendering code
> /web → TypeScript viewer

## Author
Niharika Rampathi  
Email ID: niharikarampathi2704@gmail.com  
Alternative Email ID: 22bds050@iiitdwd.ac.in  
