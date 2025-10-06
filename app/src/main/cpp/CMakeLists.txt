cmake_minimum_required(VERSION 3.10.2)
project("edgedetection")

# Tell CMake where to find OpenCV (adjust path if needed)
set(OpenCV_DIR "C:/Users/DELL/Downloads/opencv-4.12.0-android-sdk/OpenCV-android-sdk/sdk/native/jni")

find_package(OpenCV REQUIRED)

# Create a shared library from our native code
add_library(native-lib SHARED native-lib.cpp)

include_directories(${OpenCV_INCLUDE_DIRS})

target_link_libraries(native-lib ${OpenCV_LIBS} log jnigraphics)

# Include directories and link libraries
target_include_directories(native-lib PRIVATE ${OpenCV_INCLUDE_DIRS})
target_link_libraries(native-lib ${OpenCV_LIBS} log)
