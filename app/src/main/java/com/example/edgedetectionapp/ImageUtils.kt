package com.example.edgedetectionapp

import android.media.Image
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer

object ImageUtils {

    fun imageToRgba(image: Image): Mat {
        val width = image.width
        val height = image.height

        // Read Y, U, V planes safely
        val yPlane = image.planes[0].buffer
        val uPlane = image.planes[1].buffer
        val vPlane = image.planes[2].buffer

        // Some devices output YUV as I420 layout (Y + U + V)
        val ySize = yPlane.remaining()
        val uSize = uPlane.remaining()
        val vSize = vPlane.remaining()

        val data = ByteArray(ySize + uSize + vSize)
        yPlane.get(data, 0, ySize)
        uPlane.get(data, ySize, uSize)
        vPlane.get(data, ySize + uSize, vSize)

        // Create YUV Mat (I420 layout)
        val yuvMat = Mat(height + height / 2, width, CvType.CV_8UC1)
        yuvMat.put(0, 0, data)

        // Convert YUV to RGBA using OpenCV
        val rgbaMat = Mat()
        Imgproc.cvtColor(yuvMat, rgbaMat, Imgproc.COLOR_YUV2RGBA_I420)
        yuvMat.release()

        return rgbaMat
    }
}
