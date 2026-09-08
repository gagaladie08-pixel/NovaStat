package com.novastats.app.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novastats.app.data.db.NovaDatabase
import com.novastats.app.service.detection.ScrobbleProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── DataStore extension ───────────────────────
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "nova_settings"
)

// ════════════════════════════════════════════
// CLÉS DATASTORE
// ════════════════════════════════════════════
object SettingsKeys {
    // Détection
    val THRESHOLD        = intPreferencesKey("threshold")
    val VOLUME_TRACKING  = booleanPreferencesKey("volume_tracking")
    val FILTER_LONG      = booleanPreferencesKey("filter_long")

    // Notifications certifications
    val NOTIF_SILVER     = booleanPreferencesKey("notif_silver")
    val NOTIF_GOLD       = booleanPreferencesKey("notif_gold")
    val NOTIF_PLATINUM   = booleanPreferencesKey("notif_platinum")
    val NOTIF_DIAMOND    = booleanPreferencesKey("notif_diamond")
    val NOTIF_MULTI      = booleanPreferencesKey("notif_multiplier")

    // Notifications panthéon
    val NOTIF_STAR       = booleanPreferencesKey("notif_star")
    val NOTIF_SUPERSTAR  = booleanPreferencesKey("notif_superstar")
    val NOTIF_MEGASTAR   = booleanPreferencesKey("notif_megastar")
    val NOTIF_LEGEND     = booleanPreferencesKey("notif_legend")
    val NOTIF_MYTHIQUE   = booleanPreferencesKey("notif_mythique")

    // Notifications autres
    val NOTIF_HOF        = booleanPreferencesKey("notif_hof")

    // Thème
    val THEME_ID         = stringPreferencesKey("theme_id")

    // Watchdog
    val WATCHDOG_ENABLED = booleanPreferencesKey("watchdog_enabled")
}

// ════════════════════════════════════════════
// UI STATE
// ════════════════════════════════════════════
data class SettingsUiState(
    // Détection
    val threshold: Int = 30,
    val volumeTracking: Boolean = true,
    val filterLong: Boolean = true,

    // Notifications certifications
    val notifSilver: Boolean = true,
    val notifGold: Boolean = true,
    val notifPlatinum: Boolean = true,
    val notifDiamond: Boolean = true,
    val notifMultiplier: Boolean = true,

    // Notifications panthéon
    val notifStar: Boolean = true,
    val notifSuperstar: Boolean = true,
    val notifMegastar: Boolean = true,
    val notifLegend: Boolean = true,
    val notifMythique: Boolean = true,

    // Notifications autres
    val notifHof: Boolean = true,

    // Service
    val watchdogEnabled: Boolean = true,

    // DB
    val dbSizeMb: String = "0 MB",

    // Version
    val appVersion: String = "1.0",

    val isLoading: Boolean = true
)

// ════════════════════════════════════════════
// VIEWMODEL
// ════════════════════════════════════════════
@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: NovaDatabase,
    private val scrobbleProcessor: ScrobbleProcessor
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        loadSettings()
        loadDbSize()
    }

    // ── Charger les settings ──────────────────
    private fun loadSettings() {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                _state.update { state ->
                    state.copy(
                        threshold = prefs[SettingsKeys.THRESHOLD] ?: 30,
                        volumeTracking = prefs[SettingsKeys.VOLUME_TRACKING] ?: true,
                        filterLong = prefs[SettingsKeys.FILTER_LONG] ?: true,
                        notifSilver = prefs[SettingsKeys.NOTIF_SILVER] ?: true,
                        notifGold = prefs[SettingsKeys.NOTIF_GOLD] ?: true,
                        notifPlatinum = prefs[SettingsKeys.NOTIF_PLATINUM] ?: true,
                        notifDiamond = prefs[SettingsKeys.NOTIF_DIAMOND] ?: true,
                        notifMultiplier = prefs[SettingsKeys.NOTIF_MULTI] ?: true,
                        notifStar = prefs[SettingsKeys.NOTIF_STAR] ?: true,
                        notifSuperstar = prefs[SettingsKeys.NOTIF_SUPERSTAR] ?: true,
                        notifMegastar = prefs[SettingsKeys.NOTIF_MEGASTAR] ?: true,
                        notifLegend = prefs[SettingsKeys.NOTIF_LEGEND] ?: true,
                        notifMythique = prefs[SettingsKeys.NOTIF_MYTHIQUE] ?: true,
                        notifHof = prefs[SettingsKeys.NOTIF_HOF] ?: true,
                        watchdogEnabled = prefs[SettingsKeys.WATCHDOG_ENABLED] ?: true,
                        isLoading = false
                    )
                }
            }
        }
    }

    // ── Taille DB ─────────────────────────────
    private fun loadDbSize() {
        viewModelScope.launch {
            try {
                val dbFile = context.getDatabasePath("nova_database")
                val sizeBytes = dbFile.length()
                val sizeMb = sizeBytes / (1024.0 * 1024.0)
                _state.update { it.copy(
                    dbSizeMb = String.format("%.2f MB", sizeMb)
                )}
            } catch (e: Exception) {
                _state.update { it.copy(dbSizeMb = "—") }
            }
        }
    }

    // ════════════════════════════════════════
    // SETTERS
    // ════════════════════════════════════════

    fun setThreshold(value: Int) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[SettingsKeys.THRESHOLD] = value
            }
            // Hot-reload
            scrobbleProcessor.setThreshold(value)
            _state.update { it.copy(threshold = value) }
        }
    }

    fun setVolumeTracking(value: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[SettingsKeys.VOLUME_TRACKING] = value
            }
            _state.update { it.copy(volumeTracking = value) }
        }
    }

    fun setFilterLong(value: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[SettingsKeys.FILTER_LONG] = value
            }
            _state.update { it.copy(filterLong = value) }
        }
    }

    // ── Notifications certifications ──────────
    fun setNotifSilver(v: Boolean) = setSetting(SettingsKeys.NOTIF_SILVER, v) {
        _state.update { it.copy(notifSilver = v) }
    }
    fun setNotifGold(v: Boolean) = setSetting(SettingsKeys.NOTIF_GOLD, v) {
        _state.update { it.copy(notifGold = v) }
    }
    fun setNotifPlatinum(v: Boolean) = setSetting(SettingsKeys.NOTIF_PLATINUM, v) {
        _state.update { it.copy(notifPlatinum = v) }
    }
    fun setNotifDiamond(v: Boolean) = setSetting(SettingsKeys.NOTIF_DIAMOND, v) {
        _state.update { it.copy(notifDiamond = v) }
    }
    fun setNotifMultiplier(v: Boolean) = setSetting(SettingsKeys.NOTIF_MULTI, v) {
        _state.update { it.copy(notifMultiplier = v) }
    }

    // ── Notifications panthéon ────────────────
    fun setNotifStar(v: Boolean) = setSetting(SettingsKeys.NOTIF_STAR, v) {
        _state.update { it.copy(notifStar = v) }
    }
    fun setNotifSuperstar(v: Boolean) = setSetting(SettingsKeys.NOTIF_SUPERSTAR, v) {
        _state.update { it.copy(notifSuperstar = v) }
    }
    fun setNotifMegastar(v: Boolean) = setSetting(SettingsKeys.NOTIF_MEGASTAR, v) {
        _state.update { it.copy(notifMegastar = v) }
    }
    fun setNotifLegend(v: Boolean) = setSetting(SettingsKeys.NOTIF_LEGEND, v) {
        _state.update { it.copy(notifLegend = v) }
    }
    fun setNotifMythique(v: Boolean) = setSetting(SettingsKeys.NOTIF_MYTHIQUE, v) {
        _state.update { it.copy(notifMythique = v) }
    }

    // ── Notifications autres ──────────────────
    fun setNotifHof(v: Boolean) = setSetting(SettingsKeys.NOTIF_HOF, v) {
        _state.update { it.copy(notifHof = v) }
    }

    // ── Watchdog ──────────────────────────────
    fun setWatchdog(v: Boolean) = setSetting(SettingsKeys.WATCHDOG_ENABLED, v) {
        _state.update { it.copy(watchdogEnabled = v) }
    }

    // ── Supprimer toutes les données ──────────
    fun deleteAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                db.clearAllTables()
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ── Helper ────────────────────────────────
    private fun setSetting(
        key: Preferences.Key<Boolean>,
        value: Boolean,
        update: () -> Unit
    ) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[key] = value
            }
            update()
        }
    }
}