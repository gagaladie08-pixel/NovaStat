package com.novastats.app.ui.stats

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
// PÉRIODES
// ════════════════════════════════════════════
enum class StatsPeriod(val label: String) {
    DAILY("Jour"),
    WEEKLY("Semaine"),
    MONTHLY("Mois"),
    YEARLY("Année"),
    GLOBAL("Global")
}

// ════════════════════════════════════════════
// CATÉGORIES
// ════════════════════════════════════════════
enum class StatsCategory(val label: String) {
    TRACKS("Titres"),
    ARTISTS("Artistes"),
    ALBUMS("Albums")
}

// ════════════════════════════════════════════
// UI STATE
// ════════════════════════════════════════════
data class StatsUiState(
    val period: StatsPeriod = StatsPeriod.GLOBAL,
    val category: StatsCategory = StatsCategory.TRACKS,

    // Bandeau résumé
    val totalPlays: Int = 0,
    val totalDurationMs: Long = 0,
    val distinctTracks: Int = 0,
    val distinctArtists: Int = 0,
    val avgPerDay: Double = 0.0,

    // Classements
    val tracks: List<TrackEntity> = emptyList(),
    val artists: List<ArtistEntity> = emptyList(),
    val albums: List<AlbumEntity> = emptyList(),

    // Recherche
    val searchQuery: String = "",

    val isLoading: Boolean = true
)

// ════════════════════════════════════════════
// VIEWMODEL
// ════════════════════════════════════════════
@HiltViewModel
class StatsViewModel @Inject constructor(
    private val db: NovaDatabase
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    // ── Changer période ───────────────────────
    fun setPeriod(period: StatsPeriod) {
        _uiState.update { it.copy(period = period, isLoading = true) }
        loadStats()
    }

    // ── Changer catégorie ─────────────────────
    fun setCategory(category: StatsCategory) {
        _uiState.update { it.copy(category = category) }
    }

    // ── Recherche ─────────────────────────────
    fun setSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadStats()
    }

    // ── Charger les stats ─────────────────────
    private fun loadStats() {
        viewModelScope.launch {
            val period = _uiState.value.period
            val query = _uiState.value.searchQuery

            when (period) {
                StatsPeriod.GLOBAL -> loadGlobal(query)
                else -> loadPeriod(period, query)
            }
        }
    }

    // ── Stats globales ────────────────────────
    private fun loadGlobal(query: String) {
        viewModelScope.launch {
            // Tracks
            db.trackDao().getTopTracks(300).collect { tracks ->
                val filtered = if (query.isEmpty()) tracks
                else tracks.filter {
                    it.title.contains(query, ignoreCase = true)
                }

                // Artistes
                val artists = db.artistDao()
                    .getTopArtists(300).first()
                    .let { list ->
                        if (query.isEmpty()) list
                        else list.filter {
                            it.name.contains(query, ignoreCase = true)
                        }
                    }

                // Albums
                val albums = db.albumDao()
                    .getTopAlbums(300).first()
                    .let { list ->
                        if (query.isEmpty()) list
                        else list.filter {
                            it.title.contains(query, ignoreCase = true)
                        }
                    }

                // Stats globales
                val totalPlays = db.trackDao().getTotalPlayCount()
                val totalDuration = filtered.sumOf { it.total_duration_ms }
                val daysSinceFirst = getDaysSinceFirstPlay(filtered)

                _uiState.update { state ->
                    state.copy(
                        tracks = filtered,
                        artists = artists,
                        albums = albums,
                        totalPlays = totalPlays,
                        totalDurationMs = totalDuration,
                        distinctTracks = filtered.size,
                        distinctArtists = artists.size,
                        avgPerDay = if (daysSinceFirst > 0)
                            totalPlays.toDouble() / daysSinceFirst
                        else 0.0,
                        isLoading = false
                    )
                }
            }
        }
    }

    // ── Stats par période ─────────────────────
    private fun loadPeriod(period: StatsPeriod, query: String) {
        viewModelScope.launch {
            val (from, to) = getDateRange(period)

            val scrobbles = db.scrobbleDao()
                .getConfirmedBetween(from, to)

            // Regrouper par track
            val trackPlays = scrobbles
                .groupBy { it.track_id }
                .mapValues { it.value.size }
                .toList()
                .sortedByDescending { it.second }
                .take(300)

            val tracks = trackPlays.mapNotNull { (trackId, _) ->
                db.trackDao().getById(trackId)
            }.let { list ->
                if (query.isEmpty()) list
                else list.filter {
                    it.title.contains(query, ignoreCase = true)
                }
            }

            // Regrouper par artiste
            val artistPlays = scrobbles
                .groupBy { it.artist_id }
                .mapValues { it.value.size }
                .toList()
                .sortedByDescending { it.second }
                .take(300)

            val artists = artistPlays.mapNotNull { (artistId, _) ->
                artistId?.let { db.artistDao().getById(it) }
            }.let { list ->
                if (query.isEmpty()) list
                else list.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            }

            // Regrouper par album
            val albumPlays = scrobbles
                .groupBy { it.album_id }
                .mapValues { it.value.size }
                .toList()
                .sortedByDescending { it.second }
                .take(300)

            val albums = albumPlays.mapNotNull { (albumId, _) ->
                albumId?.let { db.albumDao().getById(it) }
            }.let { list ->
                if (query.isEmpty()) list
                else list.filter {
                    it.title.contains(query, ignoreCase = true)
                }
            }

            val totalDuration = scrobbles.sumOf { it.duration_listened_ms }

            _uiState.update { state ->
                state.copy(
                    tracks = tracks,
                    artists = artists,
                    albums = albums,
                    totalPlays = scrobbles.size,
                    totalDurationMs = totalDuration,
                    distinctTracks = tracks.size,
                    distinctArtists = artists.size,
                    isLoading = false
                )
            }
        }
    }

    // ── Plage de dates selon période ──────────
    private fun getDateRange(period: StatsPeriod): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val to = cal.timeInMillis

        when (period) {
            StatsPeriod.DAILY -> cal.add(Calendar.DAY_OF_YEAR, -1)
            StatsPeriod.WEEKLY -> cal.add(Calendar.WEEK_OF_YEAR, -1)
            StatsPeriod.MONTHLY -> cal.add(Calendar.MONTH, -1)
            StatsPeriod.YEARLY -> cal.add(Calendar.YEAR, -1)
            StatsPeriod.GLOBAL -> cal.add(Calendar.YEAR, -10)
        }

        return Pair(cal.timeInMillis, to)
    }

    // ── Jours depuis la première écoute ───────
    private fun getDaysSinceFirstPlay(tracks: List<TrackEntity>): Long {
        val firstPlay = tracks.minOfOrNull { it.first_played_at ?: Long.MAX_VALUE }
            ?: return 1
        val diff = System.currentTimeMillis() - firstPlay
        return maxOf(1, diff / (24 * 60 * 60 * 1000))
    }

    // ── Formater durée ────────────────────────
    fun formatDuration(ms: Long): String {
        val hours = ms / 3_600_000
        val minutes = (ms % 3_600_000) / 60_000
        return if (hours > 0) "${hours}h ${minutes}min"
        else "${minutes}min"
    }

    // ── Position affichage ────────────────────
    fun formatPosition(index: Int): String {
        return when (index) {
            0 -> "🥇"
            1 -> "🥈"
            2 -> "🥉"
            in 3..9 -> "🔥 #${index + 1}"
            else -> "#${index + 1}"
        }
    }
}