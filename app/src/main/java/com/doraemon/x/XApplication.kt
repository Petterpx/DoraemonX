package com.doraemon.x

import android.app.Application

/**
 *
 * @author petterp
 */
class XApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        lateinit var context: Application
    }
}
