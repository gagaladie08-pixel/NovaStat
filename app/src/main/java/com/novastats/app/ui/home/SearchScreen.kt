package com.novastats.app.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.novastats.app.core.theme.LocalNovaTheme

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onTrackClick: (Long) -> Unit,
    onArtistClick: (Long) -> Unit,
    onAlbumClick: (Long) -> Unit
) {
    val theme = LocalNovaTheme.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🔍 Search — Bientôt !",
            color = theme.text,
            fontSize = 20.sp
        )
    }
}