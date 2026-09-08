package com.novastats.app.ui.popups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novastats.app.core.theme.LocalNovaTheme

// ════════════════════════════════════════════
// GRILLE DE STATS
// ════════════════════════════════════════════
@Composable
fun PopupStatGrid(
    stats: List<Pair<String, String>>
) {
    val theme = LocalNovaTheme.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(theme.background)
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            stats.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { (emoji, value) ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = emoji, fontSize = 14.sp)
                            Text(
                                text = value,
                                color = theme.text,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════
// SECTION AVEC TITRE
// ════════════════════════════════════════════
@Composable
fun PopupSection(
    title: String,
    content: @Composable () -> Unit
) {
    val theme = LocalNovaTheme.current

    Column {
        Text(
            text = title,
            color = theme.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(theme.background)
                .padding(12.dp)
        ) {
            content()
        }
    }
}