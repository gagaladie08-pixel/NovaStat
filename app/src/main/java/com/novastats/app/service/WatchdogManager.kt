package com.novastats.app.service

import android.content.Context
import com.novastats.app.service.detection.NovaDetectionService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchdogManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val CHECK_INTERVAL_MS = 5 * 60 * 1000L // 5 minutes
    private var isRunning = false

    fun start() {
        if (isRunning) return
        isRunning = true

        scope.launch {
            while (isRunning) {
                delay(CHECK_INTERVAL_MS)
                checkAndRestart()
            }
        }
    }

    fun stop() {
        isRunning = false
    }

    private fun checkAndRestart() {
        try {
            NovaDetectionService.start(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}