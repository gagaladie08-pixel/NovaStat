package com.novastats.app.data.db

import android.content.Context
import androidx.room.Room
import com.novastats.app.data.db.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // ── Database ──────────────────────────────
    @Provides
    @Singleton
    fun provideNovaDatabase(
        @ApplicationContext context: Context
    ): NovaDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            NovaDatabase::class.java,
            "nova_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // ── Module 1 — Main ──────────────────────
    @Provides
    @Singleton
    fun provideTrackDao(db: NovaDatabase): TrackDao =
        db.trackDao()

    @Provides
    @Singleton
    fun provideArtistDao(db: NovaDatabase): ArtistDao =
        db.artistDao()

    @Provides
    @Singleton
    fun provideAlbumDao(db: NovaDatabase): AlbumDao =
        db.albumDao()

    @Provides
    @Singleton
    fun provideTrackArtistDao(db: NovaDatabase): TrackArtistDao =
        db.trackArtistDao()

    @Provides
    @Singleton
    fun provideTrackAlbumDao(db: NovaDatabase): TrackAlbumDao =
        db.trackAlbumDao()

    // ── Module 2 — Scrobbles ─────────────────
    @Provides
    @Singleton
    fun provideScrobbleDao(db: NovaDatabase): ScrobbleDao =
        db.scrobbleDao()

    @Provides
    @Singleton
    fun provideSessionDao(db: NovaDatabase): SessionDao =
        db.sessionDao()

    @Provides
    @Singleton
    fun providePendingQueueDao(db: NovaDatabase): PendingQueueDao =
        db.pendingQueueDao()

    @Provides
    @Singleton
    fun provideDailyPlayDao(db: NovaDatabase): DailyPlayDao =
        db.dailyPlayDao()

    @Provides
    @Singleton
    fun provideDailyStreakDao(db: NovaDatabase): DailyStreakDao =
        db.dailyStreakDao()

    // ── Module 3 — Snapshots ─────────────────
    @Provides
    @Singleton
    fun provideSnapshotDao(db: NovaDatabase): SnapshotDao =
        db.snapshotDao()

    @Provides
    @Singleton
    fun provideSnapshotTrackDao(db: NovaDatabase): SnapshotTrackDao =
        db.snapshotTrackDao()

    @Provides
    @Singleton
    fun provideSnapshotArtistDao(db: NovaDatabase): SnapshotArtistDao =
        db.snapshotArtistDao()

    @Provides
    @Singleton
    fun provideSnapshotAlbumDao(db: NovaDatabase): SnapshotAlbumDao =
        db.snapshotAlbumDao()

    // ── Module 4 — Billboard ─────────────────
    @Provides
    @Singleton
    fun provideBillboardTrackDao(db: NovaDatabase): BillboardTrackDao =
        db.billboardTrackDao()

    @Provides
    @Singleton
    fun provideBillboardArtistDao(db: NovaDatabase): BillboardArtistDao =
        db.billboardArtistDao()

    @Provides
    @Singleton
    fun provideBillboardAlbumDao(db: NovaDatabase): BillboardAlbumDao =
        db.billboardAlbumDao()

    // ── Module 5 — Certifications ────────────
    @Provides
    @Singleton
    fun provideCertificationDao(db: NovaDatabase): CertificationDao =
        db.certificationDao()

    @Provides
    @Singleton
    fun provideCertificationHistoryDao(db: NovaDatabase): CertificationHistoryDao =
        db.certificationHistoryDao()

    // ── Module 6 — Hall of Fame ──────────────
    @Provides
    @Singleton
    fun provideHallOfFameDao(db: NovaDatabase): HallOfFameDao =
        db.hallOfFameDao()

    @Provides
    @Singleton
    fun provideHallOfFameBadgeDao(db: NovaDatabase): HallOfFameBadgeDao =
        db.hallOfFameBadgeDao()

    // ── Module 7 — Panthéon ──────────────────
    @Provides
    @Singleton
    fun providePantheonStatusDao(db: NovaDatabase): PantheonStatusDao =
        db.pantheonStatusDao()

    @Provides
    @Singleton
    fun providePantheonHistoryDao(db: NovaDatabase): PantheonHistoryDao =
        db.pantheonHistoryDao()

    // ── Module 8 — Records ───────────────────
    @Provides
    @Singleton
    fun provideRecordCacheDao(db: NovaDatabase): RecordCacheDao =
        db.recordCacheDao()

    // ── Module 9 — Awards ────────────────────
    @Provides
    @Singleton
    fun provideNovaAwardDao(db: NovaDatabase): NovaAwardDao =
        db.novaAwardDao()

    @Provides
    @Singleton
    fun provideNovaAwardHistoryDao(db: NovaDatabase): NovaAwardHistoryDao =
        db.novaAwardHistoryDao()

    // ── Module 10 — Home ─────────────────────
    @Provides
    @Singleton
    fun provideNowPlayingDao(db: NovaDatabase): NowPlayingDao =
        db.nowPlayingDao()

    @Provides
    @Singleton
    fun provideDailyStatsDao(db: NovaDatabase): DailyStatsDao =
        db.dailyStatsDao()

    @Provides
    @Singleton
    fun provideNotificationFeedDao(db: NovaDatabase): NotificationFeedDao =
        db.notificationFeedDao()

    // ── Module 11 — System ───────────────────
    @Provides
    @Singleton
    fun provideApiCacheDao(db: NovaDatabase): ApiCacheDao =
        db.apiCacheDao()

    @Provides
    @Singleton
    fun provideApiReliabilityDao(db: NovaDatabase): ApiReliabilityDao =
        db.apiReliabilityDao()

    @Provides
    @Singleton
    fun provideEditHistoryDao(db: NovaDatabase): EditHistoryDao =
        db.editHistoryDao()

    @Provides
    @Singleton
    fun provideUserCorrectionDao(db: NovaDatabase): UserCorrectionDao =
        db.userCorrectionDao()

    @Provides
    @Singleton
    fun provideMigrationLogDao(db: NovaDatabase): MigrationLogDao =
        db.migrationLogDao()

    @Provides
    @Singleton
    fun provideServiceHealthDao(db: NovaDatabase): ServiceHealthDao =
        db.serviceHealthDao()

    @Provides
    @Singleton
    fun provideServiceIncidentDao(db: NovaDatabase): ServiceIncidentDao =
        db.serviceIncidentDao()
}