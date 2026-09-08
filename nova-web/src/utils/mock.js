// ════════════════════════════════════════════
// DONNÉES FACTICES POUR LE DEV
// ════════════════════════════════════════════

export const mockBillboard = {
    period: "WEEKLY",
    category: "TRACK",
    date: "2024-06-27",
    total: 10,
    items: [
        {
            position: 1,
            previousPos: 1,
            movement: 0,
            isNew: false,
            isReentry: false,
            trackId: 1,
            title: "Blinding Lights",
            artist: "The Weeknd",
            cover: null,
            plays: 134,
            variationPlays: 8,
            peakPosition: 1,
            timesAtPeak: 25,
            weeksInChart: 52
        },
        {
            position: 2,
            previousPos: 5,
            movement: 3,
            isNew: false,
            isReentry: false,
            trackId: 2,
            title: "Starboy",
            artist: "The Weeknd",
            cover: null,
            plays: 89,
            variationPlays: 12,
            peakPosition: 1,
            timesAtPeak: 10,
            weeksInChart: 30
        },
        {
            position: 3,
            previousPos: 2,
            movement: -1,
            isNew: false,
            isReentry: false,
            trackId: 3,
            title: "Save Your Tears",
            artist: "The Weeknd",
            cover: null,
            plays: 76,
            variationPlays: -5,
            peakPosition: 2,
            timesAtPeak: 5,
            weeksInChart: 20
        },
        {
            position: 4,
            previousPos: 0,
            movement: 0,
            isNew: true,
            isReentry: false,
            trackId: 4,
            title: "As It Was",
            artist: "Harry Styles",
            cover: null,
            plays: 65,
            variationPlays: 65,
            peakPosition: 4,
            timesAtPeak: 1,
            weeksInChart: 1
        },
        {
            position: 5,
            previousPos: 3,
            movement: -2,
            isNew: false,
            isReentry: false,
            trackId: 5,
            title: "Anti-Hero",
            artist: "Taylor Swift",
            cover: null,
            plays: 58,
            variationPlays: -10,
            peakPosition: 1,
            timesAtPeak: 8,
            weeksInChart: 45
        }
    ]
}

export const mockCerts = {
    entityType: "TRACK",
    items: [
        {
            certificationId: 1,
            entityId: 1,
            entityType: "TRACK",
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
            entityType: "TRACK",
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
            entityType: "TRACK",
            title: "Save Your Tears",
            artist: "The Weeknd",
            level: "GOLD",
            multiplier: 1,
            playCountAtCert: 50,
            certifiedAt: Date.now() - 259200000
        }
    ]
}

export const mockPantheon = {
    total: 3,
    counts: {
        MYTHIQUE: 1,
        LEGENDE: 0,
        MEGASTAR: 1,
        SUPERSTAR: 1,
        STAR: 0
    },
    artists: [
        {
            artistId: 1,
            name: "The Weeknd",
            currentStatus: "MYTHIQUE",
            playCount: 7500,
            statusDate: Date.now() - 86400000
        },
        {
            artistId: 2,
            name: "Taylor Swift",
            currentStatus: "MEGASTAR",
            playCount: 1500,
            statusDate: Date.now() - 172800000
        },
        {
            artistId: 3,
            name: "Harry Styles",
            currentStatus: "SUPERSTAR",
            playCount: 750,
            statusDate: Date.now() - 259200000
        }
    ]
}

export const mockAwards = {
    year: 2024,
    allYears: [2024, 2023],
    awards: [
        {
            category: "SONG_OF_YEAR",
            winnerId: 1,
            winnerType: "TRACK",
            title: "Blinding Lights",
            artist: "The Weeknd",
            value: 134,
            message: "Tu l'as écoutée 134 fois — soit 7h 22min de ta vie 🔥"
        },
        {
            category: "ARTIST_OF_YEAR",
            winnerId: 1,
            winnerType: "ARTIST",
            name: "The Weeknd",
            value: 299,
            message: "The Weeknd t'a accompagné 299 fois — 42% de toutes tes écoutes 👑"
        },
        {
            category: "LONGEST_STREAK",
            winnerId: null,
            winnerType: null,
            value: 45,
            message: "45 jours consécutifs sans manquer un seul jour 🔥"
        }
    ]
}

export const mockHof = {
    period: "WEEKLY",
    entityType: "TRACK",
    entries: [
        {
            hofId: 1,
            entityId: 1,
            entityType: "TRACK",
            title: "Blinding Lights",
            artist: "The Weeknd",
            entryType: "LONG_RUN",
            entryDate: "2024-01-15",
            weeksAt1: 15,
            badges: ["LONG_RUN", "DIRECT_DEBUT"]
        },
        {
            hofId: 2,
            entityId: 5,
            entityType: "TRACK",
            title: "Anti-Hero",
            artist: "Taylor Swift",
            entryType: "DIRECT_DEBUT",
            entryDate: "2024-03-20",
            weeksAt1: 3,
            badges: ["DIRECT_DEBUT"]
        }
    ]
}

export const mockRecords = {
    recordType: "MOST_CUMULATIVE",
    period: "WEEKLY",
    category: "TRACK",
    items: [
        {
            entityId: 1,
            title: "Blinding Lights",
            artist: "The Weeknd",
            value: 52,
            valueDate: "2024-06-27"
        },
        {
            entityId: 5,
            title: "Anti-Hero",
            artist: "Taylor Swift",
            value: 45,
            valueDate: "2024-06-27"
        }
    ]
}

// Fonction pour simuler le bridge en dev
export function getMockData(request) {
    switch (request.type) {
        case "billboard": return mockBillboard
        case "certs":     return mockCerts
        case "pantheon":  return mockPantheon
        case "awards":    return mockAwards
        case "hof":       return mockHof
        case "records":   return mockRecords
        default:          return {}
    }
}