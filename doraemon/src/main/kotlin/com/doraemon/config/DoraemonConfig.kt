package com.doraemon.config

import android.content.Context
import com.doraemon.helper.GestureResultListener

/**
 * DoraemonX配置类
 * @author petterp
 */
class DoraemonConfig private constructor() {
    var context: Context? = null
    var intervalTime: Long = 10L
    var enablePreview = false
    internal var resultListener: GestureResultListener? = null
    internal val preViewConfig: PreViewConfig = PreViewConfig()

    fun updatePreView(obj: PreViewConfig.() -> Unit) {
        preViewConfig.apply(obj).check()
    }

    companion object {
        fun builder(obj: DoraemonConfig.() -> Unit) = DoraemonConfig().also(obj)
    }
}
