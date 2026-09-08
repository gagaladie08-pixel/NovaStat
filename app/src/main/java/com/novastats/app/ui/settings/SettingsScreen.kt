package com.novastats.app.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.novastats.app.core.theme.LocalNovaTheme
import com.novastats.app.core.theme.NovaThemes

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onThemeChange: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = LocalNovaTheme.current
    val context = LocalContext.current

    // ── Dialogs ───────────────────────────────
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }

    // ── Delete dialog ─────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "⚠️ Supprimer toutes les données ?",
                    color = theme.text
                )
            },
            text = {
                Text(
                    "Cette action est IRRÉVERSIBLE. " +
                            "Toutes tes écoutes, stats, certifications " +
                            "et records seront perdus.",
                    color = theme.textSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllData {
                            showDeleteDialog = false
                        }
                    }
                ) {
                    Text("SUPPRIMER", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("ANNULER", color = theme.primary)
                }
            },
            containerColor = theme.surface
        )
    }

    // ── Theme sheet ───────────────────────────
    if (showThemeSheet) {
        ThemeSelector(
            currentThemeId = theme.id,
            onThemeSelected = { themeId ->
                onThemeChange(themeId)
                showThemeSheet = false
            },
            onDismiss = { showThemeSheet = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
    ) {

        // ── Header ────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "←",
                    color = theme.primary,
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { onBack() }
                )
                Text(
                    text = "⚙️ Paramètres",
                    color = theme.text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // ════════════════════════════════
            // SECTION DÉTECTION
            // ════════════════════════════════
            item {
                SettingsSection(title = "🎵 Détection") {

                    // Seuil
                    SettingsItemSelector(
                        label = "Seuil de scrobble",
                        value = "${state.threshold}s",
                        options = listOf("15s", "30s", "60s", "90s"),
                        onSelect = { option ->
                            viewModel.setThreshold(
                                option.replace("s", "").toInt()
                            )
                        }
                    )

                    SettingsDivider()

                    // Volume = 0
                    SettingsToggle(
                        label = "Tracking volume = 0%",
                        subtitle = "Suspendre si le son est coupé",
                        checked = state.volumeTracking,
                        onCheckedChange = { viewModel.setVolumeTracking(it) }
                    )

                    SettingsDivider()

                    // Filtre durée > 10 min
                    SettingsToggle(
                        label = "Filtre durée > 10 min",
                        subtitle = "Confirmation avant de tracker",
                        checked = state.filterLong,
                        onCheckedChange = { viewModel.setFilterLong(it) }
                    )
                }
            }

            // ════════════════════════════════
            // SECTION NOTIFICATIONS
            // ════════════════════════════════
            item {
                SettingsSection(title = "🔔 Notifications — Certifications") {

                    SettingsToggle(
                        label = "🥉 Argent",
                        checked = state.notifSilver,
                        onCheckedChange = { viewModel.setNotifSilver(it) }
                    )
                    SettingsDivider()

                    SettingsToggle(
                        label = "🥈 Or",
                        checked = state.notifGold,
                        onCheckedChange = { viewModel.setNotifGold(it) }
                    )
                    SettingsDivider()

                    SettingsToggle(
                        label = "🥇 Platine",
                        checked = state.notifPlatinum,
                        onCheckedChange = { viewModel.setNotifPlatinum(it) }
                    )
                    SettingsDivider()

                    SettingsToggle(
                        label = "💎 Diamant",
                        checked = state.notifDiamond,
                        onCheckedChange = { viewModel.setNotifDiamond(it) }
                    )
                    SettingsDivider()

                    SettingsToggle(
                        label = "💎 Multiplicateurs",
                        checked = state.notifMultiplier,
                        onCheckedChange = { viewModel.setNotifMultiplier(it) }
                    )
                }
            }

            item {
                SettingsSection(title = "🔔 Notifications — Panthéon") {

                    SettingsToggle(
                        label = "⭐ Star",
                        checked = state.notifStar,
                        onCheckedChange = { viewModel.setNotifStar(it) }
                    )
                    SettingsDivider()

                    SettingsToggle(
                        label = "🌟 Superstar",
                        checked = state.notifSuperstar,
                        onCheckedChange = { viewModel.setNotifSuperstar(it) }
                    )
                    SettingsDivider()

                    SettingsToggle(
                        label = "👑 Megastar",
                        checked = state.notifMegastar,
                        onCheckedChange = { viewModel.setNotifMegastar(it) }
                    )
                    SettingsDivider()

                    SettingsToggle(
                        label = "🏛️ Légende",
                        checked = state.notifLegend,
                        onCheckedChange = { viewModel.setNotifLegend(it) }
                    )
                    SettingsDivider()

                    SettingsToggle(
                        label = "✨ Mythique",
                        checked = state.notifMythique,
                        onCheckedChange = { viewModel.setNotifMythique(it) }
                    )
                    SettingsDivider()

                    SettingsToggle(
                        label = "🏛️ Hall of Fame",
                        checked = state.notifHof,
                        onCheckedChange = { viewModel.setNotifHof(it) }
                    )
                }
            }

            // ════════════════════════════════
            // SECTION APPARENCE
            // ════════════════════════════════
            item {
                SettingsSection(title = "🎨 Apparence") {
                    SettingsItemArrow(
                        label = "Thème visuel",
                        value = "${theme.emoji} ${theme.name}",
                        onClick = { showThemeSheet = true }
                    )
                }
            }

            // ════════════════════════════════
            // SECTION DONNÉES
            // ════════════════════════════════
            item {
                SettingsSection(title = "🗄️ Données") {

                    SettingsItemInfo(
                        label = "Taille de la base",
                        value = state.dbSizeMb
                    )

                    SettingsDivider()

                    SettingsItemArrow(
                        label = "Exporter mes données (JSON)",
                        onClick = { /* TODO */ }
                    )

                    SettingsDivider()

                    SettingsItemArrow(
                        label = "Importer des données (JSON)",
                        onClick = { /* TODO */ }
                    )

                    SettingsDivider()

                    // Supprimer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDeleteDialog = true }
                            .padding(vertical = 14.dp)
                    ) {
                        Text(
                            text = "🗑️ Supprimer toutes mes données",
                            color = Color.Red,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ════════════════════════════════
            // SECTION SERVICE
            // ════════════════════════════════
            item {
                SettingsSection(title = "🛡️ Service") {

                    SettingsToggle(
                        label = "Watchdog",
                        subtitle = "Relance auto toutes les 5 min",
                        checked = state.watchdogEnabled,
                        onCheckedChange = { viewModel.setWatchdog(it) }
                    )

                    SettingsDivider()

                    // Optimisation batterie
                    SettingsItemArrow(
                        label = "Optimisation batterie",
                        subtitle = "Ouvrir les paramètres Android",
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                            ).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        }
                    )

                    SettingsDivider()

                    // Accès notifications
                    SettingsItemArrow(
                        label = "Accès aux notifications",
                        subtitle = "Obligatoire pour la détection",
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                                )
                            )
                        }
                    )
                }
            }

            // ════════════════════════════════
            // SECTION À PROPOS
            // ════════════════════════════════
            item {
                SettingsSection(title = "ℹ️ À propos") {

                    SettingsItemInfo(
                        label = "Version",
                        value = state.appVersion
                    )

                    SettingsDivider()

                    SettingsItemInfo(
                        label = "Développeur",
                        value = "NovaStats Team"
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

// ════════════════════════════════════════════
// SÉLECTEUR DE THÈME
// ════════════════════════════════════════════
@Composable
fun ThemeSelector(
    currentThemeId: String,
    onThemeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val theme = LocalNovaTheme.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(16.dp))
                .background(theme.surface)
                .clickable(enabled = false) {}
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Header
                Text(
                    text = "🎨 Choisis ton univers",
                    color = theme.primary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(NovaThemes.ALL.size) { index ->
                        val t = NovaThemes.ALL[index]
                        val isSelected = t.id == currentThemeId

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected)
                                        Color(t.primary.value).copy(alpha = 0.2f)
                                    else
                                        theme.background
                                )
                                .clickable { onThemeSelected(t.id) }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = t.emoji,
                                    fontSize = 24.sp
                                )
                                Column {
                                    Text(
                                        text = t.name,
                                        color = if (isSelected)
                                            t.primary
                                        else theme.text,
                                        fontSize = 15.sp,
                                        fontWeight = if (isSelected)
                                            FontWeight.Bold
                                        else FontWeight.Normal
                                    )
                                    // Mini palette
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        listOf(
                                            t.primary,
                                            t.secondary,
                                            t.glow,
                                            t.accent
                                        ).forEach { color ->
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(color)
                                            )
                                        }
                                    }
                                }
                            }

                            if (isSelected) {
                                Text(
                                    text = "✅",
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }

                    item {
                        // Bouton fermer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(theme.primary)
                                .clickable { onDismiss() }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "FERMER",
                                color = theme.background,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════
// COMPOSANTS RÉUTILISABLES SETTINGS
// ════════════════════════════════════════════

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val theme = LocalNovaTheme.current

    Column {
        Text(
            text = title,
            color = theme.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(
                horizontal = 4.dp,
                vertical = 6.dp
            )
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(theme.surface)
                .padding(horizontal = 16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun SettingsToggle(
    label: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val theme = LocalNovaTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = theme.text,
                fontSize = 15.sp
            )
            subtitle?.let {
                Text(
                    text = it,
                    color = theme.textSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = theme.background,
                checkedTrackColor = theme.primary,
                uncheckedThumbColor = theme.textSecondary,
                uncheckedTrackColor = theme.surface
            )
        )
    }
}

@Composable
fun SettingsItemArrow(
    label: String,
    value: String? = null,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    val theme = LocalNovaTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = theme.text,
                fontSize = 15.sp
            )
            subtitle?.let {
                Text(
                    text = it,
                    color = theme.textSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            value?.let {
                Text(
                    text = it,
                    color = theme.textSecondary,
                    fontSize = 13.sp
                )
            }
            Text(
                text = "›",
                color = theme.textSecondary,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun SettingsItemInfo(
    label: String,
    value: String
) {
    val theme = LocalNovaTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = theme.text,
            fontSize = 15.sp
        )
        Text(
            text = value,
            color = theme.textSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
fun SettingsItemSelector(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    val theme = LocalNovaTheme.current
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = theme.text,
            fontSize = 15.sp
        )

        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.background)
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    color = theme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "▾",
                    color = theme.primary,
                    fontSize = 12.sp
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = if (option == value)
                                    theme.primary
                                else theme.text
                            )
                        },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsDivider() {
    val theme = LocalNovaTheme.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(theme.background)
    )
}