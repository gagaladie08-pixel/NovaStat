package com.novastats.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NovaApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}