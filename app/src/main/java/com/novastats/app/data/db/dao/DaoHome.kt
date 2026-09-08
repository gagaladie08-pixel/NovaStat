package com.novastats.app.data.db.dao

import androidx.room.*
import com.novastats.app.data.db.entities.*
import kotlinx.coroutines.flow.Flow

// ════════════════════════════════════════════
// DAO — now_playing
// ════════════════════════════════════════════
@Dao
interface NowPlayingDao {

    // ── INSERT / UPDATE / DELETE ──────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: NowPlayingEntity): Long

    @Update
    suspend fun update(item: NowPlayingEntity)

    @Query("DELETE FROM now_playing")
    suspend fun clear()

    // ── SELECT ────────────────────────────────
    @Query("""
        SELECT * FROM now_playing
        ORDER BY updated_at DESC
        LIMIT 1
    """)
    suspend fun getCurrent(): NowPlayingEntity?

    @Query("""
        SELECT * FROM now_playing
        ORDER BY updated_at DESC
        LIMIT 1
    """)
    fun getCurrentFlow(): Flow<NowPlayingEntity?>

    // ── UPDATE PARTIELS ───────────────────────
    @Query("""
        UPDATE now_playing
        SET progress_ms = :progress,
            updated_at = :updatedAt
        WHERE id = :id
    """)
    suspend fun updateProgress(id: Long, progress: Long, updatedAt: Long)

    @Query("""
        UPDATE now_playing
        SET scrobble_status = :status,
            updated_at = :updatedAt
        WHERE id = :id
    """)
    suspend fun updateStatus(id: Long, status: String, updatedAt: Long)
}

// ════════════════════════════════════════════
// DAO — daily_stats
// ════════════════════════════════════════════
@Dao
interface DailyStatsDao {

    // ── INSERT / UPDATE ───────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DailyStatsEntity): Long

    @Update
    suspend fun update(item: DailyStatsEntity)

    // ── SELECT ────────────────────────────────
    @Query("""
        SELECT * FROM daily_stats
        WHERE date = :date
        LIMIT 1
    """)
    suspend fun getByDate(date: String): DailyStatsEntity?

    @Query("""
        SELECT * FROM daily_stats
        WHERE date = :date
        LIMIT 1
    """)
    fun getByDateFlow(date: String): Flow<DailyStatsEntity?>

    @Query("""
        SELECT * FROM daily_stats
        ORDER BY date DESC
        LIMIT 1
    """)
    fun getLatestFlow(): Flow<DailyStatsEntity?>

    @Query("""
        SELECT * FROM daily_stats
        ORDER BY date DESC
        LIMIT :limit
    """)
    suspend fun getRecent(limit: Int = 30): List<DailyStatsEntity>

    @Query("""
        SELECT * FROM daily_stats
        WHERE date >= :from
        AND date <= :to
        ORDER BY date ASC
    """)
    suspend fun getBetween(from: String, to: String): List<DailyStatsEntity>

    // ── UPDATE PARTIELS ───────────────────────
    @Query("""
        UPDATE daily_stats
        SET play_count = play_count + 1,
            total_duration_ms = total_duration_ms + :duration
        WHERE date = :date
    """)
    suspend fun incrementPlays(date: String, duration: Long)

    @Query("""
        UPDATE daily_stats
        SET distinct_tracks = :tracks,
            distinct_artists = :artists,
            distinct_albums = :albums
        WHERE date = :date
    """)
    suspend fun updateDistincts(
        date: String,
        tracks: Int,
        artists: Int,
        albums: Int
    )

    // ── STATS ─────────────────────────────────
    @Query("SELECT SUM(play_count) FROM daily_stats")
    suspend fun getTotalPlays(): Int

    @Query("SELECT SUM(total_duration_ms) FROM daily_stats")
    suspend fun getTotalDuration(): Long

    @Query("SELECT AVG(play_count) FROM daily_stats WHERE play_count > 0")
    suspend fun getAveragePlaysPerDay(): Double

    @Query("""
        SELECT MAX(play_count) FROM daily_stats
    """)
    suspend fun getBestDayPlays(): Int

    @Query("""
        SELECT * FROM daily_stats
        ORDER BY play_count DESC
        LIMIT 1
    """)
    suspend fun getBestDay(): DailyStatsEntity?
}

// ════════════════════════════════════════════
// DAO — notifications_feed
// ════════════════════════════════════════════
@Dao
interface NotificationFeedDao {

    // ── INSERT / UPDATE / DELETE ──────────────
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: NotificationFeedEntity): Long

    @Update
    suspend fun update(item: NotificationFeedEntity)

    @Delete
    suspend fun delete(item: NotificationFeedEntity)

    // ── SELECT ────────────────────────────────
    @Query("""
        SELECT * FROM notifications_feed
        ORDER BY created_at DESC
        LIMIT :limit
    """)
    fun getRecent(limit: Int = 50): Flow<List<NotificationFeedEntity>>

    @Query("""
        SELECT * FROM notifications_feed
        WHERE is_read = 0
        ORDER BY created_at DESC
    """)
    fun getUnread(): Flow<List<NotificationFeedEntity>>

    @Query("""
        SELECT * FROM notifications_feed
        WHERE type = :type
        ORDER BY created_at DESC
        LIMIT :limit
    """)
    suspend fun getByType(type: String, limit: Int = 20): List<NotificationFeedEntity>

    @Query("""
        SELECT * FROM notifications_feed
        WHERE entity_id = :entityId
        AND entity_type = :entityType
        ORDER BY created_at DESC
    """)
    suspend fun getByEntity(
        entityId: Long,
        entityType: String
    ): List<NotificationFeedEntity>

    // ── UPDATE PARTIELS ───────────────────────
    @Query("""
        UPDATE notifications_feed
        SET is_read = 1
        WHERE id = :id
    """)
    suspend fun markAsRead(id: Long)

    @Query("""
        UPDATE notifications_feed
        SET is_read = 1
    """)
    suspend fun markAllAsRead()

    // ── STATS ─────────────────────────────────
    @Query("""
        SELECT COUNT(*) FROM notifications_feed
        WHERE is_read = 0
    """)
    fun getUnreadCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM notifications_feed")
    suspend fun getTotalCount(): Int

    // ── NETTOYAGE ─────────────────────────────
    @Query("""
        DELETE FROM notifications_feed
        WHERE created_at < :threshold
    """)
    suspend fun deleteOlderThan(threshold: Long)

    @Query("""
        DELETE FROM notifications_feed
        WHERE id NOT IN (
            SELECT id FROM notifications_feed
            ORDER BY created_at DESC
            LIMIT 200
        )
    """)
    suspend fun keepOnly200()
}