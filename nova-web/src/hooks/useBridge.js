// ════════════════════════════════════════════
// HOOK — COMMUNICATION APP ↔ WEBVIEW
// ════════════════════════════════════════════
import { useEffect } from 'react'
import { getMockData } from '../utils/mock'

// Thème par défaut (dev)
const defaultTheme = {
    id: "cyber-nova",
    primary: "#FF006E",
    secondary: "#00B4FF",
    glow: "#BD00FF",
    background: "#050510",
    surface: "#0D0D2B",
    text: "#F0F0FF",
    textSecondary: "#A0A0C0",
    accent: "#00FFF0"
}

export function useBridge() {

    // ── Récupérer des données ─────────────────
    const getData = (request) => {
        if (window.NovaBridge) {
            try {
                const json = window.NovaBridge.getData(
                    JSON.stringify(request)
                )
                return JSON.parse(json)
            } catch (e) {
                console.error("Bridge error:", e)
                return getMockData(request)
            }
        }
        // Mode dev → données factices
        return getMockData(request)
    }

    // ── Récupérer le thème ────────────────────
    const getTheme = () => {
        if (window.NovaBridge) {
            try {
                return JSON.parse(window.NovaBridge.getTheme())
            } catch (e) {
                return defaultTheme
            }
        }
        return defaultTheme
    }

    // ── Ouvrir un popup natif ─────────────────
    const openPopup = (type, id) => {
        if (window.NovaBridge) {
            window.NovaBridge.action(JSON.stringify({
                type: 'openPopup',
                entityType: type,
                entityId: id
            }))
        } else {
            // Dev → log uniquement
            console.log(`[DEV] openPopup: ${type} #${id}`)
        }
    }

    // ── Naviguer vers un onglet natif ─────────
    const navigateTo = (tab) => {
        if (window.NovaBridge) {
            window.NovaBridge.action(JSON.stringify({
                type: 'navigate',
                tab: tab
            }))
        } else {
            console.log(`[DEV] navigate: ${tab}`)
        }
    }

    // ── Écouter les events du natif ───────────
    const onNovaEvent = (callback) => {
        useEffect(() => {
            window.onNovaEvent = (event, data) => {
                try {
                    callback(event, typeof data === 'string'
                        ? JSON.parse(data)
                        : data
                    )
                } catch (e) {
                    console.error("Event error:", e)
                }
            }
            return () => {
                window.onNovaEvent = null
            }
        }, [])
    }

    // ── Infos app ─────────────────────────────
    const getAppInfo = () => {
        if (window.NovaBridge) {
            try {
                return JSON.parse(window.NovaBridge.getAppInfo())
            } catch (e) {
                return { version: "1.0", platform: "android" }
            }
        }
        return { version: "dev", platform: "web" }
    }

    return {
        getData,
        getTheme,
        openPopup,
        navigateTo,
        onNovaEvent,
        getAppInfo,
        isDev: !window.NovaBridge
    }
}