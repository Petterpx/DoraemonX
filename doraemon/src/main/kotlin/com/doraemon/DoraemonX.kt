package com.doraemon

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.doraemon.config.DoraemonConfig
import com.doraemon.helper.CameraLifecycle
import com.doraemon.helper.GestureResultListener

/**
 *
 * @author petterp
 */
@SuppressLint("StaticFieldLeak")
object DoraemonX {
    private lateinit var control: DoraemonControl
    internal lateinit var context: Context

    fun init(obj: DoraemonConfig.() -> Unit): DoraemonX {
        val config = DoraemonConfig.builder(obj)
        this.context = config.context ?: throw IllegalAccessException("context != null")
        control = DoraemonControl().apply {
            init(config)
        }
        return this
    }

    fun startAnalyze(lifecycle: LifecycleOwner? = null, listener: GestureResultListener? = null) {
        checkOrInitListener(lifecycle, listener)
        control.startLive(lifecycle ?: CameraLifecycle())
    }

    fun pauseAnalyze() {
        control.pauseLive()
    }

    fun stopAnalyze() {
        control.stopLive()
    }

    private fun checkOrInitListener(
        lifecycle: LifecycleOwner?,
        listener: GestureResultListener?
    ) {
        control.updateConfig {
            this.resultListener = listener
        }
        lifecycle?.lifecycle?.apply {
            addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        control.updateConfig {
                            this.resultListener = null
                        }
                        stopAnalyze()
                    }
                }
            })
        }
    }
}
