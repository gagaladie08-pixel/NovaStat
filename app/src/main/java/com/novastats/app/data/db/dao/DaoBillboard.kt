package com.novastats.app.data.db.dao

import androidx.room.*
import com.novastats.app.data.db.entities.*
import kotlinx.coroutines.flow.Flow

// ════════════════════════════════════════════
// DAO — billboard_history_tracks
// ════════════════════════════════════════════
@Dao
interface BillboardTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: BillboardHistoryTrackEntity): Long

    @Update
    suspend fun update(item: BillboardHistoryTrackEntity)

    @Upsert
    suspend fun upsert(item: BillboardHistoryTrackEntity): Long

    @Delete
    suspend fun delete(item: BillboardHistoryTrackEntity)

    // ── SELECT ────────────────────────────────
    @Query("""
        SELECT * FROM billboard_history_tracks 
        WHERE track_id = :trackId 
        AND period_type = :period 
        LIMIT 1
    """)
    suspend fun getByTrackAndPeriod(
        trackId: Long,
        period: String
    ): BillboardHistoryTrackEntity?

    @Query("""
        SELECT * FROM billboard_history_tracks 
        WHERE track_id = :trackId
    """)
    suspend fun getAllByTrack(trackId: Long): List<BillboardHistoryTrackEntity>

    @Query("""
        SELECT * FROM billboard_history_tracks 
        WHERE period_type = :period 
        AND is_active = 1
        ORDER BY last_position ASC
    """)
    fun getActiveByPeriod(period: String): Flow<List<BillboardHistoryTrackEntity>>

    @Query("""
        SELECT * FROM billboard_history_tracks 
        WHERE period_type = :period 
        AND peak_position = 1
        ORDER BY total_weeks_at_1 DESC
    """)
    suspend fun getNumber1sByPeriod(period: String): List<BillboardHistoryTrackEntity>

    @Query("""
        SELECT * FROM billboard_history_tracks 
        WHERE period_type = :period
        ORDER BY total_weeks_in_chart DESC
        LIMIT 10
    """)
    suspend fun getMostCumulative(period: String): List<BillboardHistoryTrackEntity>

    @Query("""
        SELECT * FROM billboard_history_tracks 
        WHERE period_type = :period
        ORDER BY biggest_jump DESC
        LIMIT 10
    """)
    suspend fun getBiggestJumps(period: String): List<BillboardHistoryTrackEntity>

    @Query("""
        SELECT * FROM billboard_history_tracks 
        WHERE period_type = :period
        ORDER BY biggest_fall DESC
        LIMIT 10
    """)
    suspend fun getBiggestFalls(period: String): List<BillboardHistoryTrackEntity>

    @Query("""
        SELECT * FROM billboard_history_tracks 
        WHERE period_type = :period
        AND weeks_blocked_top5 > 0
        ORDER BY weeks_blocked_top5 DESC
        LIMIT 10
    """)
    suspend fun getMostBlockedTop5(period: String): List<BillboardHistoryTrackEntity>

    // ── UPDATE PARTIELS ───────────────────────
    @Query("""
        UPDATE billboard_history_tracks 
        SET is_active = :active,
            exit_date = :exitDate
        WHERE track_id = :trackId 
        AND period_type = :period
    """)
    suspend fun setActive(
        trackId: Long,
        period: String,
        active: Boolean,
        exitDate: String?
    )

    @Query("""
        UPDATE billboard_history_tracks
        SET reentry_count = reentry_count + 1,
            is_active = 1,
            exit_date = null
        WHERE track_id = :trackId
        AND period_type = :period
    """)
    suspend fun markReentry(trackId: Long, period: String)

    @Query("""
        UPDATE billboard_history_tracks
        SET biggest_jump = CASE 
            WHEN :jump > biggest_jump THEN :jump 
            ELSE biggest_jump END
        WHERE track_id = :trackId
        AND period_type = :period
    """)
    suspend fun updateBiggestJump(trackId: Long, period: String, jump: Int)

    @Query("""
        UPDATE billboard_history_tracks
        SET biggest_fall = CASE 
            WHEN :fall > biggest_fall THEN :fall 
            ELSE biggest_fall END
        WHERE track_id = :trackId
        AND period_type = :period
    """)
    suspend fun updateBiggestFall(trackId: Long, period: String, fall: Int)

    @Query("""
        UPDATE billboard_history_tracks
        SET total_days_at_1 = total_days_at_1 + 1
        WHERE track_id = :trackId
        AND period_type = 'DAILY'
    """)
    suspend fun incrementDaysAt1(trackId: Long)

    @Query("""
        UPDATE billboard_history_tracks
        SET total_weeks_at_1 = total_weeks_at_1 + 1
        WHERE track_id = :trackId
        AND period_type = 'WEEKLY'
    """)
    suspend fun incrementWeeksAt1(trackId: Long)

    @Query("""
        UPDATE billboard_history_tracks
        SET peak_position = :peak,
            peak_date = :date,
            times_at_peak = times_at_peak + 1
        WHERE track_id = :trackId
        AND period_type = :period
        AND (:peak < peak_position OR peak_position IS NULL)
    """)
    suspend fun updatePeak(
        trackId: Long,
        period: String,
        peak: Int,
        date: String
    )

    // ── STATS ─────────────────────────────────
    @Query("""
        SELECT COUNT(*) FROM billboard_history_tracks 
        WHERE period_type = :period 
        AND is_active = 1
    """)
    suspend fun getActiveCount(period: String): Int
}

// ════════════════════════════════════════════
// DAO — billboard_history_artists
// ════════════════════════════════════════════
@Dao
interface BillboardArtistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: BillboardHistoryArtistEntity): Long

    @Update
    suspend fun update(item: BillboardHistoryArtistEntity)

    @Upsert
    suspend fun upsert(item: BillboardHistoryArtistEntity): Long

    @Delete
    suspend fun delete(item: BillboardHistoryArtistEntity)

    @Query("""
        SELECT * FROM billboard_history_artists 
        WHERE artist_id = :artistId 
        AND period_type = :period 
        LIMIT 1
    """)
    suspend fun getByArtistAndPeriod(
        artistId: Long,
        period: String
    ): BillboardHistoryArtistEntity?

    @Query("""
        SELECT * FROM billboard_history_artists 
        WHERE artist_id = :artistId
    """)
    suspend fun getAllByArtist(artistId: Long): List<BillboardHistoryArtistEntity>

    @Query("""
        SELECT * FROM billboard_history_artists 
        WHERE period_type = :period 
        AND is_active = 1
        ORDER BY last_seen_date DESC
    """)
    fun getActiveByPeriod(period: String): Flow<List<BillboardHistoryArtistEntity>>

    @Query("""
        SELECT * FROM billboard_history_artists 
        WHERE period_type = :period
        ORDER BY total_weeks_in_chart DESC
        LIMIT 10
    """)
    suspend fun getMostCumulative(period: String): List<BillboardHistoryArtistEntity>

    @Query("""
        SELECT * FROM billboard_history_artists 
        WHERE period_type = :period
        ORDER BY max_simultaneous_songs DESC
        LIMIT 10
    """)
    suspend fun getMostSimultaneous(period: String): List<BillboardHistoryArtistEntity>

    @Query("""
        UPDATE billboard_history_artists
        SET peak_position = :peak,
            peak_date = :date,
            times_at_peak = times_at_peak + 1
        WHERE artist_id = :artistId
        AND period_type = :period
        AND (:peak < peak_position OR peak_position IS NULL)
    """)
    suspend fun updatePeak(
        artistId: Long,
        period: String,
        peak: Int,
        date: String
    )

    @Query("""
        UPDATE billboard_history_artists
        SET is_active = :active,
            last_seen_date = :date
        WHERE artist_id = :artistId
        AND period_type = :period
    """)
    suspend fun setActive(
        artistId: Long,
        period: String,
        active: Boolean,
        date: String?
    )

    @Query("""
        UPDATE billboard_history_artists
        SET reentry_count = reentry_count + 1,
            is_active = 1
        WHERE artist_id = :artistId
        AND period_type = :period
    """)
    suspend fun markReentry(artistId: Long, period: String)

    @Query("""
        UPDATE billboard_history_artists
        SET max_simultaneous_songs = CASE 
            WHEN :count > max_simultaneous_songs THEN :count 
            ELSE max_simultaneous_songs END
        WHERE artist_id = :artistId
        AND period_type = :period
    """)
    suspend fun updateMaxSimultaneousSongs(artistId: Long, period: String, count: Int)

    @Query("""
        UPDATE billboard_history_artists
        SET distinct_songs_in_chart = :count
        WHERE artist_id = :artistId
        AND period_type = :period
    """)
    suspend fun updateDistinctSongs(artistId: Long, period: String, count: Int)

    @Query("""
        SELECT COUNT(*) FROM billboard_history_artists 
        WHERE period_type = :period 
        AND is_active = 1
    """)
    suspend fun getActiveCount(period: String): Int
}

// ════════════════════════════════════════════
// DAO — billboard_history_albums
// ════════════════════════════════════════════
@Dao
interface BillboardAlbumDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: BillboardHistoryAlbumEntity): Long

    @Update
    suspend fun update(item: BillboardHistoryAlbumEntity)

    @Upsert
    suspend fun upsert(item: BillboardHistoryAlbumEntity): Long

    @Delete
    suspend fun delete(item: BillboardHistoryAlbumEntity)

    @Query("""
        SELECT * FROM billboard_history_albums 
        WHERE album_id = :albumId 
        AND period_type = :period 
        LIMIT 1
    """)
    suspend fun getByAlbumAndPeriod(
        albumId: Long,
        period: String
    ): BillboardHistoryAlbumEntity?

    @Query("""
        SELECT * FROM billboard_history_albums 
        WHERE album_id = :albumId
    """)
    suspend fun getAllByAlbum(albumId: Long): List<BillboardHistoryAlbumEntity>

    @Query("""
        SELECT * FROM billboard_history_albums 
        WHERE period_type = :period 
        AND is_active = 1
        ORDER BY last_seen_date DESC
    """)
    fun getActiveByPeriod(period: String): Flow<List<BillboardHistoryAlbumEntity>>

    @Query("""
        SELECT * FROM billboard_history_albums 
        WHERE period_type = :period
        ORDER BY total_weeks_in_chart DESC
        LIMIT 10
    """)
    suspend fun getMostCumulative(period: String): List<BillboardHistoryAlbumEntity>

    @Query("""
        UPDATE billboard_history_albums
        SET peak_position = :peak,
            peak_date = :date,
            times_at_peak = times_at_peak + 1
        WHERE album_id = :albumId
        AND period_type = :period
        AND (:peak < peak_position OR peak_position IS NULL)
    """)
    suspend fun updatePeak(
        albumId: Long,
        period: String,
        peak: Int,
        date: String
    )

    @Query("""
        UPDATE billboard_history_albums
        SET is_active = :active,
            last_seen_date = :date
        WHERE album_id = :albumId
        AND period_type = :period
    """)
    suspend fun setActive(
        albumId: Long,
        period: String,
        active: Boolean,
        date: String?
    )

    @Query("""
        UPDATE billboard_history_albums
        SET reentry_count = reentry_count + 1,
            is_active = 1
        WHERE album_id = :albumId
        AND period_type = :period
    """)
    suspend fun markReentry(albumId: Long, period: String)

    @Query("""
        UPDATE billboard_history_albums
        SET distinct_songs_in_chart = :count
        WHERE album_id = :albumId
        AND period_type = :period
    """)
    suspend fun updateDistinctSongs(albumId: Long, period: String, count: Int)

    @Query("""
        SELECT COUNT(*) FROM billboard_history_albums 
        WHERE period_type = :period 
        AND is_active = 1
    """)
    suspend fun getActiveCount(period: String): Int
}