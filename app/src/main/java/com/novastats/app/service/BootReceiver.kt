package com.novastats.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.novastats.app.service.detection.NovaDetectionService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON" -> {
                NovaDetectionService.start(context)
            }
        }
    }
}