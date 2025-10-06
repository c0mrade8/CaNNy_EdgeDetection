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
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var glRenderer: GLRenderer
    private lateinit var cameraHelper: CameraHelper

    private var showEdges = false

    companion object {
        init {
            System.loadLibrary("opencv_java4")
            System.loadLibrary("native-lib")
        }
    }

    external fun processFrame(addrInput: Long, addrOutput: Long, width: Int, height: Int)
    external fun processEdges(addrInput: Long, addrOutput: Long, width: Int, height: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OpenCVLoader.initDebug()
        setContentView(R.layout.activity_main)

        glSurfaceView = findViewById(R.id.glSurfaceView)
        glSurfaceView.setEGLContextClientVersion(2)
        glRenderer = GLRenderer()
        glSurfaceView.setRenderer(glRenderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        val toggleButton = findViewById<Button>(R.id.toggleButton)
        toggleButton.setOnClickListener {
            showEdges = !showEdges
            toggleButton.text = if (showEdges) "Show Live Feed" else "Show Edges"
        }

        cameraHelper = CameraHelper(this) { image ->
            // Convert the YUV image to RGBA
            val rgba = ImageUtils.imageToRgba(image)

            // Prepare output Mat
            val output = Mat()

            // Rotate frame so it matches phone orientation (portrait)
            val rotated = Mat()
            org.opencv.core.Core.rotate(rgba, rotated, org.opencv.core.Core.ROTATE_90_CLOCKWISE)

            // Process rotated frame (Canny edges, etc.)
            if (showEdges) {
                // Apply Canny edge detection
                processEdges(rotated.nativeObjAddr, output.nativeObjAddr, rotated.cols(), rotated.rows())
            } else {
                // Just show live feed
                rotated.copyTo(output)
            }
            //processFrame(rotated.nativeObjAddr, output.nativeObjAddr, rotated.cols(), rotated.rows())

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1001)
        } else {
            cameraHelper.startCamera()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cameraHelper.startCamera()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraHelper.stopCamera()
    }
}
