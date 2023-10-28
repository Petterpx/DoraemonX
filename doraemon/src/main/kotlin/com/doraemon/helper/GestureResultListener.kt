package com.doraemon.helper

import com.doraemon.GestureType

/**
 *
 * @author petterp
 */
interface GestureResultListener {
    fun onDefaultResult(type: GestureType, time: Long)
}

