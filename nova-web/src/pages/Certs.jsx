// ════════════════════════════════════════════
// PAGE — CERTIFICATIONS
// ════════════════════════════════════════════
import { useState, useEffect } from 'react'
import { useBridge } from '../hooks/useBridge'
import { formatCertLevel, formatDate, timeAgo } from '../utils/format'

const CATEGORIES = ['TRACK', 'ALBUM']

const CERT_LEVELS = [
    { id: 'DIAMOND', label: '💎 Diamant', color: '#00FFFF' },
    { id: 'PLATINUM', label: '🥇 Platine', color: '#E5E4E2' },
    { id: 'GOLD', label: '🥈 Or', color: '#FFD700' },
    { id: 'SILVER', label: '🥉 Argent', color: '#C0C0C0' }
]

export default function Certs() {
    const { getData, openPopup } = useBridge()
    const [category, setCategory] = useState('TRACK')
    const [data, setData] = useState(null)
    const [selectedItem, setSelectedItem] = useState(null)
    const [showPopup, setShowPopup] = useState(false)

    useEffect(() => {
        const result = getData({
            type: 'certs',
            category
        })
        setData(result)
    }, [category])

    // Mock items pour le dev
    const mockItems = [
        {
            certificationId: 1,
            entityId: 1,
            title: "Blinding Lights",
            artist: "The Weeknd",
            level: "DIAMOND",
            multiplier: 2,
            playCountAtCert: 700,
            certifiedAt: Date.now() - 86400000
        },
        {
            certificationId: 2,
            entityId: 2,
            title: "Starboy",
            artist: "The Weeknd",
            level: "PLATINUM",
            multiplier: 1,
            playCountAtCert: 100,
            certifiedAt: Date.now() - 172800000
        },
        {
            certificationId: 3,
            entityId: 3,
            title: "Save Your Tears",
            artist: "The Weeknd",
            level: "GOLD",
            multiplier: 1,
            playCountAtCert: 50,
            certifiedAt: Date.now() - 259200000
        },
        {
            certificationId: 4,
            entityId: 4,
            title: "Anti-Hero",
            artist: "Taylor Swift",
            level: "SILVER",
            multiplier: 1,
            playCountAtCert: 25,
            certifiedAt: Date.now() - 345600000
        }
    ]

    const items = data?.items || mockItems

    return (
        <div style={{ padding: '16px' }}>

            {/* ── Sélecteur catégorie ── */}
            <div style={{
                display: 'flex',
                gap: '8px',
                marginBottom: '16px'
            }}>
                {CATEGORIES.map(c => (
                    <button
                        key={c}
                        onClick={() => setCategory(c)}
                        style={{
                            flex: 1,
                            padding: '10px',
                            borderRadius: '12px',
                            border: 'none',
                            cursor: 'pointer',
                            background: category === c
                                ? 'var(--primary)'
                                : 'var(--surface)',
                            color: category === c
                                ? 'var(--bg)'
                                : 'var(--text-secondary)',
                            fontWeight: category === c ? 'bold' : 'normal',
                            fontSize: '13px'
                        }}
                    >
                        {c === 'TRACK' ? '🎵 Chansons' : '💿 Albums'}
                    </button>
                ))}
            </div>

            {/* ── Radar ── */}
            <RadarSection items={items} />

            {/* ── Stats rapides ── */}
            <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(4, 1fr)',
                gap: '8px',
                marginBottom: '16px'
            }}>
                {CERT_LEVELS.map(level => (
                    <div
                        key={level.id}
                        style={{
                            background: 'var(--surface)',
                            borderRadius: '12px',
                            padding: '10px',
                            textAlign: 'center',
                            borderTop: `3px solid ${level.color}`
                        }}
                    >
                        <div style={{
                            color: level.color,
                            fontSize: '18px',
                            marginBottom: '4px'
                        }}>
                            {level.label.split(' ')[0]}
                        </div>
                        <div style={{
                            color: 'var(--text)',
                            fontWeight: 'bold',
                            fontSize: '18px'
                        }}>
                            {items.filter(i => i.level === level.id).length}
                        </div>
                    </div>
                ))}
            </div>

            {/* ── Liste ── */}
            {items.length === 0 ? (
                <EmptyCerts />
            ) : (
                items.map(item => (
                    <CertRow
                        key={item.certificationId}
                        item={item}
                        onPress={() => {
                            setSelectedItem(item)
                            setShowPopup(true)
                        }}
                    />
                ))
            )}

            {/* ── Popup ── */}
            {showPopup && selectedItem && (
                <CertPopup
                    item={selectedItem}
                    items={items}
                    onClose={() => setShowPopup(false)}
                    onOpenNative={() => {
                        openPopup('track', selectedItem.entityId)
                        setShowPopup(false)
                    }}
                />
            )}
        </div>
    )
}

// ════════════════════════════════════════════
// RADAR
// ════════════════════════════════════════════
function RadarSection({ items }) {
    const sorted = [...items].sort((a, b) => {
        const levelOrder = {
            DIAMOND: 4,
            PLATINUM: 3,
            GOLD: 2,
            SILVER: 1
        }
        return levelOrder[b.level] - levelOrder[a.level]
    })

    const next5 = sorted.slice(0, 5)

    if (next5.length === 0) return null

    return (
        <div style={{
            background: 'var(--surface)',
            borderRadius: '16px',
            padding: '16px',
            marginBottom: '16px',
            borderLeft: '3px solid var(--primary)'
        }}>
            <div style={{
                color: 'var(--primary)',
                fontWeight: 'bold',
                fontSize: '14px',
                marginBottom: '12px'
            }}>
                🎯 Radar — Prochaines certifications
            </div>
            {next5.map(item => (
                <div
                    key={item.certificationId}
                    style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        marginBottom: '8px'
                    }}
                >
                    <div style={{
                        color: 'var(--text)',
                        fontSize: '13px'
                    }}>
                        {item.title || item.name}
                    </div>
                    <div style={{
                        color: getCertColor(item.level),
                        fontSize: '12px',
                        fontWeight: 'bold'
                    }}>
                        {formatCertLevel(item.level, item.multiplier)}
                    </div>
                </div>
            ))}
        </div>
    )
}

// ════════════════════════════════════════════
// LIGNE CERTIFICATION
// ════════════════════════════════════════════
function CertRow({ item, onPress }) {
    const certColor = getCertColor(item.level)

    return (
        <div
            onClick={onPress}
            style={{
                display: 'flex',
                alignItems: 'center',
                gap: '12px',
                padding: '14px',
                marginBottom: '8px',
                background: 'var(--surface)',
                borderRadius: '14px',
                cursor: 'pointer',
                borderLeft: `4px solid ${certColor}`,
                boxShadow: item.level === 'DIAMOND'
                    ? `0 0 15px ${certColor}33`
                    : 'none'
            }}
        >
            {/* Badge niveau */}
            <div style={{
                fontSize: '28px',
                minWidth: '40px',
                textAlign: 'center'
            }}>
                {item.level === 'DIAMOND' ? '💎'.repeat(Math.min(item.multiplier, 3)) :
                 item.level === 'PLATINUM' ? '🥇' :
                 item.level === 'GOLD' ? '🥈' : '🥉'}
            </div>

            {/* Infos */}
            <div style={{ flex: 1 }}>
                <div style={{
                    color: 'var(--text)',
                    fontSize: '14px',
                    fontWeight: '600'
                }}>
                    {item.title || item.name}
                </div>
                {item.artist && (
                    <div style={{
                        color: 'var(--text-secondary)',
                        fontSize: '12px'
                    }}>
                        {item.artist}
                    </div>
                )}
                <div style={{
                    color: 'var(--text-secondary)',
                    fontSize: '11px',
                    marginTop: '2px'
                }}>
                    {timeAgo(item.certifiedAt)}
                </div>
            </div>

            {/* Niveau + écoutes */}
            <div style={{ textAlign: 'right' }}>
                <div style={{
                    color: certColor,
                    fontWeight: 'bold',
                    fontSize: '13px'
                }}>
                    {formatCertLevel(item.level, item.multiplier)}
                </div>
                <div style={{
                    color: 'var(--text-secondary)',
                    fontSize: '11px',
                    marginTop: '2px'
                }}>
                    {item.playCountAtCert} écoutes
                </div>
            </div>
        </div>
    )
}

// ════════════════════════════════════════════
// POPUP CERTIFICATION
// ════════════════════════════════════════════
function CertPopup({ item, items, onClose, onOpenNative }) {
    const certColor = getCertColor(item.level)

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
                    border: `2px solid ${certColor}`,
                    boxShadow: `0 0 30px ${certColor}44`
                }}
            >
                {/* Bannière */}
                <div style={{
                    height: '120px',
                    background: `linear-gradient(135deg, var(--bg), ${certColor}33)`,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '48px'
                }}>
                    {item.level === 'DIAMOND' ? '💎' :
                     item.level === 'PLATINUM' ? '🥇' :
                     item.level === 'GOLD' ? '🥈' : '🥉'}
                </div>

                <div style={{ padding: '16px' }}>
                    {/* Titre */}
                    <div style={{ textAlign: 'center', marginBottom: '16px' }}>
                        <div style={{
                            color: 'var(--text)',
                            fontSize: '18px',
                            fontWeight: 'bold'
                        }}>
                            {item.title || item.name}
                        </div>
                        {item.artist && (
                            <div style={{
                                color: 'var(--text-secondary)',
                                fontSize: '14px'
                            }}>
                                {item.artist}
                            </div>
                        )}
                        <div style={{
                            color: certColor,
                            fontSize: '16px',
                            fontWeight: 'bold',
                            marginTop: '8px'
                        }}>
                            {formatCertLevel(item.level, item.multiplier)}
                        </div>
                    </div>

                    {/* Tous les paliers */}
                    <div style={{
                        background: 'var(--bg)',
                        borderRadius: '12px',
                        padding: '12px',
                        marginBottom: '12px'
                    }}>
                        <div style={{
                            color: 'var(--primary)',
                            fontSize: '13px',
                            fontWeight: 'bold',
                            marginBottom: '8px'
                        }}>
                            📅 Historique des certifications
                        </div>
                        {['SILVER', 'GOLD', 'PLATINUM', 'DIAMOND'].map(level => (
                            <div
                                key={level}
                                style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    padding: '6px 0',
                                    borderBottom: '1px solid rgba(255,255,255,0.05)',
                                    opacity: isLevelReached(item.level, level)
                                        ? 1 : 0.3
                                }}
                            >
                                <span style={{
                                    color: getCertColor(level),
                                    fontSize: '13px'
                                }}>
                                    {level === 'SILVER' ? '🥉 Argent' :
                                     level === 'GOLD' ? '🥈 Or' :
                                     level === 'PLATINUM' ? '🥇 Platine' :
                                     '💎 Diamant'}
                                </span>
                                <span style={{
                                    color: 'var(--text-secondary)',
                                    fontSize: '12px'
                                }}>
                                    {isLevelReached(item.level, level)
                                        ? timeAgo(item.certifiedAt)
                                        : '—'
                                    }
                                </span>
                            </div>
                        ))}
                    </div>

                    {/* Stats */}
                    <div style={{
                        display: 'flex',
                        gap: '8px',
                        marginBottom: '16px'
                    }}>
                        <div style={{
                            flex: 1,
                            background: 'var(--bg)',
                            borderRadius: '12px',
                            padding: '12px',
                            textAlign: 'center'
                        }}>
                            <div style={{
                                color: 'var(--primary)',
                                fontSize: '22px',
                                fontWeight: 'bold'
                            }}>
                                {item.playCountAtCert}
                            </div>
                            <div style={{
                                color: 'var(--text-secondary)',
                                fontSize: '11px'
                            }}>
                                Écoutes
                            </div>
                        </div>
                        <div style={{
                            flex: 1,
                            background: 'var(--bg)',
                            borderRadius: '12px',
                            padding: '12px',
                            textAlign: 'center'
                        }}>
                            <div style={{
                                color: certColor,
                                fontSize: '22px',
                                fontWeight: 'bold'
                            }}>
                                {item.multiplier}x
                            </div>
                            <div style={{
                                color: 'var(--text-secondary)',
                                fontSize: '11px'
                            }}>
                                Multiplicateur
                            </div>
                        </div>
                    </div>

                    {/* Boutons */}
                    <button
                        onClick={onClose}
                        style={{
                            width: '100%',
                            padding: '12px',
                            background: 'var(--primary)',
                            color: 'var(--bg)',
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

// ════════════════════════════════════════════
// UTILITAIRES
// ════════════════════════════════════════════
function getCertColor(level) {
    switch (level) {
        case 'DIAMOND': return '#00FFFF'
        case 'PLATINUM': return '#E5E4E2'
        case 'GOLD': return '#FFD700'
        case 'SILVER': return '#C0C0C0'
        default: return 'var(--primary)'
    }
}

function isLevelReached(currentLevel, checkLevel) {
    const order = ['SILVER', 'GOLD', 'PLATINUM', 'DIAMOND']
    return order.indexOf(currentLevel) >= order.indexOf(checkLevel)
}

function EmptyCerts() {
    return (
        <div style={{
            textAlign: 'center',
            padding: '60px 20px',
            color: 'var(--text-secondary)'
        }}>
            <div style={{ fontSize: '48px', marginBottom: '16px' }}>💎</div>
            <div style={{ fontSize: '16px', marginBottom: '8px' }}>
                Aucune certification pour l'instant
            </div>
            <div style={{ fontSize: '13px' }}>
                Écoute 25 fois un titre pour obtenir l'Argent !
            </div>
        </div>
    )
}