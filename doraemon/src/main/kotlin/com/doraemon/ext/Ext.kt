package com.doraemon.ext

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 *
 * @author petterp
 */
internal const val MP_RECOGNIZER_TASK = "gesture_recognizer.task"
internal const val DELEGATE_CPU = 0
internal const val DELEGATE_GPU = 1
internal const val DEFAULT_HAND_DETECTION_CONFIDENCE = 0.5F
internal const val DEFAULT_HAND_TRACKING_CONFIDENCE = 0.5F
internal const val DEFAULT_HAND_PRESENCE_CONFIDENCE = 0.5F
internal const val OTHER_ERROR = 0
internal const val GPU_ERROR = 1
internal val MAIN_HANDLER = Handler(Looper.getMainLooper())
internal val BACK_EXECUTOR: ExecutorService = Executors.newSingleThreadExecutor()
