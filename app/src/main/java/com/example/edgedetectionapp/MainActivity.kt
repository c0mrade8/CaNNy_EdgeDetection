package com.example.edgedetectionapp

import android.media.Image
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat

class MainActivity : AppCompatActivity() {

    // Ensure 'CameraHelper' is defined elsewhere in your project
    private lateinit var cameraHelper: CameraHelper
    private lateinit var imageView: ImageView

    companion object {
        init {
            // Load OpenCV first
            System.loadLibrary("opencv_java4")
            // Load your native JNI library
            System.loadLibrary("native-lib")
        }
    }

    // JNI function signature
    external fun processFrame(addrInput: Long, addrOutput: Long, width: Int, height: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OpenCVLoader.initDebug()
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)

        // --- CAMERA CALLBACK WITH CORRECTED YUV → NV21 → MAT ---
        cameraHelper = CameraHelper(this) { image ->
            val width = image.width
            val height = image.height

            val yPlane = image.planes[0]
            val uPlane = image.planes[1]
            val vPlane = image.planes[2]

            val ySize = yPlane.buffer.remaining()
            val uSize = uPlane.buffer.remaining()
            val vSize = vPlane.buffer.remaining()

            // Allocate NV21 array
            val nv21 = ByteArray(ySize + uSize + vSize)

            // Copy Y plane
            yPlane.buffer.get(nv21, 0, ySize)

            // Copy UV planes respecting pixelStride
            if (uPlane.pixelStride == 1) {
                // Planar (I420)
                uPlane.buffer.get(nv21, ySize, uSize)
                vPlane.buffer.get(nv21, ySize + uSize, vSize)
            } else {
                // Interleaved (NV21/NV12)
                val uvPos = ySize
                val uBuf = uPlane.buffer
                val vBuf = vPlane.buffer
                val chromaHeight = height / 2
                val chromaWidth = width / 2

                for (row in 0 until chromaHeight) {
                    for (col in 0 until chromaWidth) {
                        val uIndex = row * uPlane.rowStride + col * uPlane.pixelStride
                        val vIndex = row * vPlane.rowStride + col * vPlane.pixelStride
                        nv21[uvPos + 2 * (row * chromaWidth + col)] = vBuf.get(vIndex)
                        nv21[uvPos + 2 * (row * chromaWidth + col) + 1] = uBuf.get(uIndex)
                    }
                }
            }

            // Convert NV21 array to Mat
            val yuvMat = Mat(height + height / 2, width, CvType.CV_8UC1)
            yuvMat.put(0, 0, nv21)

            // Prepare output Mat
            val output = Mat(height, width, CvType.CV_8UC1)

            // Call your JNI processing function
            processFrame(yuvMat.nativeObjAddr, output.nativeObjAddr, width, height)

            // Convert result to Bitmap for display
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(output, bmp)

            runOnUiThread { imageView.setImageBitmap(bmp) }

            // Release resources
            image.close()
            yuvMat.release()
            output.release()
        }

        // Start camera stream
        cameraHelper.startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraHelper.stopCamera()
    }
}
