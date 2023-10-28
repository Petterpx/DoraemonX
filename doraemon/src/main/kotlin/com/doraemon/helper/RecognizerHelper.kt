package com.doraemon.helper

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ImageProxy
import com.doraemon.DoraemonX
import com.doraemon.GestureType
import com.doraemon.ext.DEFAULT_HAND_DETECTION_CONFIDENCE
import com.doraemon.ext.DEFAULT_HAND_PRESENCE_CONFIDENCE
import com.doraemon.ext.DEFAULT_HAND_TRACKING_CONFIDENCE
import com.doraemon.ext.DELEGATE_CPU
import com.doraemon.ext.DELEGATE_GPU
import com.doraemon.ext.MAIN_HANDLER
import com.doraemon.ext.MP_RECOGNIZER_TASK
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import java.util.Locale

/**
 *
 * @author petterp
 */
class RecognizerHelper {

    private var currentDelegate: Int = DELEGATE_GPU
    private var runningMode: RunningMode = RunningMode.LIVE_STREAM
    private var minHandDetectionConfidence = DEFAULT_HAND_DETECTION_CONFIDENCE
    private var minHandTrackingConfidence = DEFAULT_HAND_TRACKING_CONFIDENCE
    private var minHandPresenceConfidence = DEFAULT_HAND_PRESENCE_CONFIDENCE
    private var gestureRecognizer: GestureRecognizer? = null
    private var resultListener: GestureResultListener? = null

    fun initGestureRecognizer(listener: GestureResultListener?) {
        this.resultListener = listener
        if (gestureRecognizer != null) return
        val baseOptionBuilder = BaseOptions.builder()
        when (currentDelegate) {
            DELEGATE_CPU -> {
                baseOptionBuilder.setDelegate(Delegate.CPU)
            }

            DELEGATE_GPU -> {
                baseOptionBuilder.setDelegate(Delegate.GPU)
            }
        }
        baseOptionBuilder.setModelAssetPath(MP_RECOGNIZER_TASK)
        kotlin.runCatching {
            val baseOptions = baseOptionBuilder.build()
            val optionsBuilder =
                GestureRecognizer.GestureRecognizerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setMinHandDetectionConfidence(minHandDetectionConfidence)
                    .setMinTrackingConfidence(minHandTrackingConfidence)
                    .setMinHandPresenceConfidence(minHandPresenceConfidence)
                    .setRunningMode(runningMode)

            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder
                    .setResultListener(this::returnLivestreamResult)
            }
            val options = optionsBuilder.build()
            gestureRecognizer =
                GestureRecognizer.createFromOptions(DoraemonX.context, options)
        }
    }

    fun recognizeLiveStream(
        imageProxy: ImageProxy,
    ) {
        val frameTime = SystemClock.uptimeMillis()
        val bitmapBuffer = Bitmap.createBitmap(
            imageProxy.width,
            imageProxy.height,
            Bitmap.Config.ARGB_8888,
        )
        bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)
        imageProxy.close()
        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            postScale(
                -1f,
                1f,
                imageProxy.width.toFloat(),
                imageProxy.height.toFloat(),
            )
        }
        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            matrix,
            true,
        )
        val mpImage = BitmapImageBuilder(rotatedBitmap).build()
        recognizeAsync(mpImage, frameTime)
    }

    // 使用MediaPipe手势识别API运行手势识别
    @VisibleForTesting
    fun recognizeAsync(mpImage: MPImage, frameTime: Long) {
        // Live Stream时，结果在 returnLivestreamResult 返回
        gestureRecognizer?.recognizeAsync(mpImage, frameTime)
    }

    private fun returnLivestreamResult(
        result: GestureRecognizerResult,
        input: MPImage,
    ) {
        val sortedCategories = result.gestures().firstOrNull()?.sortedByDescending { it.score() }
        sortedCategories?.firstOrNull()?.apply {
            val finishTimeMs = SystemClock.uptimeMillis()
            val inferenceTime = finishTimeMs - result.timestampMs()
            val name = categoryName()
            val score = String.format(Locale.US, "%.2f", score())
            actionResult(name, score, inferenceTime)
        }
    }

    private fun actionResult(name: String, score: String, time: Long) {
        if (resultListener == null) return
        val type = kotlin.runCatching { GestureType.valueOf(name) }.getOrNull() ?: GestureType.None
        MAIN_HANDLER.post { resultListener?.onDefaultResult(type, time) }
    }

    fun clearGestureRecognizer() {
        gestureRecognizer?.close()
        gestureRecognizer = null
    }
}
