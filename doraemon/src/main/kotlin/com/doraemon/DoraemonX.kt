package com.doraemon

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.doraemon.config.DoraemonConfig
import com.doraemon.helper.CameraLifecycle

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

    fun startAnalyze(lifecycle: LifecycleOwner? = null) {
        control.startLive(lifecycle ?: CameraLifecycle())
    }

    fun pauseAnalyze() {
        control.pauseLive()
    }
}
