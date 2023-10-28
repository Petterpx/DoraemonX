package com.doraemon

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.WindowManager
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.doraemon.config.DoraemonConfig
import com.doraemon.config.PreViewConfig
import com.doraemon.ext.BACK_EXECUTOR
import com.doraemon.ext.MAIN_HANDLER
import com.doraemon.ext.dp
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
        this.config = config
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
        val layoutParam = WindowManager.LayoutParams().apply {
            // 设置大小 自适应
            width = config.preViewConfig.w.dp
            height = config.preViewConfig.h.dp
            format = PixelFormat.TRANSPARENT
            flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            gravity = config.preViewConfig.gravity
            if (!config.preViewConfig.enablePreView) {
                x = -width
            }
        }
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.addView(view, layoutParam)
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
