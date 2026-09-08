// ════════════════════════════════════════════
// PAGE — BILLBOARD
// ════════════════════════════════════════════
import { useState, useEffect } from 'react'
import { useBridge } from '../hooks/useBridge'
import { formatMovement, formatDuration } from '../utils/format'

// ── Constantes ────────────────────────────────
const CHARTS = [
    { id: 'hot100', label: '🎵 Nova Hot 100' },
    { id: 'artist50', label: '🎤 Nova Artist 50' },
    { id: 'albums75', label: '💿 Nova 75 Albums' }
]

const PERIODS = [
    { id: 'DAILY', label: 'Daily' },
    { id: 'WEEKLY', label: 'Weekly' },
    { id: 'MONTHLY', label: 'Monthly' },
    { id: 'YEARLY', label: 'Yearly' },
    { id: 'GLOBAL', label: 'Global' }
]

const CATEGORIES = {
    hot100: 'TRACK',
    artist50: 'ARTIST',
    albums75: 'ALBUM'
}

export default function Billboard() {
    const { getData, openPopup } = useBridge()
    const [chart, setChart] = useState('hot100')
    const [period, setPeriod] = useState('WEEKLY')
    const [data, setData] = useState(null)
    const [search, setSearch] = useState('')

    // ── Charger les données ───────────────────
    useEffect(() => {
        const result = getData({
            type: 'billboard',
            period,
            category: CATEGORIES[chart]
        })
        setData(result)
    }, [chart, period])

    // ── Filtrer par recherche ─────────────────
    const filtered = data?.items?.filter(item => {
        if (!search) return true
        const title = item.title || item.name || ''
        const artist = item.artist || ''
        return title.toLowerCase().includes(search.toLowerCase()) ||
               artist.toLowerCase().includes(search.toLowerCase())
    }) || []

    return (
        <div style={{ padding: '16px' }}>

            {/* ── Sélecteur Chart ── */}
            <div style={{
                display: 'flex',
                gap: '8px',
                marginBottom: '12px',
                overflowX: 'auto',
                paddingBottom: '4px'
            }}>
                {CHARTS.map(c => (
                    <button
                        key={c.id}
                        onClick={() => setChart(c.id)}
                        style={{
                            padding: '8px 16px',
                            borderRadius: '20px',
                            border: 'none',
                            cursor: 'pointer',
                            whiteSpace: 'nowrap',
                            fontWeight: chart === c.id ? 'bold' : 'normal',
                            background: chart === c.id
                                ? 'var(--primary)'
                                : 'var(--surface)',
                            color: chart === c.id
                                ? 'var(--bg)'
                                : 'var(--text-secondary)',
                            fontSize: '13px'
                        }}
                    >
                        {c.label}
                    </button>
                ))}
            </div>

            {/* ── Sélecteur Période ── */}
            <div style={{
                display: 'flex',
                gap: '8px',
                marginBottom: '12px',
                overflowX: 'auto'
            }}>
                {PERIODS.map(p => (
                    <button
                        key={p.id}
                        onClick={() => setPeriod(p.id)}
                        style={{
                            padding: '6px 14px',
                            borderRadius: '20px',
                            border: `1px solid ${period === p.id
                                ? 'var(--primary)'
                                : 'transparent'}`,
                            cursor: 'pointer',
                            background: 'transparent',
                            color: period === p.id
                                ? 'var(--primary)'
                                : 'var(--text-secondary)',
                            fontSize: '12px',
                            fontWeight: period === p.id ? 'bold' : 'normal'
                        }}
                    >
                        {p.label}
                    </button>
                ))}
            </div>

            {/* ── Recherche ── */}
            <div style={{
                background: 'var(--surface)',
                borderRadius: '12px',
                padding: '10px 16px',
                marginBottom: '12px',
                display: 'flex',
                alignItems: 'center',
                gap: '8px'
            }}>
                <span>🔍</span>
                <input
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                    placeholder="Rechercher..."
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

            {/* ── Résumé période ── */}
            {data && (
                <div style={{
                    background: 'var(--surface)',
                    borderRadius: '12px',
                    padding: '10px 16px',
                    marginBottom: '12px',
                    display: 'flex',
                    justifyContent: 'space-between',
                    fontSize: '12px',
                    color: 'var(--text-secondary)'
                }}>
                    <span>📊 {filtered.length} entrées</span>
                    <span>📅 {data.date || 'En cours'}</span>
                    <span
                        style={{
                            color: 'var(--primary)',
                            fontWeight: 'bold'
                        }}
                    >
                        🔴 LIVE
                    </span>
                </div>
            )}

            {/* ── Liste ── */}
            {filtered.length === 0 ? (
                <EmptyBillboard />
            ) : (
                filtered.map((item, index) => (
                    <BillboardRow
                        key={item.trackId || item.artistId || item.albumId}
                        item={item}
                        category={CATEGORIES[chart]}
                        onPress={() => {
                            const type = CATEGORIES[chart].toLowerCase()
                            const id = item.trackId ||
                                       item.artistId ||
                                       item.albumId
                            openPopup(type, id)
                        }}
                    />
                ))
            )}
        </div>
    )
}

// ════════════════════════════════════════════
// LIGNE BILLBOARD
// ════════════════════════════════════════════
function BillboardRow({ item, category, onPress }) {
    const mov = formatMovement(
        item.movement,
        item.isNew,
        item.isReentry
    )

    const posStyle = item.position === 1
        ? { color: '#FFD700', fontSize: '18px', fontWeight: 'bold' }
        : item.position === 2
        ? { color: '#C0C0C0', fontSize: '16px', fontWeight: 'bold' }
        : item.position === 3
        ? { color: '#CD7F32', fontSize: '16px', fontWeight: 'bold' }
        : item.position <= 10
        ? { color: 'var(--primary)', fontSize: '14px', fontWeight: 'bold' }
        : { color: 'var(--text-secondary)', fontSize: '13px' }

    return (
        <div
            onClick={onPress}
            style={{
                display: 'flex',
                alignItems: 'center',
                gap: '12px',
                padding: '12px',
                marginBottom: '8px',
                background: item.position <= 3
                    ? 'var(--surface)'
                    : 'var(--surface)',
                borderRadius: '12px',
                cursor: 'pointer',
                borderLeft: item.position === 1
                    ? '3px solid #FFD700'
                    : item.position <= 3
                    ? '3px solid var(--primary)'
                    : '3px solid transparent',
                transition: 'all 0.2s ease'
            }}
        >
            {/* Position */}
            <div style={{
                minWidth: '36px',
                textAlign: 'center',
                ...posStyle
            }}>
                {item.position === 1 ? '🥇' :
                 item.position === 2 ? '🥈' :
                 item.position === 3 ? '🥉' :
                 `#${item.position}`}
            </div>

            {/* Cover placeholder */}
            <div style={{
                width: '44px',
                height: '44px',
                borderRadius: '8px',
                background: 'var(--primary)',
                opacity: 0.3,
                flexShrink: 0,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '20px'
            }}>
                {category === 'TRACK' ? '🎵' :
                 category === 'ARTIST' ? '🎤' : '💿'}
            </div>

            {/* Infos */}
            <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{
                    color: 'var(--text)',
                    fontSize: '14px',
                    fontWeight: '500',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'nowrap'
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
                    display: 'flex',
                    gap: '8px',
                    marginTop: '2px',
                    fontSize: '11px',
                    color: 'var(--text-secondary)'
                }}>
                    <span>Peak #{item.peakPosition}</span>
                    <span>•</span>
                    <span>{item.weeksInChart}sem</span>
                </div>
            </div>

            {/* Droite */}
            <div style={{
                textAlign: 'right',
                flexShrink: 0
            }}>
                <div style={{
                    color: 'var(--primary)',
                    fontSize: '14px',
                    fontWeight: 'bold'
                }}>
                    {item.plays}
                </div>
                <div style={{
                    fontSize: '11px',
                    ...mov.className === 'move-up'
                        ? { color: '#2ECC71' }
                        : mov.className === 'move-down'
                        ? { color: '#E74C3C' }
                        : { color: '#888' }
                }}>
                    {mov.label}
                </div>
            </div>
        </div>
    )
}

// ════════════════════════════════════════════
// ÉTAT VIDE
// ════════════════════════════════════════════
function EmptyBillboard() {
    return (
        <div style={{
            textAlign: 'center',
            padding: '60px 20px',
            color: 'var(--text-secondary)'
        }}>
            <div style={{ fontSize: '48px', marginBottom: '16px' }}>
                🎵
            </div>
            <div style={{ fontSize: '16px', marginBottom: '8px' }}>
                Ton classement s'enrichit à chaque écoute
            </div>
            <div style={{ fontSize: '13px' }}>
                Lance ta musique pour commencer !
            </div>
        </div>
    )
}