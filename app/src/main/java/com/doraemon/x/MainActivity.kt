package com.doraemon.x

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.doraemon.DoraemonX
import com.doraemon.helper.GestureResultListener
import com.doraemon.GestureType

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btnStart).setOnClickListener {
            DoraemonX.init {
                intervalTime = 500L
                context = XApplication.context
                resultListener = object : GestureResultListener {
                    override fun onDefaultResult(type: GestureType, time: Long) {
                        Log.e("petterp", "type---->$type")
                    }
                }
                updatePreView {
                    defaultSize(300F)
                    enablePreView = true
                }
            }.startAnalyze()
        }
    }
}
