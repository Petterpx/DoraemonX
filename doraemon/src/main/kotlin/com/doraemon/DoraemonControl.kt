package com.doraemon

import androidx.camera.core.ImageAnalysis
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.doraemon.config.DoraemonConfig
import com.doraemon.config.PreViewConfig
import com.doraemon.ext.BACK_EXECUTOR
import com.doraemon.ext.MAIN_HANDLER
import com.doraemon.helper.CameraHelper
import com.doraemon.helper.RecognizerHelper

/**
 *
 * @author petterp
 */
class DoraemonControl {

    private lateinit var config: DoraemonConfig
    private lateinit var view: PreviewView
    private lateinit var recognizer: RecognizerHelper
    private lateinit var cameraHelper: CameraHelper
    private var startTime = 0L

    fun init(config: DoraemonConfig) {
        initPreView()
        recognizer = RecognizerHelper()
        cameraHelper = CameraHelper().apply {
            analyzer = ImageAnalysis.Analyzer { image ->
                val currentTime = System.currentTimeMillis()
                if (currentTime - startTime < config.intervalTime) {
                    image.close()
                } else {
                    recognizer.recognizeLiveStream(image)
                    startTime = currentTime
                }
            }
        }
    }

    private fun initPreView() {
        val context = config.context ?: return
        view = PreviewView(context)
    }

    fun updatePreViewConfig(obj: PreViewConfig.() -> Unit) {
        config.updatePreView(obj)
    }

    fun startLive(lifecycleOwner: LifecycleOwner) {
        BACK_EXECUTOR.execute {
            recognizer.initGestureRecognizer(config.resultListener)
            MAIN_HANDLER.post {
                cameraHelper.initCamera(view, lifecycleOwner)
            }
        }
    }

    fun pauseLive() {
        BACK_EXECUTOR.execute {
            recognizer.clearGestureRecognizer()
        }
    }
}
