package com.novastats.app.data.db.dao

import androidx.room.*
import com.novastats.app.data.db.entities.*
import kotlinx.coroutines.flow.Flow

// ════════════════════════════════════════════
// DAO — pantheon_status
// ════════════════════════════════════════════
@Dao
interface PantheonStatusDao {

    // ── INSERT / UPDATE / DELETE ──────────────
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: PantheonStatusEntity): Long

    @Update
    suspend fun update(item: PantheonStatusEntity)

    @Upsert
    suspend fun upsert(item: PantheonStatusEntity): Long

    @Delete
    suspend fun delete(item: PantheonStatusEntity)

    // ── SELECT ────────────────────────────────
    @Query("""
        SELECT * FROM pantheon_status 
        WHERE artist_id = :artistId 
        LIMIT 1
    """)
    suspend fun getByArtist(artistId: Long): PantheonStatusEntity?

    @Query("""
        SELECT * FROM pantheon_status 
        WHERE artist_id = :artistId 
        LIMIT 1
    """)
    fun getByArtistFlow(artistId: Long): Flow<PantheonStatusEntity?>

    @Query("""
        SELECT * FROM pantheon_status
        ORDER BY 
            CASE current_status
                WHEN 'MYTHIQUE'   THEN 1
                WHEN 'LEGENDE'    THEN 2
                WHEN 'MEGASTAR'   THEN 3
                WHEN 'SUPERSTAR'  THEN 4
                WHEN 'STAR'       THEN 5
            END ASC,
            status_date ASC
    """)
    fun getAllSorted(): Flow<List<PantheonStatusEntity>>

    @Query("""
        SELECT * FROM pantheon_status
        WHERE current_status = :status
        ORDER BY status_date ASC
    """)
    fun getByStatus(status: String): Flow<List<PantheonStatusEntity>>

    @Query("""
        SELECT * FROM pantheon_status
        WHERE current_status = 'MYTHIQUE'
    """)
    fun getMythique(): Flow<List<PantheonStatusEntity>>

    @Query("""
        SELECT * FROM pantheon_status
        WHERE current_status = 'LEGENDE'
        OR current_status = 'MYTHIQUE'
    """)
    fun getLegendAndAbove(): Flow<List<PantheonStatusEntity>>

    // ── UPDATE PARTIELS ───────────────────────
    @Query("""
        UPDATE pantheon_status
        SET current_status = :status,
            status_date = :date,
            time_to_status_ms = :timeMs,
            reached_via_plays = :viaPlays
        WHERE artist_id = :artistId
    """)
    suspend fun updateStatus(
        artistId: Long,
        status: String,
        date: Long,
        timeMs: Long,
        viaPlays: Boolean
    )

    // ── STATS ─────────────────────────────────
    @Query("SELECT COUNT(*) FROM pantheon_status")
    suspend fun getTotalCount(): Int

    @Query("""
        SELECT COUNT(*) FROM pantheon_status
        WHERE current_status = :status
    """)
    suspend fun getCountByStatus(status: String): Int

    @Query("""
        SELECT * FROM pantheon_status
        ORDER BY status_date DESC
        LIMIT 1
    """)
    suspend fun getLatest(): PantheonStatusEntity?

    @Query("""
        SELECT * FROM pantheon_status
        WHERE current_status = 'MYTHIQUE'
        ORDER BY status_date ASC
        LIMIT 1
    """)
    suspend fun getFirstMythique(): PantheonStatusEntity?
}

// ════════════════════════════════════════════
// DAO — pantheon_history
// ════════════════════════════════════════════
@Dao
interface PantheonHistoryDao {

    // ── INSERT ────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: PantheonHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<PantheonHistoryEntity>)

    // ── SELECT ────────────────────────────────
    @Query("""
        SELECT * FROM pantheon_history 
        WHERE artist_id = :artistId
        ORDER BY 
            CASE status
                WHEN 'MYTHIQUE'   THEN 5
                WHEN 'LEGENDE'    THEN 4
                WHEN 'MEGASTAR'   THEN 3
                WHEN 'SUPERSTAR'  THEN 2
                WHEN 'STAR'       THEN 1
            END ASC
    """)
    suspend fun getByArtist(artistId: Long): List<PantheonHistoryEntity>

    @Query("""
        SELECT * FROM pantheon_history 
        WHERE artist_id = :artistId
        ORDER BY date_reached ASC
    """)
    fun getByArtistFlow(artistId: Long): Flow<List<PantheonHistoryEntity>>

    @Query("""
        SELECT * FROM pantheon_history
        WHERE artist_id = :artistId
        AND status = :status
        LIMIT 1
    """)
    suspend fun getByArtistAndStatus(
        artistId: Long,
        status: String
    ): PantheonHistoryEntity?

    @Query("""
        SELECT * FROM pantheon_history
        WHERE status = :status
        ORDER BY time_to_reach_ms ASC
        LIMIT 10
    """)
    suspend fun getFastest(status: String): List<PantheonHistoryEntity>

    @Query("""
        SELECT * FROM pantheon_history
        ORDER BY date_reached DESC
        LIMIT :limit
    """)
    fun getRecent(limit: Int = 20): Flow<List<PantheonHistoryEntity>>

    // ── STATS ─────────────────────────────────
    @Query("""
        SELECT COUNT(*) FROM pantheon_history
        WHERE artist_id = :artistId
    """)
    suspend fun getStatusCountByArtist(artistId: Long): Int

    @Query("""
        SELECT COUNT(*) FROM pantheon_history
        WHERE status = :status
    """)
    suspend fun getCountByStatus(status: String): Int

    @Query("""
        SELECT MIN(time_to_reach_ms) FROM pantheon_history
        WHERE status = :status
    """)
    suspend fun getFastestTime(status: String): Long?
}