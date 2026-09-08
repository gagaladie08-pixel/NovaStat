package com.novastats.app.ui.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewContainer(
    page: String,
    onTrackClick: (Long) -> Unit,
    onArtistClick: (Long) -> Unit,
    onAlbumClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {

                // ── Settings ──────────────────
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.allowContentAccess = true
                @Suppress("SetJavaScriptEnabled")
                settings.javaScriptEnabled = true

                // ── Activer le debug ──────────
                WebView.setWebContentsDebuggingEnabled(true)

                // ── Accès fichiers locaux ─────
                settings.allowFileAccessFromFileURLs = true
                settings.allowUniversalAccessFromFileURLs = true

                // ── WebViewClient ─────────────
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(
                        view: WebView?,
                        url: String?
                    ) {
                        super.onPageFinished(view, url)
                        view?.evaluateJavascript(
                            "if(window.navigateTo) " +
                                    "window.navigateTo('$page')",
                            null
                        )
                    }
                }

                // ── Charger le site local ─────
                loadUrl("file:///android_asset/web/index.html")
            }
        },
        update = { webView ->
            webView.evaluateJavascript(
                "if(window.navigateTo) window.navigateTo('$page')",
                null
            )
        },
        modifier = modifier.fillMaxSize()
    )
}