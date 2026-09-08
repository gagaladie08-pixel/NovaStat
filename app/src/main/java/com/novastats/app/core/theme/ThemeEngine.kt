package com.novastats.app.core.theme

import androidx.compose.ui.graphics.Color

// ════════════════════════════════════════════
// MODÈLE — NovaTheme
// ════════════════════════════════════════════
data class NovaTheme(
    val id: String,
    val name: String,
    val emoji: String,
    val primary: Color,
    val secondary: Color,
    val glow: Color,
    val background: Color,
    val surface: Color,
    val text: Color,
    val textSecondary: Color,
    val accent: Color,
    // Pour le bridge JS
    val primaryHex: String,
    val secondaryHex: String,
    val glowHex: String,
    val backgroundHex: String,
    val surfaceHex: String,
    val textHex: String,
    val textSecondaryHex: String,
    val accentHex: String
)

// ════════════════════════════════════════════
// TOUS LES THÈMES
// ════════════════════════════════════════════
object NovaThemes {

    val CYBER_NOVA = NovaTheme(
        id = "cyber-nova",
        name = "Cyber Nova",
        emoji = "🌌",
        primary = Color(0xFFFF006E),
        secondary = Color(0xFF00B4FF),
        glow = Color(0xFFBD00FF),
        background = Color(0xFF050510),
        surface = Color(0xFF0D0D2B),
        text = Color(0xFFF0F0FF),
        textSecondary = Color(0xFFA0A0C0),
        accent = Color(0xFF00FFF0),
        primaryHex = "#FF006E",
        secondaryHex = "#00B4FF",
        glowHex = "#BD00FF",
        backgroundHex = "#050510",
        surfaceHex = "#0D0D2B",
        textHex = "#F0F0FF",
        textSecondaryHex = "#A0A0C0",
        accentHex = "#00FFF0"
    )

    val NEON_DISCO = NovaTheme(
        id = "neon-disco",
        name = "Neon Disco",
        emoji = "🟣",
        primary = Color(0xFF9B00FF),
        secondary = Color(0xFFFFD700),
        glow = Color(0xFFFF69B4),
        background = Color(0xFF0A0010),
        surface = Color(0xFF1A0030),
        text = Color(0xFFE8E8FF),
        textSecondary = Color(0xFFB0A0C0),
        accent = Color(0xFFC0C0C0),
        primaryHex = "#9B00FF",
        secondaryHex = "#FFD700",
        glowHex = "#FF69B4",
        backgroundHex = "#0A0010",
        surfaceHex = "#1A0030",
        textHex = "#E8E8FF",
        textSecondaryHex = "#B0A0C0",
        accentHex = "#C0C0C0"
    )

    val VILLAIN_ERA = NovaTheme(
        id = "villain-era",
        name = "Villain Era",
        emoji = "🖤",
        primary = Color(0xFFCC0000),
        secondary = Color(0xFF4A4A4A),
        glow = Color(0xFF8B0000),
        background = Color(0xFF080808),
        surface = Color(0xFF111111),
        text = Color(0xFFEEEEEE),
        textSecondary = Color(0xFF888888),
        accent = Color(0xFFFF0000),
        primaryHex = "#CC0000",
        secondaryHex = "#4A4A4A",
        glowHex = "#8B0000",
        backgroundHex = "#080808",
        surfaceHex = "#111111",
        textHex = "#EEEEEE",
        textSecondaryHex = "#888888",
        accentHex = "#FF0000"
    )

    val SLAY_QUEEN = NovaTheme(
        id = "slay-queen",
        name = "Slay Queen",
        emoji = "👑",
        primary = Color(0xFFFFD700),
        secondary = Color(0xFFFF1493),
        glow = Color(0xFFFFA500),
        background = Color(0xFF0A0A0A),
        surface = Color(0xFF1A1500),
        text = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFD4AF37),
        accent = Color(0xFFB76E79),
        primaryHex = "#FFD700",
        secondaryHex = "#FF1493",
        glowHex = "#FFA500",
        backgroundHex = "#0A0A0A",
        surfaceHex = "#1A1500",
        textHex = "#FFFFFF",
        textSecondaryHex = "#D4AF37",
        accentHex = "#B76E79"
    )

    val PINK_Y2K = NovaTheme(
        id = "pink-y2k",
        name = "Pink Y2K",
        emoji = "🍭",
        primary = Color(0xFFFF69B4),
        secondary = Color(0xFF87CEEB),
        glow = Color(0xFFFF1493),
        background = Color(0xFFFFF0F5),
        surface = Color(0xFFFFE4E1),
        text = Color(0xFF8B0045),
        textSecondary = Color(0xFFC06080),
        accent = Color(0xFFDA70D6),
        primaryHex = "#FF69B4",
        secondaryHex = "#87CEEB",
        glowHex = "#FF1493",
        backgroundHex = "#FFF0F5",
        surfaceHex = "#FFE4E1",
        textHex = "#8B0045",
        textSecondaryHex = "#C06080",
        accentHex = "#DA70D6"
    )

    val VELVET_STAGE = NovaTheme(
        id = "velvet-stage",
        name = "Velvet Stage",
        emoji = "🎭",
        primary = Color(0xFF8B0000),
        secondary = Color(0xFF722F37),
        glow = Color(0xFFCFB53B),
        background = Color(0xFF0C0008),
        surface = Color(0xFF1A0010),
        text = Color(0xFFFFF8DC),
        textSecondary = Color(0xFFD4AF37),
        accent = Color(0xFFFFD700),
        primaryHex = "#8B0000",
        secondaryHex = "#722F37",
        glowHex = "#CFB53B",
        backgroundHex = "#0C0008",
        surfaceHex = "#1A0010",
        textHex = "#FFF8DC",
        textSecondaryHex = "#D4AF37",
        accentHex = "#FFD700"
    )

    val PINK_VENOM = NovaTheme(
        id = "pink-venom",
        name = "Pink Venom",
        emoji = "🖤",
        primary = Color(0xFFFF0080),
        secondary = Color(0xFFFF0030),
        glow = Color(0xFFFF69B4),
        background = Color(0xFF000000),
        surface = Color(0xFF0D0008),
        text = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFFFB6C1),
        accent = Color(0xFFFF007F),
        primaryHex = "#FF0080",
        secondaryHex = "#FF0030",
        glowHex = "#FF69B4",
        backgroundHex = "#000000",
        surfaceHex = "#0D0008",
        textHex = "#FFFFFF",
        textSecondaryHex = "#FFB6C1",
        accentHex = "#FF007F"
    )

    val CLOUD_NINE = NovaTheme(
        id = "cloud-nine",
        name = "Cloud Nine",
        emoji = "☁️",
        primary = Color(0xFFFFB6C1),
        secondary = Color(0xFFE6E6FA),
        glow = Color(0xFFFFC0CB),
        background = Color(0xFFFAFAFA),
        surface = Color(0xFFFFF5F7),
        text = Color(0xFF555555),
        textSecondary = Color(0xFF9090A0),
        accent = Color(0xFFC8A2C8),
        primaryHex = "#FFB6C1",
        secondaryHex = "#E6E6FA",
        glowHex = "#FFC0CB",
        backgroundHex = "#FAFAFA",
        surfaceHex = "#FFF5F7",
        textHex = "#555555",
        textSecondaryHex = "#9090A0",
        accentHex = "#C8A2C8"
    )

    val SOLARA = NovaTheme(
        id = "solara",
        name = "Solara",
        emoji = "☀️",
        primary = Color(0xFFFF6B35),
        secondary = Color(0xFFC4622D),
        glow = Color(0xFFFFA500),
        background = Color(0xFF1A0A00),
        surface = Color(0xFF2D1200),
        text = Color(0xFFFFF3E0),
        textSecondary = Color(0xFFD4A056),
        accent = Color(0xFFFFD700),
        primaryHex = "#FF6B35",
        secondaryHex = "#C4622D",
        glowHex = "#FFA500",
        backgroundHex = "#1A0A00",
        surfaceHex = "#2D1200",
        textHex = "#FFF3E0",
        textSecondaryHex = "#D4A056",
        accentHex = "#FFD700"
    )

    val CHAOS_BORN = NovaTheme(
        id = "chaos-born",
        name = "Chaos Born",
        emoji = "🦋",
        primary = Color(0xFFC0C0C0),
        secondary = Color(0xFF1A1A1A),
        glow = Color(0xFFE8E8E8),
        background = Color(0xFF050505),
        surface = Color(0xFF111111),
        text = Color(0xFFF0F0F0),
        textSecondary = Color(0xFF909090),
        accent = Color(0xFFDFDFDF),
        primaryHex = "#C0C0C0",
        secondaryHex = "#1A1A1A",
        glowHex = "#E8E8E8",
        backgroundHex = "#050505",
        surfaceHex = "#111111",
        textHex = "#F0F0F0",
        textSecondaryHex = "#909090",
        accentHex = "#DFDFDF"
    )

    val SURVIVOR = NovaTheme(
        id = "survivor",
        name = "Survivor",
        emoji = "🌈",
        primary = Color(0xFFFF69B4),
        secondary = Color(0xFF8B00FF),
        glow = Color(0xFFFF8C00),
        background = Color(0xFF080808),
        surface = Color(0xFF111111),
        text = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFAAAAAA),
        accent = Color(0xFF00C800),
        primaryHex = "#FF69B4",
        secondaryHex = "#8B00FF",
        glowHex = "#FF8C00",
        backgroundHex = "#080808",
        surfaceHex = "#111111",
        textHex = "#FFFFFF",
        textSecondaryHex = "#AAAAAA",
        accentHex = "#00C800"
    )

    val RAINBOW_POP = NovaTheme(
        id = "rainbow-pop",
        name = "Rainbow Pop",
        emoji = "🌈",
        primary = Color(0xFFFF4DA6),
        secondary = Color(0xFF4D79FF),
        glow = Color(0xFF9B4DFF),
        background = Color(0xFF0A0010),
        surface = Color(0xFF100020),
        text = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFFFB3D9),
        accent = Color(0xFFFFD700),
        primaryHex = "#FF4DA6",
        secondaryHex = "#4D79FF",
        glowHex = "#9B4DFF",
        backgroundHex = "#0A0010",
        surfaceHex = "#100020",
        textHex = "#FFFFFF",
        textSecondaryHex = "#FFB3D9",
        accentHex = "#FFD700"
    )

    val POP_REVOLUTION = NovaTheme(
        id = "pop-revolution",
        name = "Pop Revolution",
        emoji = "🎤",
        primary = Color(0xFF4A90D9),
        secondary = Color(0xFFFF6B9D),
        glow = Color(0xFF00BFFF),
        background = Color(0xFF050A1A),
        surface = Color(0xFF0A1428),
        text = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFA0C4FF),
        accent = Color(0xFFF0F8FF),
        primaryHex = "#4A90D9",
        secondaryHex = "#FF6B9D",
        glowHex = "#00BFFF",
        backgroundHex = "#050A1A",
        surfaceHex = "#0A1428",
        textHex = "#FFFFFF",
        textSecondaryHex = "#A0C4FF",
        accentHex = "#F0F8FF"
    )

    val AFRICAN_CONFESSIONS = NovaTheme(
        id = "african-confessions",
        name = "African Confessions",
        emoji = "🌍",
        primary = Color(0xFFC4622D),
        secondary = Color(0xFF2D5A27),
        glow = Color(0xFFD4A017),
        background = Color(0xFF120800),
        surface = Color(0xFF1E0E00),
        text = Color(0xFFFFF3E0),
        textSecondary = Color(0xFFC4922D),
        accent = Color(0xFFFFB347),
        primaryHex = "#C4622D",
        secondaryHex = "#2D5A27",
        glowHex = "#D4A017",
        backgroundHex = "#120800",
        surfaceHex = "#1E0E00",
        textHex = "#FFF3E0",
        textSecondaryHex = "#C4922D",
        accentHex = "#FFB347"
    )

    val BAD_ANGEL = NovaTheme(
        id = "bad-angel",
        name = "Bad Angel",
        emoji = "😇",
        primary = Color(0xFFCC0033),
        secondary = Color(0xFFFFFFFF),
        glow = Color(0xFFFF3366),
        background = Color(0xFF080808),
        surface = Color(0xFF141414),
        text = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFAAAAAA),
        accent = Color(0xFFFF0044),
        primaryHex = "#CC0033",
        secondaryHex = "#FFFFFF",
        glowHex = "#FF3366",
        backgroundHex = "#080808",
        surfaceHex = "#141414",
        textHex = "#FFFFFF",
        textSecondaryHex = "#AAAAAA",
        accentHex = "#FF0044"
    )

    // ── Liste complète ────────────────────────
    val ALL = listOf(
        CYBER_NOVA,
        NEON_DISCO,
        VILLAIN_ERA,
        SLAY_QUEEN,
        PINK_Y2K,
        VELVET_STAGE,
        PINK_VENOM,
        CLOUD_NINE,
        SOLARA,
        CHAOS_BORN,
        SURVIVOR,
        RAINBOW_POP,
        POP_REVOLUTION,
        AFRICAN_CONFESSIONS,
        BAD_ANGEL
    )

    // ── Par ID ────────────────────────────────
    fun getById(id: String): NovaTheme {
        return ALL.find { it.id == id } ?: CYBER_NOVA
    }
}

// ════════════════════════════════════════════
// THEME ENGINE
// ════════════════════════════════════════════
object ThemeEngine {
    private var _current: NovaTheme = NovaThemes.CYBER_NOVA
    val current: NovaTheme get() = _current

    fun apply(themeId: String) {
        _current = NovaThemes.getById(themeId)
    }

    fun toJson(): String {
        return """
            {
                "id": "${_current.id}",
                "primary": "${_current.primaryHex}",
                "secondary": "${_current.secondaryHex}",
                "glow": "${_current.glowHex}",
                "background": "${_current.backgroundHex}",
                "surface": "${_current.surfaceHex}",
                "text": "${_current.textHex}",
                "textSecondary": "${_current.textSecondaryHex}",
                "accent": "${_current.accentHex}"
            }
        """.trimIndent()
    }
}