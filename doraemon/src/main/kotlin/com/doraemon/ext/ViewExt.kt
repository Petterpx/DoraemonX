package com.doraemon.ext

import android.content.res.Resources
import android.graphics.Outline
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider

/**
 *
 * @author petterp
 */

val Number.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(),
        Resources.getSystem().displayMetrics,
    ).toInt()

val Number.dpF: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(),
        Resources.getSystem().displayMetrics,
    )


/** 对View进行圆角裁切 */
fun View.setRoundCut(radius: Float) {
    try {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
        invalidateOutline()
        clipToOutline = true
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
