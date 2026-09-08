package com.novastats.app.ui.popups

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novastats.app.core.theme.LocalNovaTheme
import com.novastats.app.data.db.NovaDatabase
import com.novastats.app.data.db.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// ════════════════════════════════════════════
// UI STATE
// ════════════════════════════════════════════
data class ArtistPopupState(
    val artist: ArtistEntity? = null,
    val topTracks: List<TrackEntity> = emptyList(),
    val topAlbums: List<AlbumEntity> = emptyList(),
    val pantheonStatus: PantheonStatusEntity? = null,
    val isLoading: Boolean = true
)

// ════════════════════════════════════════════
// VIEWMODEL
// ════════════════════════════════════════════
@HiltViewModel
class ArtistPopupViewModel @Inject constructor(
    private val db: NovaDatabase
) : ViewModel() {

    private val _state = MutableStateFlow(ArtistPopupState())
    val state: StateFlow<ArtistPopupState> = _state.asStateFlow()

    fun load(artistId: Long) {
        viewModelScope.launch {
            val artist = db.artistDao().getById(artistId)

            // Top tracks de cet artiste
            val allTracks = db.trackDao()
                .getTopTracks(300)
                .first()
            val topTracks = allTracks
                .filter { it.artist_id == artistId }
                .take(5)

            // Albums de cet artiste
            val topAlbums = db.albumDao()
                .getByArtist(artistId)
                .take(5)

            // Statut Panthéon
            val pantheon = db.pantheonStatusDao()
                .getByArtist(artistId)

            _state.value = ArtistPopupState(
                artist = artist,
                topTracks = topTracks,
                topAlbums = topAlbums,
                pantheonStatus = pantheon,
                isLoading = false
            )
        }
    }

    fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return "—"
        return SimpleDateFormat("dd MMM yyyy", Locale.FRENCH)
            .format(Date(timestamp))
    }

    fun formatDuration(ms: Long): String {
        val hours = ms / 3_600_000
        val minutes = (ms % 3_600_000) / 60_000
        return if (hours > 0) "${hours}h ${minutes}min"
        else "${minutes}min"
    }

    fun getPantheonLabel(status: String?): String {
        return when (status) {
            "STAR"      -> "⭐ Star"
            "SUPERSTAR" -> "🌟 Superstar"
            "MEGASTAR"  -> "👑 Megastar"
            "LEGENDE"   -> "🏛️ Légende"
            "MYTHIQUE"  -> "✨ Mythique"
            else        -> ""
        }
    }

    fun getPantheonColor(status: String?): Long {
        return when (status) {
            "STAR"      -> 0xFF4A90E2
            "SUPERSTAR" -> 0xFF9B59B6
            "MEGASTAR"  -> 0xFFFFD700
            "LEGENDE"   -> 0xFFC0392B
            "MYTHIQUE"  -> 0xFFFF69B4
            else        -> 0xFFFFFFFF
        }
    }
}

// ════════════════════════════════════════════
// ARTIST POPUP
// ════════════════════════════════════════════
@Composable
fun ArtistPopup(
    artistId: Long,
    onDismiss: () -> Unit,
    viewModel: ArtistPopupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val theme = LocalNovaTheme.current

    LaunchedEffect(artistId) {
        viewModel.load(artistId)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Color.Black
                        .copy(alpha = 0.85f)
                )
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(theme.surface)
                    .clickable(enabled = false) {}
            ) {
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⏳", fontSize = 32.sp)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {

                        // ── Bannière ──────────────────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(
                                    theme.glow.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(
                                        theme.glow.copy(alpha = 0.3f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🎤", fontSize = 40.sp)
                            }
                        }

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            // ── Nom ────────────────────
                            Text(
                                text = state.artist?.name?.uppercase() ?: "—",
                                color = theme.text,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            // ── Badge Panthéon ─────────
                            state.pantheonStatus?.let { pantheon ->
                                Box(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            androidx.compose.ui.graphics.Color(
                                                viewModel.getPantheonColor(
                                                    pantheon.current_status
                                                )
                                            ).copy(alpha = 0.2f)
                                        )
                                        .padding(
                                            horizontal = 12.dp,
                                            vertical = 4.dp
                                        )
                                ) {
                                    Text(
                                        text = viewModel.getPantheonLabel(
                                            pantheon.current_status
                                        ),
                                        color = androidx.compose.ui.graphics.Color(
                                            viewModel.getPantheonColor(
                                                pantheon.current_status
                                            )
                                        ),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // ── Stats ──────────────────
                            PopupStatGrid(
                                stats = listOf(
                                    "🎵" to "${state.artist?.play_count ?: 0} écoutes",
                                    "⏱️" to viewModel.formatDuration(
                                        state.artist?.total_duration_ms ?: 0
                                    ),
                                    "🎶" to "${state.artist?.distinct_tracks ?: 0} titres",
                                    "💿" to "${state.artist?.distinct_albums ?: 0} albums",
                                    "📅" to viewModel.formatDate(
                                        state.artist?.first_played_at
                                    ),
                                    "🔄" to viewModel.formatDate(
                                        state.artist?.last_played_at
                                    )
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // ── Top chansons ───────────
                            if (state.topTracks.isNotEmpty()) {
                                PopupSection(title = "🎵 Top chansons") {
                                    state.topTracks.forEachIndexed { i, track ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement =
                                                Arrangement.SpaceBetween,
                                            verticalAlignment =
                                                Alignment.CenterVertically
                                        ) {
                                            Row(
                                                horizontalArrangement =
                                                    Arrangement.spacedBy(8.dp),
                                                verticalAlignment =
                                                    Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = when (i) {
                                                        0 -> "🥇"
                                                        1 -> "🥈"
                                                        2 -> "🥉"
                                                        else -> "#${i + 1}"
                                                    },
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = track.title,
                                                    color = theme.text,
                                                    fontSize = 13.sp,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                )
                                            }
                                            Text(
                                                text = "${track.play_count}",
                                                color = theme.primary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // ── Top albums ─────────────
                            if (state.topAlbums.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                PopupSection(title = "💿 Albums") {
                                    state.topAlbums.forEach { album ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement =
                                                Arrangement.SpaceBetween,
                                            verticalAlignment =
                                                Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = album.title,
                                                color = theme.text,
                                                fontSize = 13.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "${album.play_count} écoutes",
                                                color = theme.primary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // ── Bouton fermer ──────────
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(theme.glow)
                                    .clickable { onDismiss() }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "FERMER",
                                    color = theme.background,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}