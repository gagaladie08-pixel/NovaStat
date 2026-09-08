package com.novastats.app.data.db.dao

import androidx.room.*
import com.novastats.app.data.db.entities.*
import kotlinx.coroutines.flow.Flow

// ════════════════════════════════════════════
// DAO — scrobbles
// ════════════════════════════════════════════
@Dao
interface ScrobbleDao {

    // ── INSERT / UPDATE / DELETE ──────────────
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(scrobble: ScrobbleEntity): Long

    @Update
    suspend fun update(scrobble: ScrobbleEntity)

    @Delete
    suspend fun delete(scrobble: ScrobbleEntity)

    // ── SELECT ────────────────────────────────
    @Query("SELECT * FROM scrobbles WHERE scrobble_id = :id")
    suspend fun getById(id: Long): ScrobbleEntity?

    @Query("SELECT * FROM scrobbles ORDER BY started_at DESC LIMIT :limit")
    fun getRecent(limit: Int = 50): Flow<List<ScrobbleEntity>>

    @Query("SELECT * FROM scrobbles WHERE status = 'PENDING'")
    suspend fun getPending(): List<ScrobbleEntity>

    @Query("SELECT * FROM scrobbles WHERE track_id = :trackId ORDER BY started_at DESC")
    suspend fun getByTrack(trackId: Long): List<ScrobbleEntity>

    @Query("""
        SELECT * FROM scrobbles 
        WHERE started_at >= :from 
        AND started_at <= :to 
        ORDER BY started_at DESC
    """)
    suspend fun getBetween(from: Long, to: Long): List<ScrobbleEntity>

    @Query("""
        SELECT * FROM scrobbles
        WHERE started_at >= :from
        AND started_at <= :to
        AND status = 'CONFIRMED'
        ORDER BY started_at DESC
    """)
    suspend fun getConfirmedBetween(from: Long, to: Long): List<ScrobbleEntity>

    // ── UPDATE PARTIELS ───────────────────────
    @Query("UPDATE scrobbles SET status = :status WHERE scrobble_id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("""
        UPDATE scrobbles 
        SET validated_at = :validatedAt, 
            status = 'CONFIRMED' 
        WHERE scrobble_id = :id
    """)
    suspend fun validate(id: Long, validatedAt: Long)

    @Query("""
        UPDATE scrobbles 
        SET ended_at = :endedAt,
            duration_listened_ms = :duration
        WHERE scrobble_id = :id
    """)
    suspend fun setEnded(id: Long, endedAt: Long, duration: Long)

    // ── STATS ─────────────────────────────────
    @Query("SELECT COUNT(*) FROM scrobbles WHERE status = 'CONFIRMED'")
    suspend fun getTotalConfirmed(): Int

    @Query("""
        SELECT COUNT(*) FROM scrobbles 
        WHERE started_at >= :from 
        AND started_at <= :to
        AND status = 'CONFIRMED'
    """)
    suspend fun getCountBetween(from: Long, to: Long): Int

    @Query("""
        SELECT SUM(duration_listened_ms) FROM scrobbles 
        WHERE started_at >= :from 
        AND started_at <= :to
        AND status = 'CONFIRMED'
    """)
    suspend fun getTotalDurationBetween(from: Long, to: Long): Long

    @Query("DELETE FROM scrobbles WHERE scrobble_id = :id")
    suspend fun deleteById(id: Long)
}

// ════════════════════════════════════════════
// DAO — sessions
// ════════════════════════════════════════════
@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(session: SessionEntity): Long

    @Update
    suspend fun update(session: SessionEntity)

    @Query("SELECT * FROM sessions ORDER BY started_at DESC LIMIT 1")
    suspend fun getLatest(): SessionEntity?

    @Query("SELECT * FROM sessions ORDER BY total_duration_ms DESC LIMIT 1")
    suspend fun getLongest(): SessionEntity?

    @Query("SELECT * FROM sessions ORDER BY started_at DESC LIMIT :limit")
    fun getRecent(limit: Int = 30): Flow<List<SessionEntity>>

    @Query("""
        UPDATE sessions 
        SET ended_at = :endedAt,
            total_duration_ms = :duration,
            track_count = :trackCount
        WHERE session_id = :id
    """)
    suspend fun closeSession(id: Long, endedAt: Long, duration: Long, trackCount: Int)

    @Query("SELECT COUNT(*) FROM sessions")
    suspend fun getTotalCount(): Int
}

// ════════════════════════════════════════════
// DAO — pending_queue
// ════════════════════════════════════════════
@Dao
interface PendingQueueDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: PendingQueueEntity): Long

    @Update
    suspend fun update(item: PendingQueueEntity)

    @Delete
    suspend fun delete(item: PendingQueueEntity)

    @Query("SELECT * FROM pending_queue WHERE status = 'PENDING' ORDER BY created_at ASC")
    suspend fun getAllPending(): List<PendingQueueEntity>

    @Query("SELECT * FROM pending_queue WHERE expires_at < :now")
    suspend fun getExpired(now: Long = System.currentTimeMillis()): List<PendingQueueEntity>

    @Query("UPDATE pending_queue SET status = :status WHERE queue_id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("UPDATE pending_queue SET retry_count = retry_count + 1 WHERE queue_id = :id")
    suspend fun incrementRetry(id: Long)

    @Query("DELETE FROM pending_queue WHERE expires_at < :now")
    suspend fun deleteExpired(now: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM pending_queue WHERE status = 'PENDING'")
    suspend fun getPendingCount(): Int
}

// ════════════════════════════════════════════
// DAO — daily_plays
// ════════════════════════════════════════════
@Dao
interface DailyPlayDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dailyPlay: DailyPlayEntity): Long

    @Update
    suspend fun update(dailyPlay: DailyPlayEntity)

    @Query("""
        SELECT * FROM daily_plays 
        WHERE track_id = :trackId AND date = :date 
        LIMIT 1
    """)
    suspend fun getByTrackAndDate(trackId: Long, date: String): DailyPlayEntity?

    @Query("SELECT * FROM daily_plays WHERE date = :date")
    suspend fun getByDate(date: String): List<DailyPlayEntity>

    @Query("SELECT * FROM daily_plays WHERE track_id = :trackId ORDER BY date DESC")
    suspend fun getByTrack(trackId: Long): List<DailyPlayEntity>

    @Query("""
        UPDATE daily_plays 
        SET play_count = play_count + 1,
            total_duration_ms = total_duration_ms + :duration
        WHERE track_id = :trackId AND date = :date
    """)
    suspend fun increment(trackId: Long, date: String, duration: Long)

    @Query("""
        SELECT SUM(play_count) FROM daily_plays 
        WHERE date = :date
    """)
    suspend fun getTotalPlaysForDate(date: String): Int

    @Query("""
        SELECT COUNT(DISTINCT track_id) FROM daily_plays 
        WHERE date = :date
    """)
    suspend fun getDistinctTracksForDate(date: String): Int
}

// ════════════════════════════════════════════
// DAO — daily_streaks
// ════════════════════════════════════════════
@Dao
interface DailyStreakDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(streak: DailyStreakEntity): Long

    @Update
    suspend fun update(streak: DailyStreakEntity)

    @Query("SELECT * FROM daily_streaks WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): DailyStreakEntity?

    @Query("SELECT * FROM daily_streaks ORDER BY date DESC LIMIT 1")
    suspend fun getLatest(): DailyStreakEntity?

    @Query("SELECT * FROM daily_streaks ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int = 30): Flow<List<DailyStreakEntity>>

    @Query("SELECT MAX(best_streak) FROM daily_streaks")
    suspend fun getBestStreakEver(): Int

    @Query("""
        UPDATE daily_streaks 
        SET has_play = 1,
            current_streak = :streak,
            best_streak = CASE WHEN :streak > best_streak THEN :streak ELSE best_streak END,
            best_streak_date = CASE WHEN :streak > best_streak THEN :date ELSE best_streak_date END
        WHERE date = :date
    """)
    suspend fun markDayWithPlay(date: String, streak: Int)
}