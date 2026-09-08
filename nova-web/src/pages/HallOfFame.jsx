// ════════════════════════════════════════════
// PAGE — HALL OF FAME
// ════════════════════════════════════════════
import { useState, useEffect } from 'react'
import { useBridge } from '../hooks/useBridge'
import { timeAgo } from '../utils/format'

const PERIODS = [
    { id: 'WEEKLY', label: 'Weekly' },
    { id: 'MONTHLY', label: 'Monthly' },
    { id: 'GLOBAL', label: 'Global' }
]

const CATEGORIES = [
    { id: 'TRACK', label: '🎵 Chansons' },
    { id: 'ARTIST', label: '🎤 Artistes' },
    { id: 'ALBUM', label: '💿 Albums' }
]

const ENTRY_TYPES = {
    DIRECT_DEBUT: {
        label: '🚀 Direct Debut',
        color: '#7B2FBE',
        desc: 'Entrée directe au #1'
    },
    LONG_RUN: {
        label: '👑 Long Run',
        color: '#FFD700',
        desc: 'Règne prolongé au #1'
    },
    TRIPLE_DEBUT: {
        label: '🌍 Triple Debut',
        color: '#0D1BFF',
        desc: '#1 Daily + Weekly + Monthly'
    },
    LEGENDARY_RUN: {
        label: '🏅 Legendary Run',
        color: '#FF6B00',
        desc: '10x #1 Weekly'
    }
}

const mockEntries = [
    {
        hofId: 1,
        entityId: 1,
        entityType: 'TRACK',
        title: 'Blinding Lights',
        artist: 'The Weeknd',
        entryType: 'LONG_RUN',
        entryDate: '2024-01-15',
        weeksAt1: 15,
        badges: ['LONG_RUN', 'DIRECT_DEBUT']
    },
    {
        hofId: 2,
        entityId: 5,
        entityType: 'TRACK',
        title: 'Anti-Hero',
        artist: 'Taylor Swift',
        entryType: 'DIRECT_DEBUT',
        entryDate: '2024-03-20',
        weeksAt1: 3,
        badges: ['DIRECT_DEBUT']
    },
    {
        hofId: 3,
        entityId: 2,
        entityType: 'TRACK',
        title: 'Starboy',
        artist: 'The Weeknd',
        entryType: 'TRIPLE_DEBUT',
        entryDate: '2024-05-10',
        weeksAt1: 5,
        badges: ['TRIPLE_DEBUT']
    }
]

export default function HallOfFame() {
    const { getData, openPopup } = useBridge()
    const [period, setPeriod] = useState('WEEKLY')
    const [category, setCategory] = useState('TRACK')
    const [data, setData] = useState(null)
    const [selectedItem, setSelectedItem] = useState(null)
    const [showPopup, setShowPopup] = useState(false)

    useEffect(() => {
        const result = getData({
            type: 'hof',
            period,
            category
        })
        setData(result)
    }, [period, category])

    const entries = data?.entries || mockEntries

    const isEmpty = entries.length === 0

    return (
        <div style={{ padding: '16px' }}>

            {/* ── Sélecteur période ── */}
            <div style={{
                display: 'flex',
                gap: '8px',
                marginBottom: '12px'
            }}>
                {PERIODS.map(p => (
                    <button
                        key={p.id}
                        onClick={() => setPeriod(p.id)}
                        style={{
                            flex: 1,
                            padding: '8px',
                            borderRadius: '20px',
                            border: 'none',
                            cursor: 'pointer',
                            background: period === p.id
                                ? 'var(--primary)'
                                : 'var(--surface)',
                            color: period === p.id
                                ? 'var(--bg)'
                                : 'var(--text-secondary)',
                            fontWeight: period === p.id ? 'bold' : 'normal',
                            fontSize: '12px'
                        }}
                    >
                        {p.label}
                    </button>
                ))}
            </div>

            {/* ── Sélecteur catégorie ── */}
            <div style={{
                display: 'flex',
                gap: '8px',
                marginBottom: '16px'
            }}>
                {CATEGORIES.map(c => (
                    <button
                        key={c.id}
                        onClick={() => setCategory(c.id)}
                        style={{
                            flex: 1,
                            padding: '8px',
                            borderRadius: '12px',
                            border: `1px solid ${category === c.id
                                ? 'var(--primary)'
                                : 'transparent'}`,
                            background: 'transparent',
                            color: category === c.id
                                ? 'var(--primary)'
                                : 'var(--text-secondary)',
                            fontSize: '12px',
                            cursor: 'pointer'
                        }}
                    >
                        {c.label}
                    </button>
                ))}
            </div>

            {/* ── Stats rapides ── */}
            <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(2, 1fr)',
                gap: '8px',
                marginBottom: '16px'
            }}>
                {Object.entries(ENTRY_TYPES).map(([key, type]) => (
                    <div
                        key={key}
                        style={{
                            background: 'var(--surface)',
                            borderRadius: '12px',
                            padding: '12px',
                            borderLeft: `4px solid ${type.color}`
                        }}
                    >
                        <div style={{
                            color: type.color,
                            fontSize: '13px',
                            fontWeight: 'bold'
                        }}>
                            {type.label}
                        </div>
                        <div style={{
                            color: 'var(--text)',
                            fontSize: '22px',
                            fontWeight: 'bold',
                            marginTop: '4px'
                        }}>
                            {entries.filter(e =>
                                e.badges?.includes(key)
                            ).length}
                        </div>
                        <div style={{
                            color: 'var(--text-secondary)',
                            fontSize: '10px'
                        }}>
                            {type.desc}
                        </div>
                    </div>
                ))}
            </div>

            {/* ── Entries ── */}
            {isEmpty ? (
                <EmptyHof />
            ) : (
                entries.map(entry => (
                    <HofCard
                        key={entry.hofId}
                        entry={entry}
                        onPress={() => {
                            setSelectedItem(entry)
                            setShowPopup(true)
                        }}
                    />
                ))
            )}

            {/* ── Popup ── */}
            {showPopup && selectedItem && (
                <HofPopup
                    entry={selectedItem}
                    onClose={() => setShowPopup(false)}
                    onOpenNative={() => {
                        openPopup(
                            selectedItem.entityType.toLowerCase(),
                            selectedItem.entityId
                        )
                        setShowPopup(false)
                    }}
                />
            )}
        </div>
    )
}

// ════════════════════════════════════════════
// CARTE HOF
// ════════════════════════════════════════════
function HofCard({ entry, onPress }) {
    const mainType = ENTRY_TYPES[entry.entryType] ||
                     ENTRY_TYPES.DIRECT_DEBUT

    return (
        <div
            onClick={onPress}
            style={{
                background: 'var(--surface)',
                borderRadius: '16px',
                padding: '16px',
                marginBottom: '12px',
                cursor: 'pointer',
                border: `1px solid ${mainType.color}44`,
                boxShadow: `0 0 20px ${mainType.color}22`,
                position: 'relative',
                overflow: 'hidden'
            }}
        >
            {/* Fond décoratif */}
            <div style={{
                position: 'absolute',
                top: 0,
                right: 0,
                fontSize: '64px',
                opacity: 0.05,
                lineHeight: 1
            }}>
                🏛️
            </div>

            {/* Badges */}
            <div style={{
                display: 'flex',
                gap: '6px',
                marginBottom: '10px',
                flexWrap: 'wrap'
            }}>
                {entry.badges?.map(badge => {
                    const t = ENTRY_TYPES[badge]
                    if (!t) return null
                    return (
                        <span
                            key={badge}
                            style={{
                                padding: '3px 10px',
                                borderRadius: '20px',
                                fontSize: '11px',
                                fontWeight: 'bold',
                                background: `${t.color}33`,
                                color: t.color,
                                border: `1px solid ${t.color}66`
                            }}
                        >
                            {t.label}
                        </span>
                    )
                })}
            </div>

            {/* Titre + Artiste */}
            <div style={{
                color: 'var(--text)',
                fontSize: '16px',
                fontWeight: 'bold',
                marginBottom: '4px'
            }}>
                {entry.title || entry.name}
            </div>
            {entry.artist && (
                <div style={{
                    color: 'var(--text-secondary)',
                    fontSize: '13px',
                    marginBottom: '10px'
                }}>
                    {entry.artist}
                </div>
            )}

            {/* Stats */}
            <div style={{
                display: 'flex',
                gap: '16px',
                fontSize: '12px'
            }}>
                <div>
                    <span style={{ color: 'var(--text-secondary)' }}>
                        👑 Semaines au #1 :
                    </span>
                    <span style={{
                        color: mainType.color,
                        fontWeight: 'bold',
                        marginLeft: '4px'
                    }}>
                        {entry.weeksAt1}
                    </span>
                </div>
                <div>
                    <span style={{ color: 'var(--text-secondary)' }}>
                        📅 Consacré :
                    </span>
                    <span style={{
                        color: 'var(--text)',
                        marginLeft: '4px'
                    }}>
                        {entry.entryDate}
                    </span>
                </div>
            </div>
        </div>
    )
}

// ════════════════════════════════════════════
// POPUP HOF
// ════════════════════════════════════════════
function HofPopup({ entry, onClose, onOpenNative }) {
    const mainType = ENTRY_TYPES[entry.entryType] ||
                     ENTRY_TYPES.DIRECT_DEBUT

    return (
        <div
            onClick={onClose}
            style={{
                position: 'fixed',
                inset: 0,
                background: 'rgba(0,0,0,0.85)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                zIndex: 1000
            }}
        >
            <div
                onClick={e => e.stopPropagation()}
                style={{
                    width: '92%',
                    maxHeight: '85vh',
                    background: 'var(--surface)',
                    borderRadius: '16px',
                    overflow: 'auto',
                    border: `2px solid ${mainType.color}`,
                    boxShadow: `0 0 40px ${mainType.color}44`
                }}
            >
                {/* Bannière */}
                <div style={{
                    height: '140px',
                    background: `linear-gradient(135deg,
                        var(--bg),
                        ${mainType.color}44)`,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '8px'
                }}>
                    <div style={{ fontSize: '40px' }}>🏛️</div>
                    <div style={{
                        color: mainType.color,
                        fontWeight: 'bold',
                        fontSize: '14px'
                    }}>
                        HALL OF FAME
                    </div>
                </div>

                <div style={{ padding: '16px' }}>
                    {/* Titre */}
                    <div style={{
                        textAlign: 'center',
                        marginBottom: '16px'
                    }}>
                        <div style={{
                            color: 'var(--text)',
                            fontSize: '20px',
                            fontWeight: 'bold'
                        }}>
                            {entry.title || entry.name}
                        </div>
                        {entry.artist && (
                            <div style={{
                                color: 'var(--text-secondary)',
                                fontSize: '14px',
                                marginTop: '4px'
                            }}>
                                {entry.artist}
                            </div>
                        )}
                    </div>

                    {/* Badges */}
                    <div style={{
                        display: 'flex',
                        gap: '8px',
                        flexWrap: 'wrap',
                        justifyContent: 'center',
                        marginBottom: '16px'
                    }}>
                        {entry.badges?.map(badge => {
                            const t = ENTRY_TYPES[badge]
                            if (!t) return null
                            return (
                                <div
                                    key={badge}
                                    style={{
                                        padding: '8px 16px',
                                        borderRadius: '20px',
                                        background: `${t.color}22`,
                                        color: t.color,
                                        border: `1px solid ${t.color}`,
                                        fontSize: '13px',
                                        fontWeight: 'bold'
                                    }}
                                >
                                    {t.label}
                                </div>
                            )
                        })}
                    </div>

                    {/* Stats */}
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(2, 1fr)',
                        gap: '8px',
                        marginBottom: '16px'
                    }}>
                        <StatBox
                            label="Semaines au #1"
                            value={entry.weeksAt1}
                            color={mainType.color}
                        />
                        <StatBox
                            label="Date d'entrée"
                            value={entry.entryDate}
                            color="var(--text)"
                        />
                    </div>

                    {/* Message */}
                    <div style={{
                        background: 'var(--bg)',
                        borderRadius: '12px',
                        padding: '12px',
                        marginBottom: '16px',
                        textAlign: 'center',
                        color: 'var(--text-secondary)',
                        fontSize: '13px',
                        fontStyle: 'italic'
                    }}>
                        "Consacré le {entry.entryDate} —
                        Une entrée dans la légende."
                    </div>

                    {/* Bouton */}
                    <button
                        onClick={onClose}
                        style={{
                            width: '100%',
                            padding: '12px',
                            background: mainType.color,
                            color: '#000',
                            border: 'none',
                            borderRadius: '12px',
                            fontWeight: 'bold',
                            fontSize: '14px',
                            cursor: 'pointer'
                        }}
                    >
                        FERMER
                    </button>
                </div>
            </div>
        </div>
    )
}

function StatBox({ label, value, color }) {
    return (
        <div style={{
            background: 'var(--bg)',
            borderRadius: '12px',
            padding: '12px',
            textAlign: 'center'
        }}>
            <div style={{
                color,
                fontSize: '20px',
                fontWeight: 'bold'
            }}>
                {value}
            </div>
            <div style={{
                color: 'var(--text-secondary)',
                fontSize: '11px',
                marginTop: '4px'
            }}>
                {label}
            </div>
        </div>
    )
}

function EmptyHof() {
    return (
        <div style={{
            textAlign: 'center',
            padding: '60px 20px',
            color: 'var(--text-secondary)'
        }}>
            <div style={{ fontSize: '48px', marginBottom: '16px' }}>🏛️</div>
            <div style={{ fontSize: '16px', marginBottom: '8px' }}>
                Le temple est vide pour l'instant
            </div>
            <div style={{ fontSize: '13px' }}>
                Entre directement au #1 pour rejoindre le Hall of Fame !
            </div>
        </div>
    )
}