package com.example.edgedetectionapp

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var glRenderer: GLRenderer
    private lateinit var cameraHelper: CameraHelper

    companion object {
        init {
            System.loadLibrary("opencv_java4")
            System.loadLibrary("native-lib")
        }
    }

    external fun processFrame(addrInput: Long, addrOutput: Long, width: Int, height: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OpenCVLoader.initDebug()
        setContentView(R.layout.activity_main)

        glSurfaceView = findViewById(R.id.glSurfaceView)
        glSurfaceView.setEGLContextClientVersion(2)
        glRenderer = GLRenderer()
        glSurfaceView.setRenderer(glRenderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        cameraHelper = CameraHelper(this) { image ->
            // Convert the YUV image to RGBA
            val rgba = ImageUtils.imageToRgba(image)

            // Prepare output Mat
            val output = Mat()

            // Rotate frame so it matches phone orientation (portrait)
            val rotated = Mat()
            org.opencv.core.Core.rotate(rgba, rotated, org.opencv.core.Core.ROTATE_90_CLOCKWISE)

            // Process rotated frame (Canny edges, etc.)
            processFrame(rotated.nativeObjAddr, output.nativeObjAddr, rotated.cols(), rotated.rows())

            // Send processed output to OpenGL renderer
            glRenderer.updateTexture(output)
            glSurfaceView.requestRender()

            // Clean up to prevent memory leaks
            image.close()
            rgba.release()
            rotated.release()
            output.release()
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1001)
        } else {
            cameraHelper.startCamera()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraHelper.startCamera()
            } else {
                // permission denied — show UI or close
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraHelper.stopCamera()
    }
}
