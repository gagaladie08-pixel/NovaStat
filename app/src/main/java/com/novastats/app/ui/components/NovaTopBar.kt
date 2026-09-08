package com.novastats.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novastats.app.core.theme.LocalNovaTheme

@Composable
fun NovaTopBar(
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val theme = LocalNovaTheme.current

    Surface(
        color = theme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Logo ──────────────────────────
            Text(
                text = "NovaStats",
                color = theme.primary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            // ── Actions ───────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Recherche
                IconButton(onClick = onSearchClick) {
                    Text(
                        text = "🔍",
                        fontSize = 20.sp
                    )
                }

                // Paramètres
                IconButton(onClick = onSettingsClick) {
                    Text(
                        text = "⚙️",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}