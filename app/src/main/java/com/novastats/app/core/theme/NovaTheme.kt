package com.novastats.app.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

// ── LocalNovaTheme ────────────────────────────
val LocalNovaTheme = staticCompositionLocalOf { NovaThemes.CYBER_NOVA }

// ── NovaTheme Composable ──────────────────────
@Composable
fun NovaAppTheme(
    theme: NovaTheme = NovaThemes.CYBER_NOVA,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = theme.primary,
        secondary = theme.secondary,
        background = theme.background,
        surface = theme.surface,
        onPrimary = theme.text,
        onSecondary = theme.text,
        onBackground = theme.text,
        onSurface = theme.text
    )

    CompositionLocalProvider(
        LocalNovaTheme provides theme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}