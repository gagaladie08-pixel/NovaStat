package com.novastats.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.novastats.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// ════════════════════════════════════════════
// PATTERNS DE VIBRATION
// ════════════════════════════════════════════
object VibratePatterns {
    val SIMPLE    = longArrayOf(0, 100)
    val DOUBLE    = longArrayOf(0, 100, 50, 100)
    val TRIPLE    = longArrayOf(0, 100, 50, 100, 50, 100)
    val IMPACT    = longArrayOf(0, 200, 100, 200)
    val LEGENDARY = longArrayOf(0, 150, 50, 150, 50, 150, 100, 300)
    val ALERT     = longArrayOf(0, 300, 200, 300)
}

// ════════════════════════════════════════════
// CANAUX DE NOTIFICATION
// ════════════════════════════════════════════
object NovaChannels {
    // Certifications
    const val CERT_SILVER   = "nova_cert_silver"
    const val CERT_GOLD     = "nova_cert_gold"
    const val CERT_PLATINUM = "nova_cert_platinum"
    const val CERT_DIAMOND  = "nova_cert_diamond"

    // Panthéon
    const val PANTHEON_STAR      = "nova_pantheon_star"
    const val PANTHEON_SUPERSTAR = "nova_pantheon_superstar"
    const val PANTHEON_MEGASTAR  = "nova_pantheon_megastar"
    const val PANTHEON_LEGEND    = "nova_pantheon_legend"
    const val PANTHEON_MYTHIQUE  = "nova_pantheon_mythique"

    // Hall of Fame
    const val HALL_OF_FAME = "nova_hall_of_fame"

    // Records
    const val RECORDS = "nova_records"

    // Awards
    const val AWARDS = "nova_awards"

    // Service
    const val SERVICE_FG     = "nova_service_foreground"
    const val SERVICE_ALERTS = "nova_service_alerts"

    // Confirmations
    const val CONFIRMATIONS = "nova_confirmations"

    // Détection
    const val DETECTION = "nova_detection"
}

// ════════════════════════════════════════════
// NOTIFICATION MANAGER
// ════════════════════════════════════════════
@Singleton
class NovaNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val manager = context.getSystemService(
        Context.NOTIFICATION_SERVICE
    ) as NotificationManager

    private var notifIdCounter = 2000

    // ── Préférences utilisateur ───────────────
    var certSilverEnabled = true
    var certGoldEnabled = true
    var certPlatinumEnabled = true
    var certDiamondEnabled = true
    var pantheonStarEnabled = true
    var pantheonSuperstarEnabled = true
    var pantheonMegastarEnabled = true
    var pantheonLegendEnabled = true
    var pantheonMythiqueEnabled = true
    var hofEnabled = true

    // ── Anti-spam ─────────────────────────────
    private val lastNotifTime = mutableMapOf<String, Long>()
    private val COOLDOWN_MS = 5 * 60 * 1000L // 5 min

    init {
        createAllChannels()
    }

    // ════════════════════════════════════════
    // CRÉER LES CANAUX
    // ════════════════════════════════════════
    private fun createAllChannels() {
        val channels = listOf(
            // Détection
            Triple(
                NovaChannels.DETECTION,
                "Détection musicale",
                NotificationManager.IMPORTANCE_LOW
            ),
            // Certifications
            Triple(
                NovaChannels.CERT_SILVER,
                "🥉 Certification Argent",
                NotificationManager.IMPORTANCE_DEFAULT
            ),
            Triple(
                NovaChannels.CERT_GOLD,
                "🥈 Certification Or",
                NotificationManager.IMPORTANCE_DEFAULT
            ),
            Triple(
                NovaChannels.CERT_PLATINUM,
                "🥇 Certification Platine",
                NotificationManager.IMPORTANCE_HIGH
            ),
            Triple(
                NovaChannels.CERT_DIAMOND,
                "💎 Certification Diamant",
                NotificationManager.IMPORTANCE_HIGH
            ),
            // Panthéon
            Triple(
                NovaChannels.PANTHEON_STAR,
                "⭐ Statut Star",
                NotificationManager.IMPORTANCE_DEFAULT
            ),
            Triple(
                NovaChannels.PANTHEON_SUPERSTAR,
                "🌟 Statut Superstar",
                NotificationManager.IMPORTANCE_DEFAULT
            ),
            Triple(
                NovaChannels.PANTHEON_MEGASTAR,
                "👑 Statut Megastar",
                NotificationManager.IMPORTANCE_HIGH
            ),
            Triple(
                NovaChannels.PANTHEON_LEGEND,
                "🏛️ Statut Légende",
                NotificationManager.IMPORTANCE_HIGH
            ),
            Triple(
                NovaChannels.PANTHEON_MYTHIQUE,
                "✨ Statut Mythique",
                NotificationManager.IMPORTANCE_HIGH
            ),
            // Hall of Fame
            Triple(
                NovaChannels.HALL_OF_FAME,
                "🏛️ Hall of Fame",
                NotificationManager.IMPORTANCE_HIGH
            ),
            // Records
            Triple(
                NovaChannels.RECORDS,
                "🏆 Records",
                NotificationManager.IMPORTANCE_DEFAULT
            ),
            // Awards
            Triple(
                NovaChannels.AWARDS,
                "🎵 Nova Awards",
                NotificationManager.IMPORTANCE_HIGH
            ),
            // Service
            Triple(
                NovaChannels.SERVICE_FG,
                "Service NovaStats",
                NotificationManager.IMPORTANCE_LOW
            ),
            Triple(
                NovaChannels.SERVICE_ALERTS,
                "⚠️ Alertes Service",
                NotificationManager.IMPORTANCE_HIGH
            ),
            // Confirmations
            Triple(
                NovaChannels.CONFIRMATIONS,
                "📋 Confirmations",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        channels.forEach { (id, name, importance) ->
            val channel = NotificationChannel(id, name, importance).apply {
                setShowBadge(importance >= NotificationManager.IMPORTANCE_DEFAULT)
            }
            manager.createNotificationChannel(channel)
        }
    }

    // ════════════════════════════════════════
    // CERTIFICATIONS
    // ════════════════════════════════════════
    fun notifyCertification(
        title: String,
        artist: String,
        level: String,
        multiplier: Int
    ) {
        val key = "cert_${title}_${level}_$multiplier"
        if (isSpam(key)) return

        when (level) {
            "SILVER" -> {
                if (!certSilverEnabled) return
                sendNotif(
                    channelId = NovaChannels.CERT_SILVER,
                    title = "🥉 Certification Argent !",
                    message = "\"$title\" de $artist vient d'être certifié Argent !",
                    vibrate = VibratePatterns.SIMPLE
                )
            }
            "GOLD" -> {
                if (!certGoldEnabled) return
                sendNotif(
                    channelId = NovaChannels.CERT_GOLD,
                    title = "🥈 Certification Or !",
                    message = "\"$title\" de $artist vient d'être certifié Or !",
                    vibrate = VibratePatterns.SIMPLE
                )
            }
            "PLATINUM" -> {
                if (!certPlatinumEnabled) return
                sendNotif(
                    channelId = NovaChannels.CERT_PLATINUM,
                    title = "🥇 Certification Platine !",
                    message = "\"$title\" de $artist atteint le Platine !",
                    vibrate = VibratePatterns.DOUBLE
                )
            }
            "DIAMOND" -> {
                if (!certDiamondEnabled) return
                val diamondLabel = if (multiplier > 1)
                    "${"💎".repeat(multiplier.coerceAtMost(3))} ${multiplier}x Diamant !"
                else "💎 Diamant !"
                sendNotif(
                    channelId = NovaChannels.CERT_DIAMOND,
                    title = diamondLabel,
                    message = "\"$title\" de $artist atteint le Diamant !",
                    vibrate = VibratePatterns.TRIPLE
                )
            }
        }
    }

    // ════════════════════════════════════════
    // PANTHÉON
    // ════════════════════════════════════════
    fun notifyPantheon(artistName: String, status: String) {
        val key = "pantheon_${artistName}_$status"
        if (isSpam(key)) return

        when (status) {
            "STAR" -> {
                if (!pantheonStarEnabled) return
                sendNotif(
                    channelId = NovaChannels.PANTHEON_STAR,
                    title = "⭐ Nouveau Star !",
                    message = "$artistName rejoint le Panthéon en tant que Star !",
                    vibrate = VibratePatterns.SIMPLE
                )
            }
            "SUPERSTAR" -> {
                if (!pantheonSuperstarEnabled) return
                sendNotif(
                    channelId = NovaChannels.PANTHEON_SUPERSTAR,
                    title = "🌟 Superstar !",
                    message = "$artistName devient Superstar dans le Panthéon !",
                    vibrate = VibratePatterns.SIMPLE
                )
            }
            "MEGASTAR" -> {
                if (!pantheonMegastarEnabled) return
                sendNotif(
                    channelId = NovaChannels.PANTHEON_MEGASTAR,
                    title = "👑 MEGASTAR !",
                    message = "$artistName atteint le statut Megastar !",
                    vibrate = VibratePatterns.DOUBLE
                )
            }
            "LEGENDE" -> {
                if (!pantheonLegendEnabled) return
                sendNotif(
                    channelId = NovaChannels.PANTHEON_LEGEND,
                    title = "🏛️ LÉGENDE !",
                    message = "$artistName entre dans la Légende du Panthéon !",
                    vibrate = VibratePatterns.TRIPLE
                )
            }
            "MYTHIQUE" -> {
                if (!pantheonMythiqueEnabled) return
                sendNotif(
                    channelId = NovaChannels.PANTHEON_MYTHIQUE,
                    title = "✨ MYTHIQUE !",
                    message = "$artistName atteint le statut suprême : MYTHIQUE !",
                    vibrate = VibratePatterns.LEGENDARY
                )
            }
        }
    }

    // ════════════════════════════════════════
    // HALL OF FAME
    // ════════════════════════════════════════
    fun notifyHallOfFame(
        entityName: String,
        entryType: String
    ) {
        if (!hofEnabled) return
        val key = "hof_${entityName}_$entryType"
        if (isSpam(key)) return

        val typeLabel = when (entryType) {
            "DIRECT_DEBUT"   -> "🚀 Direct Debut"
            "LONG_RUN"       -> "👑 Long Run"
            "TRIPLE_DEBUT"   -> "🌍 Triple Debut"
            "LEGENDARY_RUN"  -> "🏅 Legendary Run"
            else             -> entryType
        }

        sendNotif(
            channelId = NovaChannels.HALL_OF_FAME,
            title = "🏛️ Hall of Fame !",
            message = "\"$entityName\" entre au Hall of Fame — $typeLabel !",
            vibrate = VibratePatterns.IMPACT
        )
    }

    // ════════════════════════════════════════
    // SERVICE ALERT
    // ════════════════════════════════════════
    fun notifyServiceDown() {
        sendNotif(
            channelId = NovaChannels.SERVICE_ALERTS,
            title = "⚠️ Service NovaStats",
            message = "Le service de détection est inactif. Appuie pour relancer.",
            vibrate = VibratePatterns.ALERT
        )
    }

    // ════════════════════════════════════════
    // ENVOYER UNE NOTIFICATION
    // ════════════════════════════════════════
    private fun sendNotif(
        channelId: String,
        title: String,
        message: String,
        vibrate: LongArray = VibratePatterns.SIMPLE
    ) {
        try {
            val notification = NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setAutoCancel(true)
                .setVibrate(vibrate)
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(message)
                )
                .build()

            manager.notify(notifIdCounter++, notification)

            // Vibration
            vibrate(vibrate)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ════════════════════════════════════════
    // VIBRATION
    // ════════════════════════════════════════
    private fun vibrate(pattern: LongArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(
                    Context.VIBRATOR_MANAGER_SERVICE
                ) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(
                    VibrationEffect.createWaveform(pattern, -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(
                    Context.VIBRATOR_SERVICE
                ) as Vibrator
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ════════════════════════════════════════
    // ANTI-SPAM
    // ════════════════════════════════════════
    private fun isSpam(key: String): Boolean {
        val now = System.currentTimeMillis()
        val last = lastNotifTime[key] ?: 0L
        return if (now - last < COOLDOWN_MS) {
            true
        } else {
            lastNotifTime[key] = now
            false
        }
    }
}