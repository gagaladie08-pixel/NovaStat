// ════════════════════════════════════════════
// PAGE — NOVA AWARDS
// ════════════════════════════════════════════
import { useState, useEffect } from 'react'
import { useBridge } from '../hooks/useBridge'

const AWARD_CONFIG = {
    SONG_OF_YEAR: {
        emoji: '🎵',
        label: 'Chanson de l\'année',
        color: '#FFD700',
        hero: true
    },
    ARTIST_OF_YEAR: {
        emoji: '🎤',
        label: 'Artiste de l\'année',
        color: '#FFD700',
        hero: true
    },
    ALBUM_OF_YEAR: {
        emoji: '💿',
        label: 'Album de l\'année',
        color: '#FFD700',
        hero: true
    },
    BIGGEST_PROGRESSION: {
        emoji: '📈',
        label: 'Plus grosse progression',
        color: '#2ECC71',
        hero: false
    },
    REVELATION: {
        emoji: '🆕',
        label: 'Révélation de l\'année',
        color: '#9B59B6',
        hero: false
    },
    BEST_LOYALTY: {
        emoji: '🤝',
        label: 'Meilleure fidélité',
        color: '#3498DB',
        hero: false
    },
    BEST_CERTIFICATION: {
        emoji: '💎',
        label: 'Meilleure certification',
        color: '#00FFFF',
        hero: false
    },
    LONGEST_STREAK: {
        emoji: '🔥',
        label: 'Plus long streak',
        color: '#E74C3C',
        hero: false
    },
    LONGEST_SESSION: {
        emoji: '⏱️',
        label: 'Session la plus longue',
        color: '#E67E22',
        hero: false
    }
}

const mockAwards = [
    {
        category: 'SONG_OF_YEAR',
        title: 'Blinding Lights',
        artist: 'The Weeknd',
        value: 134,
        message: "Tu l'as écoutée 134 fois — soit 7h 22min de ta vie 🔥"
    },
    {
        category: 'ARTIST_OF_YEAR',
        name: 'The Weeknd',
        value: 299,
        message: "The Weeknd t'a accompagné 299 fois — 42% de toutes tes écoutes 👑"
    },
    {
        category: 'ALBUM_OF_YEAR',
        title: 'After Hours',
        artist: 'The Weeknd',
        value: 210,
        message: "Tu as tourné cet album en boucle — 210 écoutes cette année 🎵"
    },
    {
        category: 'BIGGEST_PROGRESSION',
        title: 'As It Was',
        artist: 'Harry Styles',
        value: 89,
        message: "Inconnu en début d'année, 89 écoutes rien qu'en décembre 🚀"
    },
    {
        category: 'REVELATION',
        name: 'Harry Styles',
        value: 65,
        message: "Découvert en mars, déjà 65 écoutes à son actif ✨"
    },
    {
        category: 'BEST_LOYALTY',
        name: 'Taylor Swift',
        value: 10,
        message: "Présent 10 mois sur 12 — une fidélité sans faille 💙"
    },
    {
        category: 'BEST_CERTIFICATION',
        title: 'Blinding Lights',
        artist: 'The Weeknd',
        value: 700,
        message: "Ton cheval de bataille — 700 écoutes et un Diamant bien mérité 💎"
    },
    {
        category: 'LONGEST_STREAK',
        value: 45,
        message: "45 jours consécutifs sans manquer un seul jour 🔥"
    },
    {
        category: 'LONGEST_SESSION',
        value: 187,
        message: "Le 14 juin — 3h 7min de musique non-stop 🎧"
    }
]

export default function Awards() {
    const { getData, openPopup } = useBridge()
    const [data, setData] = useState(null)
    const [selectedYear, setSelectedYear] = useState(
        new Date().getFullYear()
    )
    const [revealed, setRevealed] = useState(false)

    useEffect(() => {
        const result = getData({
            type: 'awards',
            year: selectedYear
        })
        setData(result)
    }, [selectedYear])

    const awards = data?.awards || mockAwards
    const years = data?.allYears || [new Date().getFullYear()]

    const heroAwards = awards.filter(a =>
        AWARD_CONFIG[a.category]?.hero
    )
    const normalAwards = awards.filter(a =>
        !AWARD_CONFIG[a.category]?.hero
    )

    return (
        <div style={{ padding: '16px' }}>

            {/* ── Sélecteur année ── */}
            <div style={{
                display: 'flex',
                gap: '8px',
                marginBottom: '16px',
                overflowX: 'auto'
            }}>
                {years.map(year => (
                    <button
                        key={year}
                        onClick={() => setSelectedYear(year)}
                        style={{
                            padding: '8px 16px',
                            borderRadius: '20px',
                            border: 'none',
                            cursor: 'pointer',
                            background: selectedYear === year
                                ? 'var(--primary)'
                                : 'var(--surface)',
                            color: selectedYear === year
                                ? 'var(--bg)'
                                : 'var(--text-secondary)',
                            fontWeight: 'bold',
                            fontSize: '13px',
                            position: 'relative'
                        }}
                    >
                        {year}
                        {year === new Date().getFullYear() && (
                            <span style={{
                                position: 'absolute',
                                top: '-4px',
                                right: '-4px',
                                background: '#E74C3C',
                                color: '#fff',
                                fontSize: '8px',
                                padding: '2px 4px',
                                borderRadius: '6px',
                                fontWeight: 'bold'
                            }}>
                                LIVE
                            </span>
                        )}
                    </button>
                ))}
            </div>

            {/* ── Header cérémonial ── */}
            <div style={{
                textAlign: 'center',
                padding: '20px',
                background: 'var(--surface)',
                borderRadius: '16px',
                marginBottom: '16px',
                border: '1px solid #FFD70044',
                boxShadow: '0 0 30px #FFD70022'
            }}>
                <div style={{ fontSize: '40px', marginBottom: '8px' }}>
                    🏆
                </div>
                <div style={{
                    color: '#FFD700',
                    fontSize: '20px',
                    fontWeight: 'bold',
                    letterSpacing: '2px'
                }}>
                    NOVA AWARDS
                </div>
                <div style={{
                    color: 'var(--text-secondary)',
                    fontSize: '14px',
                    marginTop: '4px'
                }}>
                    {selectedYear} — Tes moments musicaux de l'année
                </div>
            </div>

            {/* ── Awards Hero (les 3 grands) ── */}
            {heroAwards.map(award => (
                <HeroAwardCard key={award.category} award={award} />
            ))}

            {/* ── Awards normaux ── */}
            <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(2, 1fr)',
                gap: '8px',
                marginBottom: '16px'
            }}>
                {normalAwards.map(award => (
                    <NormalAwardCard key={award.category} award={award} />
                ))}
            </div>

            {/* ── Message de fin ── */}
            <div style={{
                textAlign: 'center',
                padding: '20px',
                color: 'var(--text-secondary)',
                fontSize: '13px',
                fontStyle: 'italic'
            }}>
                ✨ Continue d'écouter pour enrichir tes awards !
            </div>
        </div>
    )
}

// ════════════════════════════════════════════
// CARTE HERO
// ════════════════════════════════════════════
function HeroAwardCard({ award }) {
    const config = AWARD_CONFIG[award.category]
    if (!config) return null

    return (
        <div style={{
            background: 'var(--surface)',
            borderRadius: '16px',
            padding: '20px',
            marginBottom: '12px',
            border: `2px solid ${config.color}66`,
            boxShadow: `0 0 30px ${config.color}22`,
            position: 'relative',
            overflow: 'hidden'
        }}>
            {/* Fond décoratif */}
            <div style={{
                position: 'absolute',
                top: '-10px',
                right: '-10px',
                fontSize: '80px',
                opacity: 0.06
            }}>
                {config.emoji}
            </div>

            {/* Badge */}
            <div style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: '6px',
                background: `${config.color}22`,
                color: config.color,
                padding: '4px 12px',
                borderRadius: '20px',
                fontSize: '12px',
                fontWeight: 'bold',
                marginBottom: '12px'
            }}>
                {config.emoji} {config.label}
            </div>

            {/* Gagnant */}
            <div style={{
                color: 'var(--text)',
                fontSize: '20px',
                fontWeight: 'bold',
                marginBottom: '4px'
            }}>
                {award.title || award.name}
            </div>
            {award.artist && (
                <div style={{
                    color: 'var(--text-secondary)',
                    fontSize: '14px',
                    marginBottom: '12px'
                }}>
                    {award.artist}
                </div>
            )}

            {/* Valeur */}
            <div style={{
                color: config.color,
                fontSize: '28px',
                fontWeight: 'bold',
                marginBottom: '8px'
            }}>
                {award.value}
                <span style={{
                    fontSize: '14px',
                    marginLeft: '4px',
                    color: 'var(--text-secondary)'
                }}>
                    écoutes
                </span>
            </div>

            {/* Message */}
            <div style={{
                background: 'var(--bg)',
                borderRadius: '10px',
                padding: '10px 12px',
                color: 'var(--text-secondary)',
                fontSize: '12px',
                fontStyle: 'italic',
                lineHeight: '1.5'
            }}>
                {award.message}
            </div>
        </div>
    )
}

// ════════════════════════════════════════════
// CARTE NORMALE
// ════════════════════════════════════════════
function NormalAwardCard({ award }) {
    const config = AWARD_CONFIG[award.category]
    if (!config) return null

    return (
        <div style={{
            background: 'var(--surface)',
            borderRadius: '14px',
            padding: '14px',
            border: `1px solid ${config.color}44`,
            display: 'flex',
            flexDirection: 'column',
            gap: '6px'
        }}>
            {/* Emoji + label */}
            <div style={{
                color: config.color,
                fontSize: '12px',
                fontWeight: 'bold'
            }}>
                {config.emoji} {config.label}
            </div>

            {/* Gagnant */}
            <div style={{
                color: 'var(--text)',
                fontSize: '14px',
                fontWeight: '600'
            }}>
                {award.title || award.name || '—'}
            </div>
            {award.artist && (
                <div style={{
                    color: 'var(--text-secondary)',
                    fontSize: '11px'
                }}>
                    {award.artist}
                </div>
            )}

            {/* Valeur */}
            <div style={{
                color: config.color,
                fontSize: '20px',
                fontWeight: 'bold'
            }}>
                {award.value}
            </div>

            {/* Message court */}
            <div style={{
                color: 'var(--text-secondary)',
                fontSize: '10px',
                fontStyle: 'italic',
                lineHeight: '1.4'
            }}>
                {award.message}
            </div>
        </div>
    )
}