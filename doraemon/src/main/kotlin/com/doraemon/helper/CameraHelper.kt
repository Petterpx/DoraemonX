package com.doraemon.helper

import android.annotation.SuppressLint
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.doraemon.DoraemonX
import com.doraemon.ext.BACK_EXECUTOR

/**
 *
 * @author petterp
 */
class CameraHelper {
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT
    private var imageAnalyzer: ImageAnalysis? = null
    internal lateinit var analyzer: ImageAnalysis.Analyzer

    fun initCamera(previewView: PreviewView, lifecycle: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(DoraemonX.context)
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases(lifecycle, previewView)
            },
            ContextCompat.getMainExecutor(DoraemonX.context),
        )
    }

    fun cancelCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(DoraemonX.context)
        if (cameraProviderFuture.isCancelled) return
        cameraProvider?.unbindAll()
        cameraProviderFuture.cancel(true)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases(
        lifecycle: LifecycleOwner,
        previewView: PreviewView,
    ) {
        kotlin.runCatching {
            val cameraProvider = cameraProvider ?: return
            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(cameraFacing).build()
            preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(previewView.display.rotation)
                .build()
            imageAnalyzer =
                ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setTargetRotation(previewView.display.rotation)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()
                    .also {
                        it.setAnalyzer(BACK_EXECUTOR, analyzer)
                    }
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycle,
                cameraSelector,
                preview,
                imageAnalyzer,
            )
            preview?.setSurfaceProvider(previewView.surfaceProvider)
        }
    }
}
