package com.novastats.app.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.novastats.app.core.theme.LocalNovaTheme
import com.novastats.app.ui.home.NovaCard
import com.novastats.app.ui.home.SectionTitle

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val theme = LocalNovaTheme.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
    ) {

        // ── Sélecteur de période ──────────────
        PeriodSelector(
            selectedPeriod = state.period,
            onPeriodSelected = { viewModel.setPeriod(it) }
        )

        // ── Bandeau résumé ────────────────────
        StatsBanner(
            state = state,
            viewModel = viewModel
        )

        // ── Sélecteur catégorie ───────────────
        CategorySelector(
            selectedCategory = state.category,
            onCategorySelected = { viewModel.setCategory(it) }
        )

        // ── Barre de recherche ────────────────
        SearchBar(
            query = state.searchQuery,
            onQueryChanged = { viewModel.setSearch(it) }
        )

        // ── Classement ────────────────────────
        when (state.category) {
            StatsCategory.TRACKS -> TracksList(
                state = state,
                viewModel = viewModel
            )
            StatsCategory.ARTISTS -> ArtistsList(
                state = state,
                viewModel = viewModel
            )
            StatsCategory.ALBUMS -> AlbumsList(
                state = state,
                viewModel = viewModel
            )
        }
    }
}

// ════════════════════════════════════════════
// SÉLECTEUR DE PÉRIODE
// ════════════════════════════════════════════
@Composable
fun PeriodSelector(
    selectedPeriod: StatsPeriod,
    onPeriodSelected: (StatsPeriod) -> Unit
) {
    val theme = LocalNovaTheme.current

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(theme.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(StatsPeriod.values().size) { index ->
            val period = StatsPeriod.values()[index]
            val isSelected = period == selectedPeriod

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) theme.primary
                        else theme.background
                    )
                    .clickable { onPeriodSelected(period) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = period.label,
                    color = if (isSelected) theme.background
                    else theme.textSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected)
                        FontWeight.Bold
                    else FontWeight.Normal
                )
            }
        }
    }
}

// ════════════════════════════════════════════
// BANDEAU RÉSUMÉ
// ════════════════════════════════════════════
@Composable
fun StatsBanner(
    state: StatsUiState,
    viewModel: StatsViewModel
) {
    val theme = LocalNovaTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(theme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BannerStat(
            emoji = "🎵",
            value = state.totalPlays.toString(),
            label = "Écoutes"
        )
        BannerStat(
            emoji = "⏱️",
            value = viewModel.formatDuration(state.totalDurationMs),
            label = "Durée"
        )
        BannerStat(
            emoji = "🎶",
            value = state.distinctTracks.toString(),
            label = "Titres"
        )
        BannerStat(
            emoji = "🎤",
            value = state.distinctArtists.toString(),
            label = "Artistes"
        )
        BannerStat(
            emoji = "📅",
            value = String.format("%.1f", state.avgPerDay),
            label = "Moy/jour"
        )
    }
}

@Composable
fun BannerStat(
    emoji: String,
    value: String,
    label: String
) {
    val theme = LocalNovaTheme.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = emoji, fontSize = 16.sp)
        Text(
            text = value,
            color = theme.text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = theme.textSecondary,
            fontSize = 9.sp
        )
    }
}

// ════════════════════════════════════════════
// SÉLECTEUR CATÉGORIE
// ════════════════════════════════════════════
@Composable
fun CategorySelector(
    selectedCategory: StatsCategory,
    onCategorySelected: (StatsCategory) -> Unit
) {
    val theme = LocalNovaTheme.current
    val categories = StatsCategory.values()

    TabRow(
        selectedTabIndex = categories.indexOf(selectedCategory),
        containerColor = theme.background,
        contentColor = theme.primary,
        indicator = {}
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            Tab(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = category.label,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected)
                            FontWeight.Bold
                        else FontWeight.Normal,
                        color = if (isSelected)
                            theme.primary
                        else theme.textSecondary
                    )
                }
            )
        }
    }
}

// ════════════════════════════════════════════
// BARRE DE RECHERCHE
// ════════════════════════════════════════════
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    val theme = LocalNovaTheme.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(theme.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        if (query.isEmpty()) {
            Text(
                text = "🔍 Rechercher...",
                color = theme.textSecondary,
                fontSize = 14.sp
            )
        }
        BasicTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                color = theme.text,
                fontSize = 14.sp
            ),
            singleLine = true
        )
    }
}

// ════════════════════════════════════════════
// LISTE TRACKS
// ════════════════════════════════════════════
@Composable
fun TracksList(
    state: StatsUiState,
    viewModel: StatsViewModel
) {
    val theme = LocalNovaTheme.current

    if (state.tracks.isEmpty()) {
        EmptyState(message = "Aucun titre trouvé")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(state.tracks) { index, track ->
            StatsRow(
                position = viewModel.formatPosition(index),
                title = track.title,
                subtitle = "",
                plays = track.play_count,
                duration = viewModel.formatDuration(track.total_duration_ms)
            )
        }
    }
}

// ════════════════════════════════════════════
// LISTE ARTISTS
// ════════════════════════════════════════════
@Composable
fun ArtistsList(
    state: StatsUiState,
    viewModel: StatsViewModel
) {
    if (state.artists.isEmpty()) {
        EmptyState(message = "Aucun artiste trouvé")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(state.artists) { index, artist ->
            StatsRow(
                position = viewModel.formatPosition(index),
                title = artist.name,
                subtitle = "${artist.distinct_tracks} titres",
                plays = artist.play_count,
                duration = viewModel.formatDuration(artist.total_duration_ms)
            )
        }
    }
}

// ════════════════════════════════════════════
// LISTE ALBUMS
// ════════════════════════════════════════════
@Composable
fun AlbumsList(
    state: StatsUiState,
    viewModel: StatsViewModel
) {
    if (state.albums.isEmpty()) {
        EmptyState(message = "Aucun album trouvé")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(state.albums) { index, album ->
            StatsRow(
                position = viewModel.formatPosition(index),
                title = album.title,
                subtitle = "${album.distinct_tracks_played} titres écoutés",
                plays = album.play_count,
                duration = viewModel.formatDuration(album.total_duration_ms)
            )
        }
    }
}

// ════════════════════════════════════════════
// LIGNE CLASSEMENT
// ════════════════════════════════════════════
@Composable
fun StatsRow(
    position: String,
    title: String,
    subtitle: String,
    plays: Int,
    duration: String
) {
    val theme = LocalNovaTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(theme.surface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Position
        Text(
            text = position,
            color = theme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(48.dp)
        )

        // Titre + sous-titre
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = theme.text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    color = theme.textSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Écoutes + durée
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$plays",
                color = theme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = duration,
                color = theme.textSecondary,
                fontSize = 10.sp
            )
        }
    }
}

// ════════════════════════════════════════════
// ÉTAT VIDE
// ════════════════════════════════════════════
@Composable
fun EmptyState(message: String) {
    val theme = LocalNovaTheme.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "🎵", fontSize = 48.sp)
            Text(
                text = message,
                color = theme.textSecondary,
                fontSize = 16.sp
            )
            Text(
                text = "Écoute de la musique pour voir tes stats !",
                color = theme.textSecondary,
                fontSize = 13.sp
            )
        }
    }
}