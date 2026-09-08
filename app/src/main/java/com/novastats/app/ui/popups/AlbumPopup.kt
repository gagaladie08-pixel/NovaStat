package com.novastats.app.ui.popups

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// ════════════════════════════════════════════
// UI STATE
// ════════════════════════════════════════════
data class AlbumPopupState(
    val album: AlbumEntity? = null,
    val artist: ArtistEntity? = null,
    val tracks: List<TrackEntity> = emptyList(),
    val certifications: List<CertificationEntity> = emptyList(),
    val isLoading: Boolean = true
)

// ════════════════════════════════════════════
// VIEWMODEL
// ════════════════════════════════════════════
@HiltViewModel
class AlbumPopupViewModel @Inject constructor(
    private val db: NovaDatabase
) : ViewModel() {

    private val _state = MutableStateFlow(AlbumPopupState())
    val state: StateFlow<AlbumPopupState> = _state.asStateFlow()

    fun load(albumId: Long) {
        viewModelScope.launch {
            val album = db.albumDao().getById(albumId)
            val artist = album?.artist_id?.let {
                db.artistDao().getById(it)
            }

            // Titres de l'album
            val trackAlbums = db.trackAlbumDao().getByAlbum(albumId)
            val tracks = trackAlbums.mapNotNull { ta ->
                db.trackDao().getById(ta.track_id)
            }.sortedByDescending { it.play_count }

            // Certifications album
            val certs = db.certificationDao()
                .getByEntity(albumId, "ALBUM")

            _state.value = AlbumPopupState(
                album = album,
                artist = artist,
                tracks = tracks,
                certifications = certs,
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

    fun getCertLabel(level: String, multiplier: Int): String {
        return when (level) {
            "SILVER"   -> "🥉 Argent"
            "GOLD"     -> "🥈 Or"
            "PLATINUM" -> "🥇 Platine"
            "DIAMOND"  -> "${"💎".repeat(
                multiplier.coerceAtMost(3)
            )} ${if (multiplier > 1) "${multiplier}x " else ""}Diamant"
            else -> level
        }
    }

    fun getCertColor(level: String): Long {
        return when (level) {
            "SILVER"   -> 0xFFC0C0C0
            "GOLD"     -> 0xFFFFD700
            "PLATINUM" -> 0xFFE5E4E2
            "DIAMOND"  -> 0xFF00FFFF
            else       -> 0xFFFFFFFF
        }
    }
}

// ════════════════════════════════════════════
// ALBUM POPUP
// ════════════════════════════════════════════
@Composable
fun AlbumPopup(
    albumId: Long,
    onDismiss: () -> Unit,
    viewModel: AlbumPopupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val theme = LocalNovaTheme.current

    LaunchedEffect(albumId) {
        viewModel.load(albumId)
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
                                    theme.secondary.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        theme.secondary.copy(alpha = 0.3f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "💿", fontSize = 48.sp)
                            }
                        }

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            // ── Titre + Artiste ────────
                            Text(
                                text = state.album?.title ?: "—",
                                color = theme.text,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = state.artist?.name ?: "—",
                                color = theme.textSecondary,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            state.album?.release_date?.let {
                                Text(
                                    text = "📅 $it",
                                    color = theme.textSecondary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // ── Certifications ─────────
                            if (state.certifications.isNotEmpty()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                ) {
                                    state.certifications.forEach { cert ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    androidx.compose.ui.graphics.Color(
                                                        viewModel.getCertColor(cert.level)
                                                    ).copy(alpha = 0.2f)
                                                )
                                                .padding(
                                                    horizontal = 10.dp,
                                                    vertical = 4.dp
                                                )
                                        ) {
                                            Text(
                                                text = viewModel.getCertLabel(
                                                    cert.level,
                                                    cert.multiplier
                                                ),
                                                color = androidx.compose.ui.graphics.Color(
                                                    viewModel.getCertColor(cert.level)
                                                ),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // ── Stats ──────────────────
                            PopupStatGrid(
                                stats = listOf(
                                    "🎵" to "${state.album?.play_count ?: 0} écoutes",
                                    "⏱️" to viewModel.formatDuration(
                                        state.album?.total_duration_ms ?: 0
                                    ),
                                    "🎶" to "${state.tracks.size} titres",
                                    "📅" to viewModel.formatDate(
                                        state.album?.first_played_at
                                    ),
                                    "🔄" to viewModel.formatDate(
                                        state.album?.last_played_at
                                    ),
                                    "✅" to "${state.album?.distinct_tracks_played ?: 0} écoutés"
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // ── Liste titres ───────────
                            if (state.tracks.isNotEmpty()) {
                                PopupSection(title = "🎵 Titres") {
                                    state.tracks.forEach { track ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = track.title,
                                                color = theme.text,
                                                fontSize = 13.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "${track.play_count} écoutes",
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
                                    .background(theme.secondary)
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