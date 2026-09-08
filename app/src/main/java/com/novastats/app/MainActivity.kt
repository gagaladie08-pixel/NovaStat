package com.novastats.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.novastats.app.bridge.NovaBridgeManager
import com.novastats.app.core.navigation.NovaNavHost
import com.novastats.app.core.theme.NovaAppTheme
import com.novastats.app.core.theme.ThemeEngine
import com.novastats.app.service.WatchdogManager
import com.novastats.app.service.detection.NovaDetectionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var watchdogManager: WatchdogManager

    @Inject
    lateinit var bridgeManager: NovaBridgeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ── Démarrer les services ─────────────
        startDetectionService()
        watchdogManager.start()

        // ── UI ───────────────────────────────
        setContent {
            var currentTheme by remember {
                mutableStateOf(ThemeEngine.current)
            }

            NovaAppTheme(theme = currentTheme) {
                NovaNavHost(
                    onThemeChange = { themeId ->
                        ThemeEngine.apply(themeId)
                        currentTheme = ThemeEngine.current
                    },
                    onOpenPopup = { type, id ->
                        bridgeManager.onOpenPopup?.invoke(type, id)
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        watchdogManager.stop()
    }

    // ── Démarrer le service ───────────────────
    private fun startDetectionService() {
        if (isNotificationListenerEnabled()) {
            NovaDetectionService.start(this)
        }
    }

    // ── Vérifier permission NotificationListener ──
    fun isNotificationListenerEnabled(): Boolean {
        val flat = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        return flat?.contains(packageName) == true
    }

    // ── Ouvrir les paramètres NotificationListener ──
    fun openNotificationListenerSettings() {
        startActivity(
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        )
    }
}