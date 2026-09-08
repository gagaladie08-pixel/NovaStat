// ════════════════════════════════════════════
// APP — NOVASTATS REACT
// ════════════════════════════════════════════
import { useState, useEffect } from 'react'
import Billboard from './pages/Billboard'
import Records from './pages/Records'
import Certs from './pages/Certs'
import HallOfFame from './pages/HallOfFame'
import Pantheon from './pages/Pantheon'
import Awards from './pages/Awards'
import { useBridge } from './hooks/useBridge'
import './themes/themes.css'

function App() {
    const [page, setPage] = useState('billboard')
    const { getTheme } = useBridge()

    // ── Appliquer le thème ────────────────────
    useEffect(() => {
        const theme = getTheme()
        const root = document.documentElement
        root.setAttribute('data-theme', theme.id)

        // Appliquer les variables CSS directement
        Object.entries(theme).forEach(([key, value]) => {
            if (key !== 'id') {
                root.style.setProperty(`--${key}`, value)
            }
        })
    }, [])

    // ── Navigation depuis le natif ────────────
    useEffect(() => {
        window.navigateTo = (newPage) => {
            setPage(newPage)
        }
        return () => {
            window.navigateTo = null
        }
    }, [])

    // ── Rendu de la page ──────────────────────
    const renderPage = () => {
        switch (page) {
            case 'billboard': return <Billboard />
            case 'records':   return <Records />
            case 'certs':     return <Certs />
            case 'hof':       return <HallOfFame />
            case 'pantheon':  return <Pantheon />
            case 'awards':    return <Awards />
            default:          return <Billboard />
        }
    }

    return (
        <div
            className="nova-app"
            style={{
                minHeight: '100vh',
                backgroundColor: 'var(--bg)',
                color: 'var(--text)'
            }}
        >
            {renderPage()}
        </div>
    )
}

export default App