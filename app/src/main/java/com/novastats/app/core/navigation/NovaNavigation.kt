package com.novastats.app.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.novastats.app.core.theme.LocalNovaTheme
import com.novastats.app.ui.components.NovaTabBar
import com.novastats.app.ui.components.NovaTopBar
import com.novastats.app.ui.components.WebViewContainer
import com.novastats.app.ui.home.HomeScreen
import com.novastats.app.ui.onboarding.WelcomeScreen
import com.novastats.app.ui.popups.AlbumPopup
import com.novastats.app.ui.popups.ArtistPopup
import com.novastats.app.ui.popups.TrackPopup
import com.novastats.app.ui.search.SearchScreen
import com.novastats.app.ui.settings.SettingsScreen
import com.novastats.app.ui.stats.StatsScreen

// ════════════════════════════════════════════
// ROUTES
// ════════════════════════════════════════════
object NovaRoutes {
    const val ONBOARDING = "onboarding"
    const val MAIN       = "main"
    const val SETTINGS   = "settings"
    const val SEARCH     = "search"
}

// ════════════════════════════════════════════
// ONGLETS
// ════════════════════════════════════════════
data class NovaTab(
    val index: Int,
    val label: String,
    val icon: String,
    val webPage: String? = null
)

val novaTabs = listOf(
    NovaTab(0, "HOME",  "🏠", null),
    NovaTab(1, "STATS", "📊", null),
    NovaTab(2, "BOARD", "🏆", "billboard"),
    NovaTab(3, "RECS",  "🏅", "records"),
    NovaTab(4, "CERTS", "💎", "certs"),
    NovaTab(5, "HALL",  "🏛️", "hof"),
    NovaTab(6, "PANTH", "👑", "pantheon"),
    NovaTab(7, "AWARD", "🎵", "awards")
)

// ════════════════════════════════════════════
// NAVHOST PRINCIPAL
// ════════════════════════════════════════════
@Composable
fun NovaNavHost(
    onThemeChange: (String) -> Unit,
    onOpenPopup: (String, Long) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NovaRoutes.ONBOARDING
    ) {

        // ── Onboarding ────────────────────────
        composable(NovaRoutes.ONBOARDING) {
            WelcomeScreen(
                onFinished = {
                    navController.navigate(NovaRoutes.MAIN) {
                        popUpTo(NovaRoutes.ONBOARDING) {
                            inclusive = true
                        }
                    }
                },
                onThemeChange = onThemeChange
            )
        }

        // ── Main ──────────────────────────────
        composable(NovaRoutes.MAIN) {
            MainScreen(
                onSettingsClick = {
                    navController.navigate(NovaRoutes.SETTINGS)
                },
                onSearchClick = {
                    navController.navigate(NovaRoutes.SEARCH)
                }
            )
        }

        // ── Settings ──────────────────────────
        composable(NovaRoutes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onThemeChange = onThemeChange
            )
        }

        // ── Search ────────────────────────────
        composable(NovaRoutes.SEARCH) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onTrackClick = { id ->
                    onOpenPopup("track", id)
                },
                onArtistClick = { id ->
                    onOpenPopup("artist", id)
                },
                onAlbumClick = { id ->
                    onOpenPopup("album", id)
                }
            )
        }
    }
}

// ════════════════════════════════════════════
// MAIN SCREEN
// ════════════════════════════════════════════
@Composable
fun MainScreen(
    onSettingsClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val theme = LocalNovaTheme.current

    // ── Onglet sélectionné ────────────────────
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // ── État popups ───────────────────────────
    var showTrackPopup by remember { mutableStateOf(false) }
    var showArtistPopup by remember { mutableStateOf(false) }
    var showAlbumPopup by remember { mutableStateOf(false) }
    var popupEntityId by remember { mutableStateOf(0L) }

    // ── Callback popup ────────────────────────
    val handleOpenPopup: (String, Long) -> Unit = { type, id ->
        popupEntityId = id
        when (type) {
            "track"  -> showTrackPopup = true
            "artist" -> showArtistPopup = true
            "album"  -> showAlbumPopup = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(theme.background)
        ) {

            // ── Header fixe ───────────────────
            NovaTopBar(
                onSearchClick = onSearchClick,
                onSettingsClick = onSettingsClick
            )

            // ── TabBar fixe ───────────────────
            NovaTabBar(
                tabs = novaTabs,
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    selectedTab = index
                }
            )

            // ── Contenu ───────────────────────
            when (selectedTab) {
                0 -> HomeScreen()
                1 -> StatsScreen()
                else -> {
                    val tab = novaTabs[selectedTab]
                    tab.webPage?.let { page ->
                        WebViewContainer(
                            page = page,
                            onTrackClick = { id ->
                                handleOpenPopup("track", id)
                            },
                            onArtistClick = { id ->
                                handleOpenPopup("artist", id)
                            },
                            onAlbumClick = { id ->
                                handleOpenPopup("album", id)
                            }
                        )
                    }
                }
            }
        }
    }

    // ── Popups natifs ─────────────────────────
    if (showTrackPopup) {
        TrackPopup(
            trackId = popupEntityId,
            onDismiss = { showTrackPopup = false }
        )
    }

    if (showArtistPopup) {
        ArtistPopup(
            artistId = popupEntityId,
            onDismiss = { showArtistPopup = false }
        )
    }

    if (showAlbumPopup) {
        AlbumPopup(
            albumId = popupEntityId,
            onDismiss = { showAlbumPopup = false }
        )
    }
}