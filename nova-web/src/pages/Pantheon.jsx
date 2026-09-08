// ════════════════════════════════════════════
// PAGE — PANTHÉON
// ════════════════════════════════════════════
import { useState, useEffect } from 'react'
import { useBridge } from '../hooks/useBridge'
import { formatPantheonStatus, timeAgo } from '../utils/format'

const STATUSES = [
    {
        id: 'MYTHIQUE',
        label: '✨ Mythique',
        color: '#FF69B4',
        glow: '#FF69B4',
        threshold: 7000
    },
    {
        id: 'LEGENDE',
        label: '🏛️ Légende',
        color: '#C0392B',
        glow: '#C0392B',
        threshold: 3650
    },
    {
        id: 'MEGASTAR',
        label: '👑 Megastar',
        color: '#FFD700',
        glow: '#FFD700',
        threshold: 1250
    },
    {
        id: 'SUPERSTAR',
        label: '🌟 Superstar',
        color: '#9B59B6',
        glow: '#9B59B6',
        threshold: 650
    },
    {
        id: 'STAR',
        label: '⭐ Star',
        color: '#4A90E2',
        glow: '#4A90E2',
        threshold: 425
    }
]

const mockArtists = [
    {
        artistId: 1,
        name: "The Weeknd",
        currentStatus: "MYTHIQUE",
        playCount: 7500,
        distinctTracks: 45,
        distinctAlbums: 6,
        statusDate: Date.now() - 86400000
    },
    {
        artistId: 2,
        name: "Taylor Swift",
        currentStatus: "MEGASTAR",
        playCount: 1500,
        distinctTracks: 28,
        distinctAlbums: 4,
        statusDate: Date.now() - 172800000
    },
    {
        artistId: 3,
        name: "Harry Styles",
        currentStatus: "SUPERSTAR",
        playCount: 750,
        distinctTracks: 15,
        distinctAlbums: 2,
        statusDate: Date.now() - 259200000
    }
]

export default function Pantheon() {
    const { getData, openPopup } = useBridge()
    const [data, setData] = useState(null)
    const [search, setSearch] = useState('')
    const [selectedArtist, setSelectedArtist] = useState(null)
    const [showPopup, setShowPopup] = useState(false)

    useEffect(() => {
        const result = getData({ type: 'pantheon' })
        setData(result)
    }, [])

    const artists = data?.artists || mockArtists
    const filtered = artists.filter(a =>
        a.name.toLowerCase().includes(search.toLowerCase())
    )

    // Grouper par statut
    const grouped = STATUSES.map(s => ({
        status: s,
        artists: filtered.filter(a => a.currentStatus === s.id)
    })).filter(g => g.artists.length > 0)

    return (
        <div style={{ padding: '16px' }}>

            {/* ── Stats rapides ── */}
            <div style={{
                display: 'flex',
                gap: '6px',
                marginBottom: '16px',
                overflowX: 'auto'
            }}>
                {STATUSES.map(s => (
                    <div
                        key={s.id}
                        style={{
                            background: 'var(--surface)',
                            borderRadius: '12px',
                            padding: '10px 12px',
                            textAlign: 'center',
                            minWidth: '64px',
                            borderTop: `3px solid ${s.color}`,
                            boxShadow: `0 0 10px ${s.color}22`
                        }}
                    >
                        <div style={{
                            fontSize: '16px',
                            marginBottom: '4px'
                        }}>
                            {s.label.split(' ')[0]}
                        </div>
                        <div style={{
                            color: s.color,
                            fontWeight: 'bold',
                            fontSize: '20px'
                        }}>
                            {data?.counts?.[s.id] ||
                             artists.filter(a =>
                                a.currentStatus === s.id
                             ).length}
                        </div>
                    </div>
                ))}
            </div>

            {/* ── Recherche ── */}
            <div style={{
                background: 'var(--surface)',
                borderRadius: '12px',
                padding: '10px 16px',
                marginBottom: '16px',
                display: 'flex',
                alignItems: 'center',
                gap: '8px'
            }}>
                <span>🔍</span>
                <input
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                    placeholder="Rechercher un artiste..."
                    style={{
                        background: 'none',
                        border: 'none',
                        outline: 'none',
                        color: 'var(--text)',
                        fontSize: '14px',
                        width: '100%'
                    }}
                />
            </div>

            {/* ── Liste groupée par statut ── */}
            {filtered.length === 0 ? (
                <EmptyPantheon />
            ) : (
                grouped.map(({ status, artists: statusArtists }) => (
                    <div key={status.id}>
                        {/* Header statut */}
                        <div style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '8px',
                            marginBottom: '8px',
                            marginTop: '12px'
                        }}>
                            <div style={{
                                height: '1px',
                                flex: 1,
                                background: `${status.color}44`
                            }} />
                            <span style={{
                                color: status.color,
                                fontSize: '13px',
                                fontWeight: 'bold'
                            }}>
                                {status.label}
                            </span>
                            <div style={{
                                height: '1px',
                                flex: 1,
                                background: `${status.color}44`
                            }} />
                        </div>

                        {/* Artistes */}
                        {statusArtists.map(artist => (
                            <ArtistRow
                                key={artist.artistId}
                                artist={artist}
                                status={status}
                                onPress={() => {
                                    setSelectedArtist(artist)
                                    setShowPopup(true)
                                }}
                            />
                        ))}
                    </div>
                ))
            )}

            {/* ── Popup ── */}
            {showPopup && selectedArtist && (
                <PantheonPopup
                    artist={selectedArtist}
                    onClose={() => setShowPopup(false)}
                    onOpenNative={() => {
                        openPopup('artist', selectedArtist.artistId)
                        setShowPopup(false)
                    }}
                />
            )}
        </div>
    )
}

// ════════════════════════════════════════════
// LIGNE ARTISTE
// ════════════════════════════════════════════
function ArtistRow({ artist, status, onPress }) {
    const nextStatus = STATUSES[
        STATUSES.findIndex(s => s.id === artist.currentStatus) - 1
    ]

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
                borderLeft: `4px solid ${status.color}`,
                boxShadow: artist.currentStatus === 'MYTHIQUE'
                    ? `0 0 20px ${status.color}33`
                    : 'none'
            }}
        >
            {/* Photo placeholder */}
            <div style={{
                width: '48px',
                height: '48px',
                borderRadius: '50%',
                background: `${status.color}33`,
                border: `2px solid ${status.color}`,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '20px',
                flexShrink: 0
            }}>
                🎤
            </div>

            {/* Infos */}
            <div style={{ flex: 1 }}>
                <div style={{
                    color: 'var(--text)',
                    fontSize: '15px',
                    fontWeight: 'bold'
                }}>
                    {artist.name}
                </div>
                <div style={{
                    color: status.color,
                    fontSize: '12px',
                    fontWeight: 'bold',
                    marginTop: '2px'
                }}>
                    {status.label}
                </div>
                <div style={{
                    color: 'var(--text-secondary)',
                    fontSize: '11px',
                    marginTop: '2px'
                }}>
                    {artist.distinctTracks} titres •
                    {artist.distinctAlbums} albums
                </div>

                {/* Barre progression vers prochain statut */}
                {nextStatus && (
                    <div style={{ marginTop: '6px' }}>
                        <div style={{
                            height: '3px',
                            background: 'var(--bg)',
                            borderRadius: '2px',
                            overflow: 'hidden'
                        }}>
                            <div style={{
                                height: '100%',
                                width: `${Math.min(
                                    (artist.playCount / nextStatus.threshold) * 100,
                                    100
                                )}%`,
                                background: nextStatus.color,
                                borderRadius: '2px',
                                transition: 'width 0.3s ease'
                            }} />
                        </div>
                    </div>
                )}
            </div>

            {/* Écoutes */}
            <div style={{ textAlign: 'right', flexShrink: 0 }}>
                <div style={{
                    color: status.color,
                    fontWeight: 'bold',
                    fontSize: '16px'
                }}>
                    {artist.playCount}
                </div>
                <div style={{
                    color: 'var(--text-secondary)',
                    fontSize: '10px'
                }}>
                    écoutes
                </div>
            </div>
        </div>
    )
}

// ════════════════════════════════════════════
// POPUP PANTHÉON
// ════════════════════════════════════════════
function PantheonPopup({ artist, onClose, onOpenNative }) {
    const status = STATUSES.find(s => s.id === artist.currentStatus) ||
                   STATUSES[4]

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
                    border: `2px solid ${status.color}`,
                    boxShadow: `0 0 40px ${status.color}44`
                }}
            >
                {/* Bannière */}
                <div style={{
                    height: '140px',
                    background: `linear-gradient(135deg,
                        var(--bg),
                        ${status.color}33)`,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '8px'
                }}>
                    <div style={{
                        width: '72px',
                        height: '72px',
                        borderRadius: '50%',
                        background: `${status.color}33`,
                        border: `3px solid ${status.color}`,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: '32px'
                    }}>
                        🎤
                    </div>
                </div>

                <div style={{ padding: '16px' }}>
                    {/* Nom + statut */}
                    <div style={{ textAlign: 'center', marginBottom: '16px' }}>
                        <div style={{
                            color: 'var(--text)',
                            fontSize: '22px',
                            fontWeight: 'bold',
                            textTransform: 'uppercase'
                        }}>
                            {artist.name}
                        </div>
                        <div style={{
                            color: status.color,
                            fontSize: '16px',
                            fontWeight: 'bold',
                            marginTop: '6px',
                            textShadow: `0 0 10px ${status.color}`
                        }}>
                            {status.label}
                        </div>
                    </div>

                    {/* Historique statuts */}
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
                            📊 Progression
                        </div>
                        {STATUSES.slice().reverse().map(s => {
                            const reached = STATUSES.indexOf(s) >=
                                STATUSES.findIndex(
                                    st => st.id === artist.currentStatus
                                )
                            return (
                                <div
                                    key={s.id}
                                    style={{
                                        display: 'flex',
                                        justifyContent: 'space-between',
                                        alignItems: 'center',
                                        padding: '6px 0',
                                        borderBottom:
                                            '1px solid rgba(255,255,255,0.05)',
                                        opacity: reached ? 1 : 0.3
                                    }}
                                >
                                    <span style={{
                                        color: s.color,
                                        fontSize: '13px'
                                    }}>
                                        {s.label}
                                    </span>
                                    <span style={{
                                        color: 'var(--text-secondary)',
                                        fontSize: '12px'
                                    }}>
                                        {reached
                                            ? `${s.threshold}+ écoutes ✅`
                                            : `${s.threshold} écoutes`
                                        }
                                    </span>
                                </div>
                            )
                        })}
                    </div>

                    {/* Stats */}
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(3, 1fr)',
                        gap: '8px',
                        marginBottom: '16px'
                    }}>
                        <StatBoxP
                            label="Écoutes"
                            value={artist.playCount}
                            color={status.color}
                        />
                        <StatBoxP
                            label="Titres"
                            value={artist.distinctTracks}
                            color="var(--text)"
                        />
                        <StatBoxP
                            label="Albums"
                            value={artist.distinctAlbums}
                            color="var(--text)"
                        />
                    </div>

                    {/* Bouton */}
                    <button
                        onClick={onClose}
                        style={{
                            width: '100%',
                            padding: '12px',
                            background: status.color,
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

function StatBoxP({ label, value, color }) {
    return (
        <div style={{
            background: 'var(--bg)',
            borderRadius: '12px',
            padding: '10px',
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
                fontSize: '10px',
                marginTop: '2px'
            }}>
                {label}
            </div>
        </div>
    )
}

function EmptyPantheon() {
    return (
        <div style={{
            textAlign: 'center',
            padding: '60px 20px',
            color: 'var(--text-secondary)'
        }}>
            <div style={{ fontSize: '48px', marginBottom: '16px' }}>👑</div>
            <div style={{ fontSize: '16px', marginBottom: '8px' }}>
                Le Panthéon est vide
            </div>
            <div style={{ fontSize: '13px' }}>
                Écoute 425 fois un artiste pour qu'il devienne Star !
            </div>
        </div>
    )
}