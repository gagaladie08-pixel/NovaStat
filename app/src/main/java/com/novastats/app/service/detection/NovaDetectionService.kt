package com.novastats.app.service.detection

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.novastats.app.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NovaDetectionService : Service() {

    @Inject
    lateinit var mediaSessionDetector: MediaSessionDetector

    @Inject
    lateinit var scrobbleProcessor: ScrobbleProcessor

    companion object {
        const val CHANNEL_ID = "nova_service_foreground"
        const val NOTIF_ID = 1001

        fun start(context: android.content.Context) {
            val intent = Intent(context, NovaDetectionService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: android.content.Context) {
            val intent = Intent(context, NovaDetectionService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        setupDetector()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIF_ID, buildNotification("🟢 En écoute"))
        mediaSessionDetector.start()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        mediaSessionDetector.onTrackDetected = null
        mediaSessionDetector.onPlaybackStopped = null
    }

    // ── Setup détecteur ───────────────────────
    private fun setupDetector() {
        mediaSessionDetector.onTrackDetected = { detected ->
            scrobbleProcessor.onTrackDetected(detected)
            updateNotification("🎵 ${detected.title} — ${detected.artist}")
        }

        mediaSessionDetector.onPlaybackStopped = {
            scrobbleProcessor.onPlaybackPaused()
            updateNotification("🟢 En écoute")
        }

        scrobbleProcessor.onScrobbleValidated = { trackId ->
            // Notifier le bridge si nécessaire
        }
    }

    // ── Notification foreground ───────────────
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "NovaStats — Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Service de détection musicale"
            setShowBadge(false)
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("NovaStats")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIF_ID, buildNotification(text))
    }
}