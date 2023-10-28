package com.doraemon.helper

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 *
 * @author petterp
 */
class CameraLifecycle : LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}
