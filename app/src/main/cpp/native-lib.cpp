#include <jni.h>
#include <opencv2/opencv.hpp>

using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_edgedetectionapp_MainActivity_processFrame(
        JNIEnv *env, jobject, jlong addrInput, jlong addrOutput, jint width, jint height) {

    // Retrieve the actual Mats
    cv::Mat &yuv = *(cv::Mat *) addrInput;
    cv::Mat &output = *(cv::Mat *) addrOutput;

    cv::Mat bgr, gray, edges;

    // Convert NV21 → BGR
    cv::cvtColor(yuv, bgr, cv::COLOR_YUV2BGR_NV21);

    // Convert to grayscale
    cv::cvtColor(bgr, gray, cv::COLOR_BGR2GRAY);
    output=bgr.clone();

//    // Apply Canny edge detection
//    cv::Canny(gray, edges, 100, 200);
//
//    // Copy result to output
//    edges.copyTo(output);
}
