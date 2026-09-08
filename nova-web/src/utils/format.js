// ════════════════════════════════════════════
// FORMATAGE NOVASTATS
// ════════════════════════════════════════════

// Formater durée en ms → "2h 34min" ou "45min"
export function formatDuration(ms) {
    if (!ms || ms <= 0) return "0min"
    const hours = Math.floor(ms / 3_600_000)
    const minutes = Math.floor((ms % 3_600_000) / 60_000)
    if (hours > 0) return `${hours}h ${minutes}min`
    return `${minutes}min`
}

// Formater nombre → "1 234"
export function formatNumber(n) {
    if (!n) return "0"
    return n.toLocaleString('fr-FR')
}

// Formater position
export function formatPosition(pos) {
    switch (pos) {
        case 1: return "🥇"
        case 2: return "🥈"
        case 3: return "🥉"
        default:
            if (pos <= 10) return `🔥 #${pos}`
            return `#${pos}`
    }
}

// Formater mouvement Billboard
export function formatMovement(movement, isNew, isReentry) {
    if (isNew) return { label: "🆕 NEW", className: "move-new" }
    if (isReentry) return { label: "↩️ RE", className: "move-new" }
    if (movement > 0) return {
        label: `↑ +${movement}`,
        className: "move-up"
    }
    if (movement < 0) return {
        label: `↓ ${movement}`,
        className: "move-down"
    }
    return { label: "=", className: "move-same" }
}

// Formater date timestamp
export function formatTime(timestamp) {
    if (!timestamp) return ""
    return new Date(timestamp).toLocaleTimeString('fr-FR', {
        hour: '2-digit',
        minute: '2-digit'
    })
}

// Formater date complète
export function formatDate(dateStr) {
    if (!dateStr) return ""
    return new Date(dateStr).toLocaleDateString('fr-FR', {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
    })
}

// Formater certification
export function formatCertLevel(level, multiplier) {
    const levels = {
        SILVER: "🥉 Argent",
        GOLD: "🥈 Or",
        PLATINUM: "🥇 Platine",
        DIAMOND: "💎 Diamant"
    }
    const base = levels[level] || level
    if (level === "DIAMOND" && multiplier > 1) {
        return `${"💎".repeat(multiplier)} ${multiplier}x Diamant`
    }
    return base
}

// Formater statut Panthéon
export function formatPantheonStatus(status) {
    const statuses = {
        STAR: "⭐ Star",
        SUPERSTAR: "🌟 Superstar",
        MEGASTAR: "👑 Megastar",
        LEGENDE: "🏛️ Légende",
        MYTHIQUE: "✨ Mythique"
    }
    return statuses[status] || status
}

// Temps écoulé
export function timeAgo(timestamp) {
    if (!timestamp) return ""
    const diff = Date.now() - timestamp
    const minutes = Math.floor(diff / 60_000)
    const hours = Math.floor(diff / 3_600_000)
    const days = Math.floor(diff / 86_400_000)

    if (minutes < 1) return "À l'instant"
    if (minutes < 60) return `Il y a ${minutes}min`
    if (hours < 24) return `Il y a ${hours}h`
    return `Il y a ${days}j`
}