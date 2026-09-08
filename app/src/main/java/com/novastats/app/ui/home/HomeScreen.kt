package com.novastats.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.novastats.app.core.theme.LocalNovaTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val theme = LocalNovaTheme.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ── Section 1 — En cours ──────────────
        item {
            SectionNowPlaying(
                state = state,
                viewModel = viewModel
            )
        }

        // ── Section 2 — Aujourd'hui ───────────
        item {
            SectionToday(state = state, viewModel = viewModel)
        }

        // ── Section 3 — Top du moment ─────────
        item {
            SectionTopMoment(state = state)
        }

        // ── Section 4 — Actualités ────────────
        if (state.recentNotifications.isNotEmpty()) {
            item {
                SectionNews(state = state, viewModel = viewModel)
            }
        }

        // ── Section 7 — Récemment écouté ──────
        if (state.recentScrobbles.isNotEmpty()) {
            item {
                SectionRecentlyPlayed(
                    state = state,
                    viewModel = viewModel
                )
            }
        }

        // ── Section 8 — Streak ────────────────
        item {
            SectionStreak(state = state)
        }

        // Espace bas
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// ════════════════════════════════════════════
// SECTION 1 — EN COURS DE LECTURE
// ════════════════════════════════════════════
@Composable
fun SectionNowPlaying(
    state: HomeUiState,
    viewModel: HomeViewModel
) {
    val theme = LocalNovaTheme.current

    NovaCard {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Titre section
            SectionTitle(
                text = "🎵 En cours de lecture",
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (state.nowPlaying == null) {
                // État vide
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🎧",
                            fontSize = 32.sp
                        )
                        Text(
                            text = "En attente de ta première écoute...",
                            color = theme.textSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                // Titre + artiste
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.nowPlayingTrack?.title ?: "Titre inconnu",
                            color = theme.text,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.nowPlayingArtist?.name ?: "Artiste inconnu",
                            color = theme.textSecondary,
                            fontSize = 13.sp
                        )
                    }

                    // Status scrobble
                    val statusColor = if (
                        state.nowPlaying.scrobble_status == "VALIDATED"
                    ) theme.accent else theme.primary

                    Text(
                        text = if (
                            state.nowPlaying.scrobble_status == "VALIDATED"
                        ) "✅ VALIDÉ" else "⏳ EN COURS",
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // App source
                Text(
                    text = "📱 ${state.nowPlaying.source_app ?: "Inconnu"}",
                    color = theme.textSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ════════════════════════════════════════════
// SECTION 2 — AUJOURD'HUI
// ════════════════════════════════════════════
@Composable
fun SectionToday(
    state: HomeUiState,
    viewModel: HomeViewModel
) {
    val theme = LocalNovaTheme.current

    NovaCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            SectionTitle(
                text = "📅 Aujourd'hui",
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    emoji = "🎵",
                    value = state.todayPlays.toString(),
                    label = "Écoutes"
                )
                StatItem(
                    emoji = "⏱️",
                    value = viewModel.formatDuration(state.todayDurationMs),
                    label = "Durée"
                )
                StatItem(
                    emoji = "🎤",
                    value = state.todayArtists.toString(),
                    label = "Artistes"
                )
                StatItem(
                    emoji = "💿",
                    value = state.todayAlbums.toString(),
                    label = "Albums"
                )
            }
        }
    }
}

// ════════════════════════════════════════════
// SECTION 3 — TOP DU MOMENT
// ════════════════════════════════════════════
@Composable
fun SectionTopMoment(state: HomeUiState) {
    val theme = LocalNovaTheme.current

    NovaCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            SectionTitle(
                text = "🔥 Top du moment",
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (state.topTrack == null) {
                Text(
                    text = "Écoute de la musique pour voir ton top !",
                    color = theme.textSecondary,
                    fontSize = 13.sp
                )
            } else {
                // Top titre
                TopItem(
                    emoji = "🎵",
                    label = "Titre",
                    name = state.topTrack.title,
                    plays = state.topTrack.play_count
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Top artiste
                state.topArtist?.let {
                    TopItem(
                        emoji = "🎤",
                        label = "Artiste",
                        name = it.name,
                        plays = it.play_count
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Top album
                state.topAlbum?.let {
                    TopItem(
                        emoji = "💿",
                        label = "Album",
                        name = it.title,
                        plays = it.play_count
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════
// SECTION 4 — ACTUALITÉS
// ════════════════════════════════════════════
@Composable
fun SectionNews(
    state: HomeUiState,
    viewModel: HomeViewModel
) {
    val theme = LocalNovaTheme.current

    NovaCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            SectionTitle(
                text = "📰 Dernières actualités",
                modifier = Modifier.padding(bottom = 12.dp)
            )

            state.recentNotifications.forEach { notif ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = notif.message,
                        color = theme.text,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = viewModel.formatTime(notif.created_at),
                        color = theme.textSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════
// SECTION 7 — RÉCEMMENT ÉCOUTÉ
// ════════════════════════════════════════════
@Composable
fun SectionRecentlyPlayed(
    state: HomeUiState,
    viewModel: HomeViewModel
) {
    val theme = LocalNovaTheme.current

    NovaCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            SectionTitle(
                text = "🕐 Récemment écouté",
                modifier = Modifier.padding(bottom = 12.dp)
            )

            state.recentScrobbles.take(5).forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.track?.title ?: "Titre inconnu",
                            color = theme.text,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = item.artist?.name ?: "Artiste inconnu",
                            color = theme.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = viewModel.formatTime(item.scrobble.started_at),
                        color = theme.textSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════
// SECTION 8 — STREAK
// ════════════════════════════════════════════
@Composable
fun SectionStreak(state: HomeUiState) {
    val theme = LocalNovaTheme.current

    NovaCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                SectionTitle(text = "🔥 Streak actuel")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${state.currentStreak} jours consécutifs",
                    color = theme.text,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "🏆 Record : ${state.bestStreak} jours",
                    color = theme.textSecondary,
                    fontSize = 12.sp
                )
            }

            Text(
                text = if (state.currentStreak > 0) "🔥" else "💤",
                fontSize = 48.sp
            )
        }
    }
}

// ════════════════════════════════════════════
// COMPOSANTS RÉUTILISABLES
// ════════════════════════════════════════════

@Composable
fun NovaCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val theme = LocalNovaTheme.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(theme.surface)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    val theme = LocalNovaTheme.current

    Text(
        text = text,
        color = theme.primary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        modifier = modifier
    )
}

@Composable
fun StatItem(
    emoji: String,
    value: String,
    label: String
) {
    val theme = LocalNovaTheme.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = emoji, fontSize = 20.sp)
        Text(
            text = value,
            color = theme.text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = theme.textSecondary,
            fontSize = 10.sp
        )
    }
}

@Composable
fun TopItem(
    emoji: String,
    label: String,
    name: String,
    plays: Int
) {
    val theme = LocalNovaTheme.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Column {
                Text(
                    text = label,
                    color = theme.textSecondary,
                    fontSize = 10.sp
                )
                Text(
                    text = name,
                    color = theme.text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Text(
            text = "$plays écoutes",
            color = theme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}