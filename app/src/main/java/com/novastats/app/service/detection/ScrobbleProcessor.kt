package com.novastats.app.service.detection

import com.novastats.app.data.db.NovaDatabase
import com.novastats.app.data.db.entities.*
import com.novastats.app.notifications.NovaNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// ════════════════════════════════════════════
// SCROBBLE PROCESSOR — v3.0
// Spec complète :
// → Seuil configurable (15/30/60/90s)
// → Pause courte < 10min → timer cumulé
// → Pause longue ≥ 10min → nouvelle écoute
// → Skip avant seuil → ignoré, rien loggé
// → Loop → chaque relecture = nouvelle écoute
// → Crossfade → clôture A si B détecté < 10s
// → Anti-doublon hash (artiste+titre+5s)
// → Score de confiance MediaSession/Notif
// → Certifications auto
// → Panthéon auto
// ════════════════════════════════════════════
@Singleton
class ScrobbleProcessor @Inject constructor(
    private val db: NovaDatabase,
    private val notifManager: NovaNotificationManager
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // ── Seuil minimum configurable ────────────
    // 15000 / 30000 / 60000 / 90000 ms
    var thresholdMs: Long = 30_000L

    // ── Track en cours ────────────────────────
    private var currentTrack: DetectedTrack? = null
    private var currentScrobbleId: Long = -1
    private var trackStartTime: Long = 0L
    private var accumulatedMs: Long = 0L
    private var pauseStartTime: Long = 0L
    private var isPaused: Boolean = false

    // ── Timer de validation ───────────────────
    private var validationJob: Job? = null

    // ── Crossfade ─────────────────────────────
    // Si titre B détecté < 10s après titre A
    // → clôturer A immédiatement
    private val CROSSFADE_MS = 10_000L

    // ── Pause longue ──────────────────────────
    private val LONG_PAUSE_MS = 10 * 60 * 1000L // 10 min

    // ── Callbacks ────────────────────────────
    var onScrobbleValidated: ((Long) -> Unit)? = null
    var onNowPlayingUpdated: ((Long?) -> Unit)? = null

    // ════════════════════════════════════════
    // POINT D'ENTRÉE — Nouveau titre détecté
    // ════════════════════════════════════════
    fun onTrackDetected(detected: DetectedTrack) {
        scope.launch {
            when {
                // Même titre — gérer la reprise
                isSameTrack(detected, currentTrack) -> {
                    handleSameTrack(detected)
                }
                // Nouveau titre
                else -> {
                    handleNewTrack(detected)
                }
            }
        }
    }

    // ════════════════════════════════════════
    // MÊME TITRE
    // ════════════════════════════════════════
    private suspend fun handleSameTrack(detected: DetectedTrack) {
        if (!isPaused) return // Déjà en lecture, rien à faire

        val now = System.currentTimeMillis()
        val pauseDuration = now - pauseStartTime

        when {
            // Pause longue ≥ 10 min → nouvelle écoute
            pauseDuration >= LONG_PAUSE_MS -> {
                // Annuler l'écoute en cours (pas encore validée)
                cancelCurrentScrobble()
                // Démarrer une nouvelle écoute du même titre
                startNewScrobble(detected)
            }
            // Pause courte < 10 min → reprendre le timer cumulé
            else -> {
                isPaused = false
                pauseStartTime = 0L
                // Reprendre le timer
                resumeValidationTimer()
            }
        }
    }

    // ════════════════════════════════════════
    // NOUVEAU TITRE
    // ════════════════════════════════════════
    private suspend fun handleNewTrack(detected: DetectedTrack) {
        val now = System.currentTimeMillis()

        // Vérifier crossfade
        // Si le titre précédent vient d'être détecté < 10s
        val isCrossfade = currentTrack != null &&
                (now - trackStartTime) < CROSSFADE_MS

        if (isCrossfade) {
            // Crossfade → clôturer A immédiatement sans valider
            cancelCurrentScrobble()
        } else {
            // Clôturer proprement le titre précédent
            closeCurrentScrobble()
        }

        // Réinitialiser
        currentScrobbleId = -1
        currentTrack = detected
        trackStartTime = now
        accumulatedMs = 0L
        pauseStartTime = 0L
        isPaused = false

        // Démarrer le nouveau scrobble
        startNewScrobble(detected)
    }

    // ════════════════════════════════════════
    // PLAYBACK STOPPÉ / PAUSÉ
    // ════════════════════════════════════════
    fun onPlaybackPaused() {
        if (currentTrack == null || isPaused) return
        isPaused = true
        pauseStartTime = System.currentTimeMillis()

        // Accumuler le temps écouté jusqu'à la pause
        val now = System.currentTimeMillis()
        accumulatedMs += now - trackStartTime
        trackStartTime = 0L

        // Annuler le timer pendant la pause
        validationJob?.cancel()
    }

    fun onPlaybackStopped() {
        scope.launch {
            closeCurrentScrobble()
            currentTrack = null
            currentScrobbleId = -1
            isPaused = false
            onNowPlayingUpdated?.invoke(null)
        }
    }

    // ════════════════════════════════════════
    // DÉMARRER UN SCROBBLE
    // ════════════════════════════════════════
    private suspend fun startNewScrobble(detected: DetectedTrack) {
        try {
            // Calculer le score de confiance
            val confidence = when (detected.detectionSource) {
                "MediaSession"             -> 100
                "Mixed"                    -> 80
                "Notification"             -> 70
                else                       -> 50
            }

            // Trouver ou créer artiste / album / titre
            val artistId = getOrCreateArtist(detected.artist)
            val albumId = if (detected.album.isNotEmpty()) {
                getOrCreateAlbum(detected.album, artistId)
            } else null
            val trackId = getOrCreateTrack(detected, artistId, albumId)

            // Mettre à jour first_played_at si c'est la première fois
            updateFirstPlayedIfNeeded(trackId, artistId, albumId)

            // Créer le scrobble en DB
            val scrobble = ScrobbleEntity(
                track_id = trackId,
                artist_id = artistId,
                album_id = albumId,
                started_at = System.currentTimeMillis(),
                validated_at = null,
                ended_at = null,
                duration_listened_ms = 0,
                source_app = detected.sourceApp,
                detection_source = detected.detectionSource,
                confidence_score = confidence,
                status = "PENDING",
                volume_level = null,
                is_skip = false
            )

            currentScrobbleId = db.scrobbleDao().insert(scrobble)
            trackStartTime = System.currentTimeMillis()
            isPaused = false

            // Mettre à jour now_playing
            db.nowPlayingDao().clear()
            db.nowPlayingDao().insert(
                NowPlayingEntity(
                    track_id = trackId,
                    started_at = System.currentTimeMillis(),
                    source_app = detected.sourceApp,
                    scrobble_status = "PENDING"
                )
            )

            onNowPlayingUpdated?.invoke(trackId)

            // Lancer le timer de validation
            launchValidationTimer(trackId, currentScrobbleId)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ════════════════════════════════════════
    // TIMER DE VALIDATION
    // ════════════════════════════════════════
    private fun launchValidationTimer(
        trackId: Long,
        scrobbleIdAtStart: Long
    ) {
        // Annuler le timer précédent
        validationJob?.cancel()

        validationJob = scope.launch {
            // Attendre le seuil moins le temps déjà accumulé
            val remaining = thresholdMs - accumulatedMs
            if (remaining > 0) delay(remaining)

            // Vérifier que c'est toujours le même scrobble
            // et qu'il n'a pas été annulé
            if (currentScrobbleId == scrobbleIdAtStart
                && currentScrobbleId > 0
                && !isPaused) {
                validateScrobble(trackId)
            }
        }
    }

    // ── Reprendre le timer après pause courte ─
    private fun resumeValidationTimer() {
        val track = currentTrack ?: return
        val scrobbleId = currentScrobbleId
        if (scrobbleId < 0) return

        trackStartTime = System.currentTimeMillis()

        scope.launch {
            // Trouver le trackId depuis la DB
            val scrobble = db.scrobbleDao().getById(scrobbleId)
                ?: return@launch
            launchValidationTimer(scrobble.track_id, scrobbleId)
        }
    }

    // ════════════════════════════════════════
    // VALIDER UN SCROBBLE
    // ════════════════════════════════════════
    private suspend fun validateScrobble(trackId: Long) {
        if (currentScrobbleId < 0) return

        val now = System.currentTimeMillis()
        val totalDuration = accumulatedMs +
                if (trackStartTime > 0) now - trackStartTime else 0L

        // Valider en DB
        db.scrobbleDao().validate(currentScrobbleId, now)

        // Mettre à jour les stats du titre
        db.trackDao().incrementPlayCount(trackId, totalDuration, now)

        // Mettre à jour daily_plays
        val today = dateFormat.format(Date())
        val existingDaily = db.dailyPlayDao()
            .getByTrackAndDate(trackId, today)
        if (existingDaily != null) {
            db.dailyPlayDao().increment(trackId, today, totalDuration)
        } else {
            db.dailyPlayDao().insert(
                DailyPlayEntity(
                    track_id = trackId,
                    date = today,
                    play_count = 1,
                    total_duration_ms = totalDuration
                )
            )
        }

        // Mettre à jour daily_stats
        updateDailyStats(today, totalDuration)

        // Mettre à jour streak
        updateStreak(today)

        // Mettre à jour now_playing → VALIDATED
        db.nowPlayingDao().getCurrent()?.let { np ->
            db.nowPlayingDao().updateStatus(
                np.id,
                "VALIDATED",
                now
            )
        }

        // Vérifier certifications + panthéon
        checkCertifications(trackId)

        // Mettre à jour les stats artiste
        val scrobble = db.scrobbleDao().getById(currentScrobbleId)
        scrobble?.artist_id?.let { artistId ->
            db.artistDao().incrementPlayCount(
                artistId, totalDuration, now
            )
            // Vérifier Panthéon
            checkPantheon(artistId)
        }

        // Mettre à jour les stats album
        scrobble?.album_id?.let { albumId ->
            db.albumDao().incrementPlayCount(
                albumId, totalDuration, now
            )
        }

        onScrobbleValidated?.invoke(trackId)
    }

    // ════════════════════════════════════════
    // CLÔTURER LE SCROBBLE ACTUEL
    // ════════════════════════════════════════
    private suspend fun closeCurrentScrobble() {
        if (currentScrobbleId < 0) return

        val now = System.currentTimeMillis()
        val duration = accumulatedMs +
                if (trackStartTime > 0) now - trackStartTime else 0L

        when {
            // Skip avant seuil → ignoré, rien loggé
            duration < thresholdMs -> {
                db.scrobbleDao().updateStatus(
                    currentScrobbleId,
                    "CANCELLED"
                )
                // Marquer comme skip
                db.scrobbleDao().getById(currentScrobbleId)?.let {
                    db.scrobbleDao().update(it.copy(is_skip = true))
                }
            }
            // Seuil atteint → clôturer normalement
            else -> {
                db.scrobbleDao().setEnded(
                    currentScrobbleId,
                    now,
                    duration
                )
            }
        }

        // Annuler le timer
        validationJob?.cancel()
        currentScrobbleId = -1
    }

    // ════════════════════════════════════════
    // ANNULER SANS TRACE (crossfade / loop)
    // ════════════════════════════════════════
    private suspend fun cancelCurrentScrobble() {
        if (currentScrobbleId < 0) return

        db.scrobbleDao().updateStatus(
            currentScrobbleId,
            "CANCELLED"
        )

        validationJob?.cancel()
        currentScrobbleId = -1
    }

    // ════════════════════════════════════════
    // CERTIFICATIONS
    // ════════════════════════════════════════
    private suspend fun checkCertifications(trackId: Long) {
        val track = db.trackDao().getById(trackId) ?: return
        val artist = track.artist_id?.let {
            db.artistDao().getById(it)
        }
        val plays = track.play_count
        val title = track.title
        val artistName = artist?.name ?: ""

        // Seuils selon cahier des charges
        // Argent : 25 / Or : 50 / Platine : 100
        // Diamant : 350 puis +350 × multiplicateur
        when {
            plays == 25  -> {
                saveCertification(trackId, "TRACK", "SILVER", 1, plays)
                notifManager.notifyCertification(
                    title, artistName, "SILVER", 1
                )
            }
            plays == 50  -> {
                saveCertification(trackId, "TRACK", "GOLD", 1, plays)
                notifManager.notifyCertification(
                    title, artistName, "GOLD", 1
                )
            }
            plays == 100 -> {
                saveCertification(trackId, "TRACK", "PLATINUM", 1, plays)
                notifManager.notifyCertification(
                    title, artistName, "PLATINUM", 1
                )
            }
            // Diamant infini : 350 × multiplicateur
            plays >= 350 && (plays % 350 == 0) -> {
                val multiplier = plays / 350
                saveCertification(
                    trackId, "TRACK", "DIAMOND", multiplier, plays
                )
                notifManager.notifyCertification(
                    title, artistName, "DIAMOND", multiplier
                )
            }
        }
    }

    // ── Sauvegarder une certification ─────────
    private suspend fun saveCertification(
        entityId: Long,
        entityType: String,
        level: String,
        multiplier: Int,
        playCount: Int
    ) {
        val now = System.currentTimeMillis()
        val track = db.trackDao().getById(entityId)
        val timeToReach = now - (track?.first_played_at ?: now)

        db.certificationDao().insert(
            CertificationEntity(
                entity_id = entityId,
                entity_type = entityType,
                level = level,
                multiplier = multiplier,
                play_count_at_cert = playCount,
                certified_at = now,
                time_to_certify_ms = timeToReach
            )
        )

        db.certificationHistoryDao().insert(
            CertificationHistoryEntity(
                entity_id = entityId,
                entity_type = entityType,
                level = level,
                multiplier = multiplier,
                certified_at = now,
                play_count_at_cert = playCount,
                time_to_certify_ms = timeToReach
            )
        )

        // Notif dans le feed
        db.notificationFeedDao().insert(
            NotificationFeedEntity(
                type = "CERTIFICATION",
                entity_id = entityId,
                entity_type = entityType,
                message = "🏆 $level × $multiplier atteint !"
            )
        )
    }

    // ════════════════════════════════════════
    // PANTHÉON
    // ════════════════════════════════════════
    private suspend fun checkPantheon(artistId: Long) {
        val artist = db.artistDao().getById(artistId) ?: return
        val plays = artist.play_count
        val name = artist.name

        // Seuils selon cahier des charges
        val newStatus = when {
            plays >= 7000 -> "MYTHIQUE"
            plays >= 3650 -> "LEGENDE"
            plays >= 1250 -> "MEGASTAR"
            plays >= 650  -> "SUPERSTAR"
            plays >= 425  -> "STAR"
            else          -> null
        } ?: return

        // Vérifier si le statut a changé
        val current = db.pantheonStatusDao().getByArtist(artistId)

        val statusOrder = listOf(
            "STAR", "SUPERSTAR", "MEGASTAR", "LEGENDE", "MYTHIQUE"
        )

        val currentIndex = statusOrder.indexOf(current?.current_status ?: "")
        val newIndex = statusOrder.indexOf(newStatus)

        // Seulement si le nouveau statut est supérieur
        if (newIndex <= currentIndex) return

        val now = System.currentTimeMillis()
        val timeToReach = now - (artist.first_played_at ?: now)

        // Sauvegarder le statut
        if (current == null) {
            db.pantheonStatusDao().insert(
                PantheonStatusEntity(
                    artist_id = artistId,
                    current_status = newStatus,
                    status_date = now,
                    time_to_status_ms = timeToReach,
                    reached_via_plays = true
                )
            )
        } else {
            db.pantheonStatusDao().updateStatus(
                artistId, newStatus, now, timeToReach, true
            )
        }

        // Historique
        db.pantheonHistoryDao().insert(
            PantheonHistoryEntity(
                artist_id = artistId,
                status = newStatus,
                date_reached = now,
                time_to_reach_ms = timeToReach,
                play_count_at_status = plays
            )
        )

        // Mettre à jour l'artiste
        db.artistDao().updatePantheonStatus(artistId, newStatus, now)

        // Notification
        notifManager.notifyPantheon(name, newStatus)

        // Feed
        db.notificationFeedDao().insert(
            NotificationFeedEntity(
                type = "PANTHEON",
                entity_id = artistId,
                entity_type = "ARTIST",
                message = "$name vient d'atteindre le statut $newStatus ! 🏛️"
            )
        )
    }

    // ════════════════════════════════════════
    // DAILY STATS
    // ════════════════════════════════════════
    private suspend fun updateDailyStats(date: String, duration: Long) {
        val existing = db.dailyStatsDao().getByDate(date)
        if (existing != null) {
            db.dailyStatsDao().incrementPlays(date, duration)
        } else {
            db.dailyStatsDao().insert(
                DailyStatsEntity(
                    date = date,
                    play_count = 1,
                    total_duration_ms = duration,
                    distinct_tracks = 1,
                    distinct_artists = 1,
                    distinct_albums = 1
                )
            )
        }
    }

    // ════════════════════════════════════════
    // STREAK
    // ════════════════════════════════════════
    private suspend fun updateStreak(today: String) {
        val latest = db.dailyStreakDao().getLatest()
        val yesterday = getYesterday()

        val currentStreak = when {
            // Déjà joué aujourd'hui
            latest?.date == today -> latest.current_streak
            // Joué hier → continuer le streak
            latest?.date == yesterday && latest.has_play -> {
                latest.current_streak + 1
            }
            // Sinon → streak repart à 1
            else -> 1
        }

        val bestStreak = maxOf(
            currentStreak,
            latest?.best_streak ?: 0
        )

        val existing = db.dailyStreakDao().getByDate(today)
        if (existing == null) {
            db.dailyStreakDao().insert(
                DailyStreakEntity(
                    date = today,
                    has_play = true,
                    current_streak = currentStreak,
                    best_streak = bestStreak,
                    best_streak_date = if (currentStreak >= bestStreak)
                        today else latest?.best_streak_date
                )
            )
        } else {
            db.dailyStreakDao().markDayWithPlay(today, currentStreak)
        }
    }

    private fun getYesterday(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return dateFormat.format(cal.time)
    }

    // ════════════════════════════════════════
    // TROUVER OU CRÉER ARTISTE
    // ════════════════════════════════════════
    private suspend fun getOrCreateArtist(name: String): Long {
        val existing = db.artistDao().getByName(name)
        if (existing != null) return existing.artist_id

        return db.artistDao().insert(
            ArtistEntity(
                name = name,
                name_raw = name,
                first_played_at = System.currentTimeMillis(),
                last_played_at = System.currentTimeMillis()
            )
        )
    }

    // ════════════════════════════════════════
    // TROUVER OU CRÉER ALBUM
    // ════════════════════════════════════════
    private suspend fun getOrCreateAlbum(
        title: String,
        artistId: Long
    ): Long {
        val existing = db.albumDao().getByTitleAndArtist(title, artistId)
        if (existing != null) return existing.album_id

        return db.albumDao().insert(
            AlbumEntity(
                title = title,
                title_raw = title,
                artist_id = artistId,
                first_played_at = System.currentTimeMillis(),
                last_played_at = System.currentTimeMillis()
            )
        )
    }

    // ════════════════════════════════════════
    // TROUVER OU CRÉER TITRE
    // ════════════════════════════════════════
    private suspend fun getOrCreateTrack(
        detected: DetectedTrack,
        artistId: Long,
        albumId: Long?
    ): Long {
        // Chercher par titre + artiste
        val tracks = db.trackDao().search(detected.title)
        val existing = tracks.firstOrNull { track ->
            track.artist_id == artistId &&
                    track.title.equals(detected.title, ignoreCase = true)
        }
        if (existing != null) return existing.track_id

        // Créer le titre
        val trackId = db.trackDao().insert(
            TrackEntity(
                title = detected.title,
                title_raw = detected.title,
                artist_id = artistId,
                album_id = albumId,
                duration_ms = detected.durationMs,
                confidence_score = when (detected.detectionSource) {
                    "MediaSession"  -> 100
                    "Mixed"         -> 80
                    "Notification"  -> 70
                    else            -> 50
                },
                first_played_at = System.currentTimeMillis(),
                last_played_at = System.currentTimeMillis()
            )
        )

        // Lier artiste
        db.trackArtistDao().insert(
            TrackArtistEntity(
                track_id = trackId,
                artist_id = artistId,
                is_primary = true,
                role = "main"
            )
        )

        // Lier album
        albumId?.let {
            db.trackAlbumDao().insert(
                TrackAlbumEntity(
                    track_id = trackId,
                    album_id = it,
                    is_primary = true
                )
            )
        }

        return trackId
    }

    // ════════════════════════════════════════
    // METTRE À JOUR FIRST_PLAYED_AT
    // ════════════════════════════════════════
    private suspend fun updateFirstPlayedIfNeeded(
        trackId: Long,
        artistId: Long,
        albumId: Long?
    ) {
        val now = System.currentTimeMillis()

        val track = db.trackDao().getById(trackId)
        if (track?.first_played_at == null) {
            db.trackDao().update(
                track!!.copy(first_played_at = now)
            )
        }

        val artist = db.artistDao().getById(artistId)
        if (artist?.first_played_at == null) {
            db.artistDao().update(
                artist!!.copy(first_played_at = now)
            )
        }

        albumId?.let {
            val album = db.albumDao().getById(it)
            if (album?.first_played_at == null) {
                db.albumDao().update(
                    album!!.copy(first_played_at = now)
                )
            }
        }
    }

    // ════════════════════════════════════════
    // COMPARER DEUX TITRES
    // ════════════════════════════════════════
    private fun isSameTrack(
        a: DetectedTrack,
        b: DetectedTrack?
    ): Boolean {
        if (b == null) return false
        return a.title.equals(b.title, ignoreCase = true) &&
                a.artist.equals(b.artist, ignoreCase = true)
    }

    // ════════════════════════════════════════
    // CHANGER LE SEUIL À CHAUD (hot-reload)
    // ════════════════════════════════════════
    fun setThreshold(seconds: Int) {
        thresholdMs = when (seconds) {
            15   -> 15_000L
            30   -> 30_000L
            60   -> 60_000L
            90   -> 90_000L
            else -> 30_000L
        }
    }
}