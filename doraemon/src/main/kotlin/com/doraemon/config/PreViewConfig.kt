package com.doraemon.config

import android.view.Gravity

/**
 *
 * @author petterp
 */
class PreViewConfig {
    var w: Float = 0F
    var h: Float = 0F
    var gravity: Int = Gravity.TOP or Gravity.RIGHT
    var enablePreView = true

    fun defaultSize(w: Float) {
        this.w = w
        h = w * 4 / 3
    }

    internal fun check() {
        w = w.coerceAtLeast(1F)
        h = h.coerceAtLeast(1.3F)
    }
}
