package com.novastats.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novastats.app.data.db.NovaDatabase
import com.novastats.app.data.db.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// ════════════════════════════════════════════
// UI STATE
// ════════════════════════════════════════════
data class HomeUiState(
    // Section 1 — En cours
    val nowPlaying: NowPlayingEntity? = null,
    val nowPlayingTrack: TrackEntity? = null,
    val nowPlayingArtist: ArtistEntity? = null,

    // Section 2 — Aujourd'hui
    val todayPlays: Int = 0,
    val todayDurationMs: Long = 0,
    val todayArtists: Int = 0,
    val todayAlbums: Int = 0,
    val todayTracks: Int = 0,

    // Section 3 — Top du moment
    val topTrack: TrackEntity? = null,
    val topArtist: ArtistEntity? = null,
    val topAlbum: AlbumEntity? = null,

    // Section 4 — Actualités
    val recentNotifications: List<NotificationFeedEntity> = emptyList(),

    // Section 5 — Prochaines certifications
    val nextCerts: List<CertificationEntity> = emptyList(),

    // Section 6 — Records récents
    val recentRecords: List<RecordCacheEntity> = emptyList(),

    // Section 7 — Récemment écouté
    val recentScrobbles: List<ScrobbleWithTrack> = emptyList(),

    // Section 8 — Streak
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,

    // Statut service
    val isServiceActive: Boolean = false,

    // Loading
    val isLoading: Boolean = true
)

data class ScrobbleWithTrack(
    val scrobble: ScrobbleEntity,
    val track: TrackEntity?,
    val artist: ArtistEntity?
)

// ════════════════════════════════════════════
// VIEWMODEL
// ════════════════════════════════════════════
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val db: NovaDatabase
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today get() = dateFormat.format(Date())

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeNowPlaying()
        observeTodayStats()
        observeTopMoment()
        observeNotifications()
        observeStreak()
        loadRecentScrobbles()
    }

    // ── Section 1 — En cours ─────────────────
    private fun observeNowPlaying() {
        viewModelScope.launch {
            db.nowPlayingDao().getCurrentFlow().collect { nowPlaying ->
                val track = nowPlaying?.let {
                    db.trackDao().getById(it.track_id)
                }
                val artist = track?.artist_id?.let {
                    db.artistDao().getById(it)
                }
                _uiState.update { state ->
                    state.copy(
                        nowPlaying = nowPlaying,
                        nowPlayingTrack = track,
                        nowPlayingArtist = artist
                    )
                }
            }
        }
    }

    // ── Section 2 — Aujourd'hui ───────────────
    private fun observeTodayStats() {
        viewModelScope.launch {
            db.dailyStatsDao().getByDateFlow(today).collect { stats ->
                _uiState.update { state ->
                    state.copy(
                        todayPlays = stats?.play_count ?: 0,
                        todayDurationMs = stats?.total_duration_ms ?: 0,
                        todayArtists = stats?.distinct_artists ?: 0,
                        todayAlbums = stats?.distinct_albums ?: 0,
                        todayTracks = stats?.distinct_tracks ?: 0,
                        isLoading = false
                    )
                }
            }
        }
    }

    // ── Section 3 — Top du moment ─────────────
    private fun observeTopMoment() {
        viewModelScope.launch {
            db.trackDao().getTopTracks(1).collect { tracks ->
                val topTrack = tracks.firstOrNull()
                val topArtist = topTrack?.artist_id?.let {
                    db.artistDao().getById(it)
                }
                val topAlbum = topTrack?.album_id?.let {
                    db.albumDao().getById(it)
                }
                _uiState.update { state ->
                    state.copy(
                        topTrack = topTrack,
                        topArtist = topArtist,
                        topAlbum = topAlbum
                    )
                }
            }
        }
    }

    // ── Section 4 — Notifications ─────────────
    private fun observeNotifications() {
        viewModelScope.launch {
            db.notificationFeedDao().getRecent(5).collect { notifs ->
                _uiState.update { state ->
                    state.copy(recentNotifications = notifs)
                }
            }
        }
    }

    // ── Section 7 — Récemment écouté ──────────
    private fun loadRecentScrobbles() {
        viewModelScope.launch {
            db.scrobbleDao().getRecent(10).collect { scrobbles ->
                val withTracks = scrobbles.map { scrobble ->
                    val track = db.trackDao().getById(scrobble.track_id)
                    val artist = track?.artist_id?.let {
                        db.artistDao().getById(it)
                    }
                    ScrobbleWithTrack(scrobble, track, artist)
                }
                _uiState.update { state ->
                    state.copy(recentScrobbles = withTracks)
                }
            }
        }
    }

    // ── Section 8 — Streak ────────────────────
    private fun observeStreak() {
        viewModelScope.launch {
            db.dailyStreakDao().getRecent(1).collect { streaks ->
                val latest = streaks.firstOrNull()
                _uiState.update { state ->
                    state.copy(
                        currentStreak = latest?.current_streak ?: 0,
                        bestStreak = latest?.best_streak ?: 0
                    )
                }
            }
        }
    }

    // ── Utilitaires ───────────────────────────
    fun formatDuration(ms: Long): String {
        val hours = ms / 3_600_000
        val minutes = (ms % 3_600_000) / 60_000
        return if (hours > 0) "${hours}h ${minutes}min"
        else "${minutes}min"
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}