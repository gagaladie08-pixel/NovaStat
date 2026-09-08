package com.novastats.app.service.detection

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// ════════════════════════════════════════════
// MODÈLE — Titre détecté
// ════════════════════════════════════════════
data class DetectedTrack(
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val sourceApp: String,
    val detectionSource: String, // MediaSession / Notification
    val timestamp: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// MEDIASESSION DETECTOR
// ════════════════════════════════════════════
@Singleton
class MediaSessionDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Callback quand un titre est détecté
    var onTrackDetected: ((DetectedTrack) -> Unit)? = null
    var onPlaybackStopped: (() -> Unit)? = null

    // Apps whitelistées
    private val whitelist = mutableSetOf<String>()

    // Dernier titre détecté (anti-doublon)
    private var lastHash: String = ""
    private var lastHashTime: Long = 0
    private val DEBOUNCE_MS = 5000L // 5 secondes

    // ── Démarrer la surveillance ──────────────
    fun start() {
        try {
            val sessionManager = context.getSystemService(
                Context.MEDIA_SESSION_SERVICE
            ) as MediaSessionManager

            val componentName = ComponentName(
                context,
                NovaNotificationListener::class.java
            )

            val sessions = sessionManager.getActiveSessions(componentName)
            sessions.forEach { controller ->
                attachController(controller)
            }

            // Surveiller les nouvelles sessions
            sessionManager.addOnActiveSessionsChangedListener(
                { newSessions ->
                    newSessions?.forEach { controller ->
                        attachController(controller)
                    }
                },
                componentName
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ── Attacher un controller ────────────────
    private fun attachController(controller: MediaController) {
        // Vérifier whitelist
        val packageName = controller.packageName
        if (whitelist.isNotEmpty() && !whitelist.contains(packageName)) {
            return
        }

        controller.registerCallback(object : MediaController.Callback() {

            override fun onMetadataChanged(metadata: MediaMetadata?) {
                val state = controller.playbackState
                if (state?.state == PlaybackState.STATE_PLAYING) {
                    metadata?.let { processMeta(it, packageName) }
                }
            }

            override fun onPlaybackStateChanged(state: PlaybackState?) {
                when (state?.state) {
                    PlaybackState.STATE_PLAYING -> {
                        controller.metadata?.let {
                            processMeta(it, packageName)
                        }
                    }
                    PlaybackState.STATE_STOPPED,
                    PlaybackState.STATE_NONE -> {
                        onPlaybackStopped?.invoke()
                    }
                    else -> {}
                }
            }
        })
    }

    // ── Traiter les métadonnées ───────────────
    private fun processMeta(metadata: MediaMetadata, sourceApp: String) {
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
            ?.trim() ?: return
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
            ?.trim()
            ?: metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
                ?.trim()
            ?: return
        val album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM)
            ?.trim() ?: ""
        val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)

        // Filtrer les pubs et titres vides
        if (title.isEmpty() || artist.isEmpty()) return
        if (isProbablyAd(title, artist)) return

        // Anti-doublon (hash + 5s)
        val hash = "$artist|$title"
        val now = System.currentTimeMillis()
        if (hash == lastHash && (now - lastHashTime) < DEBOUNCE_MS) return

        lastHash = hash
        lastHashTime = now

        val detected = DetectedTrack(
            title = normalizeTitle(title),
            artist = normalizeArtist(artist),
            album = album,
            durationMs = duration,
            sourceApp = sourceApp,
            detectionSource = "MediaSession"
        )

        onTrackDetected?.invoke(detected)
    }

    // ── Normalisation ─────────────────────────
    private fun normalizeTitle(title: String): String {
        var result = title
        // Nettoyer les mots indésirables
        val keywords = listOf(
            " (Remix)", " (Acoustic)", " (Live)", " (Instrumental)",
            " (Extended)", " (Extended Mix)", " (Edit)", " (Radio Edit)",
            " (Remaster)", " (Remastered)", " (DJ Mix)", " (DJ Remix)",
            " (Club Mix)", " (Club Edit)", " (EDM Remix)", " (Deluxe)",
            " (Bonus Track)", " (Slowed)", " (Sped Up)", " (Reverb)",
            " (Slowed + Reverb)", " (Bass Boosted)", " (Lofi)"
        )
        keywords.forEach { keyword ->
            result = result.replace(keyword, "", ignoreCase = true)
        }
        // Nettoyer symboles
        result = result.replace("™", "").replace("®", "").trim()
        return result
    }

    private fun normalizeArtist(artist: String): String {
        return artist
            .replace("™", "")
            .replace("®", "")
            .trim()
    }

    // ── Détection publicité ───────────────────
    private fun isProbablyAd(title: String, artist: String): Boolean {
        val adKeywords = listOf(
            "advertisement", "publicité", "spotify",
            "ad", "commercial", "unknown"
        )
        val lowerTitle = title.lowercase()
        val lowerArtist = artist.lowercase()
        return adKeywords.any {
            lowerTitle.contains(it) || lowerArtist.contains(it)
        }
    }

    // ── Whitelist ─────────────────────────────
    fun setWhitelist(apps: Set<String>) {
        whitelist.clear()
        whitelist.addAll(apps)
    }
}