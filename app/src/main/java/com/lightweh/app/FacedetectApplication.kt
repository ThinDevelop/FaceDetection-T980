package com.lightweh.app

import android.app.Application
import com.lightweh.facedetection.PreferencesManager

class FacedetectApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesManager.initializeInstance(this)
    }
}