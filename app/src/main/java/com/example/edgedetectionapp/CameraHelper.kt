package com.example.edgedetectionapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.Image
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface

@SuppressLint("MissingPermission")
class CameraHelper(
    private val context: Context,
    private val onFrame: (Image) -> Unit
) {
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private lateinit var backgroundHandler: Handler
    private lateinit var imageReader: android.media.ImageReader

    fun startCamera() {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = manager.cameraIdList.first()

        val thread = HandlerThread("CameraThread").apply { start() }
        backgroundHandler = Handler(thread.looper)

        imageReader = android.media.ImageReader.newInstance(640, 480, ImageFormat.YUV_420_888, 2)
        imageReader.setOnImageAvailableListener({
            val image = it.acquireNextImage() ?: return@setOnImageAvailableListener
            onFrame(image)
        }, backgroundHandler)

        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                val surface = imageReader.surface
                val request = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                request.addTarget(surface)
                camera.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        request.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                        session.setRepeatingRequest(request.build(), null, backgroundHandler)
                    }
                    override fun onConfigureFailed(session: CameraCaptureSession) {}
                }, backgroundHandler)
            }
            override fun onDisconnected(camera: CameraDevice) {}
            override fun onError(camera: CameraDevice, error: Int) {}
        }, backgroundHandler)
    }

    fun stopCamera() {
        captureSession?.close()
        cameraDevice?.close()
        imageReader.close()
    }
}
