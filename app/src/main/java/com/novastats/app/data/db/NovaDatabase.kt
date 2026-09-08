package com.novastats.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.novastats.app.data.db.dao.*
import com.novastats.app.data.db.entities.*

@Database(
    entities = [
        // ── Module 1 — Main ──────────────────
        TrackEntity::class,
        ArtistEntity::class,
        AlbumEntity::class,
        TrackArtistEntity::class,
        TrackAlbumEntity::class,

        // ── Module 2 — Scrobbles ─────────────
        ScrobbleEntity::class,
        SessionEntity::class,
        PendingQueueEntity::class,
        DailyPlayEntity::class,
        DailyStreakEntity::class,

        // ── Module 3 — Snapshots ─────────────
        SnapshotEntity::class,
        SnapshotTrackEntity::class,
        SnapshotArtistEntity::class,
        SnapshotAlbumEntity::class,

        // ── Module 4 — Billboard ─────────────
        BillboardHistoryTrackEntity::class,
        BillboardHistoryArtistEntity::class,
        BillboardHistoryAlbumEntity::class,

        // ── Module 5 — Certifications ────────
        CertificationEntity::class,
        CertificationHistoryEntity::class,

        // ── Module 6 — Hall of Fame ──────────
        HallOfFameEntity::class,
        HallOfFameBadgeEntity::class,

        // ── Module 7 — Panthéon ──────────────
        PantheonStatusEntity::class,
        PantheonHistoryEntity::class,

        // ── Module 8 — Records ───────────────
        RecordCacheEntity::class,

        // ── Module 9 — Awards ────────────────
        NovaAwardEntity::class,
        NovaAwardHistoryEntity::class,

        // ── Module 10 — Home ─────────────────
        NowPlayingEntity::class,
        DailyStatsEntity::class,
        NotificationFeedEntity::class,

        // ── Module 11 — System ───────────────
        ApiCacheEntity::class,
        ApiReliabilityEntity::class,
        EditHistoryEntity::class,
        UserCorrectionEntity::class,
        MigrationLogEntity::class,
        ServiceHealthEntity::class,
        ServiceIncidentEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class NovaDatabase : RoomDatabase() {

    // ── Module 1 — Main ──────────────────────
    abstract fun trackDao(): TrackDao
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun trackArtistDao(): TrackArtistDao
    abstract fun trackAlbumDao(): TrackAlbumDao

    // ── Module 2 — Scrobbles ─────────────────
    abstract fun scrobbleDao(): ScrobbleDao
    abstract fun sessionDao(): SessionDao
    abstract fun pendingQueueDao(): PendingQueueDao
    abstract fun dailyPlayDao(): DailyPlayDao
    abstract fun dailyStreakDao(): DailyStreakDao

    // ── Module 3 — Snapshots ─────────────────
    abstract fun snapshotDao(): SnapshotDao
    abstract fun snapshotTrackDao(): SnapshotTrackDao
    abstract fun snapshotArtistDao(): SnapshotArtistDao
    abstract fun snapshotAlbumDao(): SnapshotAlbumDao

    // ── Module 4 — Billboard ─────────────────
    abstract fun billboardTrackDao(): BillboardTrackDao
    abstract fun billboardArtistDao(): BillboardArtistDao
    abstract fun billboardAlbumDao(): BillboardAlbumDao

    // ── Module 5 — Certifications ────────────
    abstract fun certificationDao(): CertificationDao
    abstract fun certificationHistoryDao(): CertificationHistoryDao

    // ── Module 6 — Hall of Fame ──────────────
    abstract fun hallOfFameDao(): HallOfFameDao
    abstract fun hallOfFameBadgeDao(): HallOfFameBadgeDao

    // ── Module 7 — Panthéon ──────────────────
    abstract fun pantheonStatusDao(): PantheonStatusDao
    abstract fun pantheonHistoryDao(): PantheonHistoryDao

    // ── Module 8 — Records ───────────────────
    abstract fun recordCacheDao(): RecordCacheDao

    // ── Module 9 — Awards ────────────────────
    abstract fun novaAwardDao(): NovaAwardDao
    abstract fun novaAwardHistoryDao(): NovaAwardHistoryDao

    // ── Module 10 — Home ─────────────────────
    abstract fun nowPlayingDao(): NowPlayingDao
    abstract fun dailyStatsDao(): DailyStatsDao
    abstract fun notificationFeedDao(): NotificationFeedDao

    // ── Module 11 — System ───────────────────
    abstract fun apiCacheDao(): ApiCacheDao
    abstract fun apiReliabilityDao(): ApiReliabilityDao
    abstract fun editHistoryDao(): EditHistoryDao
    abstract fun userCorrectionDao(): UserCorrectionDao
    abstract fun migrationLogDao(): MigrationLogDao
    abstract fun serviceHealthDao(): ServiceHealthDao
    abstract fun serviceIncidentDao(): ServiceIncidentDao

    // ── Singleton ────────────────────────────
    companion object {
        @Volatile
        private var INSTANCE: NovaDatabase? = null

        fun getInstance(context: Context): NovaDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    NovaDatabase::class.java,
                    "nova_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}