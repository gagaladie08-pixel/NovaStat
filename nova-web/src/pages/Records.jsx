// ════════════════════════════════════════════
// PAGE — RECORDS
// ════════════════════════════════════════════
import { useState, useEffect } from 'react'
import { useBridge } from '../hooks/useBridge'

const RECORDS = [
    { id: 'MOST_CUMULATIVE', label: '#1 Most Cumulative', emoji: '📊' },
    { id: 'MOST_CUMULATIVE_TOP10', label: '#2 Most Top 10', emoji: '🔥' },
    { id: 'MOST_TIME_AT_1', label: '#3 Most Time #1', emoji: '👑' },
    { id: 'MOST_SONGS_CHARTS', label: '#4 Most Songs in Charts', emoji: '🎵' },
    { id: 'MOST_SONGS_TOP10', label: '#5 Most Songs Top 10', emoji: '🏆' },
    { id: 'MOST_SONGS_AT1', label: '#6 Most Songs at #1', emoji: '🥇' },
    { id: 'MOST_DEBUT_1', label: '#7 Most Debut #1', emoji: '🚀' },
    { id: 'MOST_DEBUT_TOP10', label: '#8 Most Debut Top 10', emoji: '⭐' },
    { id: 'FASTEST_CERT', label: '#9 Fastest Certification', emoji: '💎' },
    { id: 'BIGGEST_PERIOD', label: '#10 Biggest Day/Week/Month', emoji: '📈' },
    { id: 'BIGGEST_DEBUT', label: '#11 Biggest Debut', emoji: '🆕' },
    { id: 'MOST_CERTIFICATIONS', label: '#12 Most Certifications', emoji: '🏅' },
    { id: 'MOST_HOF', label: '#13 Most HoF Entries', emoji: '🏛️' },
    { id: 'BIGGEST_COMEBACK', label: '#15 Biggest Comeback', emoji: '↩️' },
    { id: 'FASTEST_RISE', label: '#16 Fastest Rise', emoji: '⚡' },
    { id: 'MOST_CONSISTENT', label: '#17 Most Consistent', emoji: '🔄' },
    { id: 'BIGGEST_JUMP', label: '#18 Biggest Jump', emoji: '⬆️' },
    { id: 'BIGGEST_FALL', label: '#19 Biggest Fall', emoji: '⬇️' },
    { id: 'SLEEPER_HIT', label: '#20 Sleeper Hit', emoji: '😴' },
    { id: 'MULTI_CHART', label: '#21 Multi Chart', emoji: '🌍' },
    { id: 'MOST_BLOCKED_TOP5', label: '#22 Most Blocked Top 5', emoji: '🚧' },
    { id: 'MOST_SIMULTANEOUS', label: '#23 Most Simultaneous', emoji: '🎯' },
    { id: 'MOST_SUCCESSIVE_1', label: '#24 Most Successive #1', emoji: '♾️' }
]

const PERIODS = ['DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY']
const CATEGORIES = ['TRACK', 'ARTIST', 'ALBUM']

export default function Records() {
    const { getData, openPopup } = useBridge()
    const [selectedRecord, setSelectedRecord] = useState(RECORDS[0])
    const [period, setPeriod] = useState('WEEKLY')
    const [category, setCategory] = useState('TRACK')
    const [data, setData] = useState(null)
    const [showPopup, setShowPopup] = useState(false)
    const [selectedItem, setSelectedItem] = useState(null)

    useEffect(() => {
        const result = getData({
            type: 'records',
            recordType: selectedRecord.id,
            period,
            category
        })
        setData(result)
    }, [selectedRecord, period, category])

    return (
        <div style={{ padding: '16px' }}>

            {/* ── Liste des records ── */}
            <div style={{
                display: 'flex',
                gap: '8px',
                overflowX: 'auto',
                marginBottom: '12px',
                paddingBottom: '4px'
            }}>
                {RECORDS.map(r => (
                    <button
                        key={r.id}
                        onClick={() => setSelectedRecord(r)}
                        style={{
                            padding: '8px 14px',
                            borderRadius: '20px',
                            border: 'none',
                            cursor: 'pointer',
                            whiteSpace: 'nowrap',
                            background: selectedRecord.id === r.id
                                ? 'var(--primary)'
                                : 'var(--surface)',
                            color: selectedRecord.id === r.id
                                ? 'var(--bg)'
                                : 'var(--text-secondary)',
                            fontSize: '12px',
                            fontWeight: selectedRecord.id === r.id
                                ? 'bold' : 'normal'
                        }}
                    >
                        {r.emoji} {r.label}
                    </button>
                ))}
            </div>

            {/* ── Période + Catégorie ── */}
            <div style={{
                display: 'flex',
                gap: '8px',
                marginBottom: '12px',
                flexWrap: 'wrap'
            }}>
                {PERIODS.map(p => (
                    <button
                        key={p}
                        onClick={() => setPeriod(p)}
                        style={{
                            padding: '6px 12px',
                            borderRadius: '20px',
                            border: `1px solid ${period === p
                                ? 'var(--primary)'
                                : 'transparent'}`,
                            background: 'transparent',
                            color: period === p
                                ? 'var(--primary)'
                                : 'var(--text-secondary)',
                            fontSize: '12px',
                            cursor: 'pointer'
                        }}
                    >
                        {p}
                    </button>
                ))}
                <div style={{ width: '1px', background: 'var(--surface)' }} />
                {CATEGORIES.map(c => (
                    <button
                        key={c}
                        onClick={() => setCategory(c)}
                        style={{
                            padding: '6px 12px',
                            borderRadius: '20px',
                            border: `1px solid ${category === c
                                ? 'var(--secondary)'
                                : 'transparent'}`,
                            background: 'transparent',
                            color: category === c
                                ? 'var(--secondary)'
                                : 'var(--text-secondary)',
                            fontSize: '12px',
                            cursor: 'pointer'
                        }}
                    >
                        {c === 'TRACK' ? '🎵' :
                         c === 'ARTIST' ? '🎤' : '💿'} {c}
                    </button>
                ))}
            </div>

            {/* ── Titre du record ── */}
            <div style={{
                background: 'var(--surface)',
                borderRadius: '12px',
                padding: '12px 16px',
                marginBottom: '12px',
                borderLeft: '3px solid var(--primary)'
            }}>
                <div style={{
                    color: 'var(--primary)',
                    fontWeight: 'bold',
                    fontSize: '14px'
                }}>
                    {selectedRecord.emoji} {selectedRecord.label}
                </div>
                <div style={{
                    color: 'var(--text-secondary)',
                    fontSize: '12px',
                    marginTop: '4px'
                }}>
                    {period} • {category} • Top 10
                </div>
            </div>

            {/* ── Items ── */}
            {data?.items?.length === 0 ? (
                <EmptyRecords />
            ) : (
                data?.items?.map((item, index) => (
                    <RecordRow
                        key={item.entityId}
                        item={item}
                        index={index}
                        category={category}
                        onPress={() => {
                            setSelectedItem(item)
                            setShowPopup(true)
                        }}
                    />
                ))
            )}

            {/* ── Popup détail ── */}
            {showPopup && selectedItem && (
                <RecordPopup
                    item={selectedItem}
                    record={selectedRecord}
                    onClose={() => setShowPopup(false)}
                />
            )}
        </div>
    )
}

// ── Ligne Record ──────────────────────────────
function RecordRow({ item, index, category, onPress }) {
    return (
        <div
            onClick={onPress}
            style={{
                display: 'flex',
                alignItems: 'center',
                gap: '12px',
                padding: '12px',
                marginBottom: '8px',
                background: 'var(--surface)',
                borderRadius: '12px',
                cursor: 'pointer'
            }}
        >
            <div style={{
                color: index === 0 ? '#FFD700' :
                       index === 1 ? '#C0C0C0' :
                       index === 2 ? '#CD7F32' : 'var(--text-secondary)',
                fontWeight: 'bold',
                minWidth: '32px',
                fontSize: index < 3 ? '18px' : '14px'
            }}>
                {index === 0 ? '🥇' :
                 index === 1 ? '🥈' :
                 index === 2 ? '🥉' : `#${index + 1}`}
            </div>

            <div style={{ flex: 1 }}>
                <div style={{
                    color: 'var(--text)',
                    fontSize: '14px',
                    fontWeight: '500'
                }}>
                    {item.title || item.name || `ID: ${item.entityId}`}
                </div>
                {item.artist && (
                    <div style={{
                        color: 'var(--text-secondary)',
                        fontSize: '12px'
                    }}>
                        {item.artist}
                    </div>
                )}
            </div>

            <div style={{
                color: 'var(--primary)',
                fontWeight: 'bold',
                fontSize: '16px'
            }}>
                {item.value}
            </div>
        </div>
    )
}

// ── Popup Record ──────────────────────────────
function RecordPopup({ item, record, onClose }) {
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
                    width: '90%',
                    maxHeight: '80vh',
                    background: 'var(--surface)',
                    borderRadius: '16px',
                    overflow: 'auto',
                    padding: '20px',
                    border: '1px solid var(--primary)'
                }}
            >
                {/* Header */}
                <div style={{
                    textAlign: 'center',
                    marginBottom: '16px'
                }}>
                    <div style={{ fontSize: '32px' }}>
                        {record.emoji}
                    </div>
                    <div style={{
                        color: 'var(--primary)',
                        fontWeight: 'bold',
                        fontSize: '16px',
                        marginTop: '8px'
                    }}>
                        {record.label}
                    </div>
                    <div style={{
                        color: 'var(--text)',
                        fontSize: '18px',
                        fontWeight: 'bold',
                        marginTop: '8px'
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
                </div>

                {/* Valeur */}
                <div style={{
                    textAlign: 'center',
                    padding: '16px',
                    background: 'var(--bg)',
                    borderRadius: '12px',
                    marginBottom: '16px'
                }}>
                    <div style={{
                        color: 'var(--primary)',
                        fontSize: '36px',
                        fontWeight: 'bold'
                    }}>
                        {item.value}
                    </div>
                    {item.valueDate && (
                        <div style={{
                            color: 'var(--text-secondary)',
                            fontSize: '12px',
                            marginTop: '4px'
                        }}>
                            📅 {item.valueDate}
                        </div>
                    )}
                </div>

                {/* Fermer */}
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
    )
}

function EmptyRecords() {
    return (
        <div style={{
            textAlign: 'center',
            padding: '60px 20px',
            color: 'var(--text-secondary)'
        }}>
            <div style={{ fontSize: '48px', marginBottom: '16px' }}>🏅</div>
            <div style={{ fontSize: '16px' }}>
                Aucun record pour l'instant
            </div>
            <div style={{ fontSize: '13px', marginTop: '8px' }}>
                Continue d'écouter pour établir des records !
            </div>
        </div>
    )
}