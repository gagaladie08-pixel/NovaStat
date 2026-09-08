package com.novastats.app.service.detection

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NovaNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // Utilisé comme fallback par MediaSessionDetector
        // pour avoir accès aux sessions actives
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Rien pour l'instant
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
    }
}