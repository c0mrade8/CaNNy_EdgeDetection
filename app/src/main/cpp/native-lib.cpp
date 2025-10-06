#include <jni.h>
#include <opencv2/opencv.hpp>
using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_edgedetectionapp_MainActivity_processFrame(
        JNIEnv*, jobject, jlong addrInput, jlong addrOutput, jint width, jint height) {
    Mat &input = *(Mat *) addrInput;
    Mat &output = *(Mat *) addrOutput;
    if (input.empty()) return;

    //input to output for raw feed
    input.copyTo(output);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_edgedetectionapp_MainActivity_processEdges(
        JNIEnv*, jobject, jlong addrInput, jlong addrOutput, jint width, jint height) {
    Mat &input = *(Mat *) addrInput;
    Mat &output = *(Mat *) addrOutput;
    if (input.empty()) return;

    Mat gray;
    cvtColor(input, gray, COLOR_RGBA2GRAY);
    GaussianBlur(gray, gray, Size(5, 5), 1.5);
    Canny(gray, output, 80, 150);
    cvtColor(output, output, COLOR_GRAY2RGBA);
}
