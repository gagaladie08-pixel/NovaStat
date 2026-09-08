package com.novastats.app.data.db.dao

import androidx.room.*
import com.novastats.app.data.db.entities.*
import kotlinx.coroutines.flow.Flow

// ════════════════════════════════════════════
// DAO — nova_awards
// ════════════════════════════════════════════
@Dao
interface NovaAwardDao {

    // ── INSERT / UPDATE / DELETE ──────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(award: NovaAwardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(awards: List<NovaAwardEntity>)

    @Update
    suspend fun update(award: NovaAwardEntity)

    @Delete
    suspend fun delete(award: NovaAwardEntity)

    // ── SELECT ────────────────────────────────
    @Query("""
        SELECT * FROM nova_awards
        WHERE year = :year
        ORDER BY 
            CASE category
                WHEN 'SONG_OF_YEAR'          THEN 1
                WHEN 'ARTIST_OF_YEAR'        THEN 2
                WHEN 'ALBUM_OF_YEAR'         THEN 3
                WHEN 'BIGGEST_PROGRESSION'   THEN 4
                WHEN 'REVELATION'            THEN 5
                WHEN 'BEST_LOYALTY'          THEN 6
                WHEN 'BEST_CERTIFICATION'    THEN 7
                WHEN 'LONGEST_STREAK'        THEN 8
                WHEN 'LONGEST_SESSION'       THEN 9
            END ASC
    """)
    fun getByYear(year: Int): Flow<List<NovaAwardEntity>>

    @Query("""
        SELECT * FROM nova_awards
        WHERE year = :year
        AND category = :category
        LIMIT 1
    """)
    suspend fun getByYearAndCategory(
        year: Int,
        category: String
    ): NovaAwardEntity?

    @Query("""
        SELECT * FROM nova_awards
        WHERE winner_id = :winnerId
        AND winner_type = :winnerType
        ORDER BY year DESC
    """)
    suspend fun getByWinner(
        winnerId: Long,
        winnerType: String
    ): List<NovaAwardEntity>

    @Query("""
        SELECT * FROM nova_awards
        WHERE year = :year
        AND is_final = 1
        ORDER BY calculated_at DESC
    """)
    suspend fun getFinalByYear(year: Int): List<NovaAwardEntity>

    @Query("""
        SELECT * FROM nova_awards
        WHERE year = :year
        AND is_final = 0
    """)
    fun getLiveByYear(year: Int): Flow<List<NovaAwardEntity>>

    @Query("""
        SELECT DISTINCT year FROM nova_awards
        ORDER BY year DESC
    """)
    suspend fun getAllYears(): List<Int>

    @Query("""
        SELECT * FROM nova_awards
        WHERE category = 'SONG_OF_YEAR'
        ORDER BY year DESC
    """)
    suspend fun getAllSongOfYear(): List<NovaAwardEntity>

    @Query("""
        SELECT * FROM nova_awards
        WHERE category = 'ARTIST_OF_YEAR'
        ORDER BY year DESC
    """)
    suspend fun getAllArtistOfYear(): List<NovaAwardEntity>

    // ── UPDATE PARTIELS ───────────────────────
    @Query("""
        UPDATE nova_awards
        SET is_final = 1,
            calculated_at = :finalizedAt
        WHERE year = :year
    """)
    suspend fun finalizeYear(year: Int, finalizedAt: Long)

    @Query("""
        UPDATE nova_awards
        SET winner_id = :winnerId,
            winner_type = :winnerType,
            value = :value,
            message = :message,
            calculated_at = :calculatedAt
        WHERE year = :year
        AND category = :category
    """)
    suspend fun updateWinner(
        year: Int,
        category: String,
        winnerId: Long,
        winnerType: String,
        value: Double,
        message: String?,
        calculatedAt: Long
    )

    // ── STATS ─────────────────────────────────
    @Query("""
        SELECT COUNT(DISTINCT year) FROM nova_awards
    """)
    suspend fun getTotalYears(): Int

    @Query("""
        SELECT MAX(year) FROM nova_awards
    """)
    suspend fun getLatestYear(): Int?

    @Query("""
        SELECT COUNT(*) FROM nova_awards
        WHERE year = :year
    """)
    suspend fun getCountByYear(year: Int): Int

    @Query("""
        SELECT * FROM nova_awards
        WHERE winner_id = :winnerId
        AND winner_type = :winnerType
        AND category = :category
        ORDER BY year DESC
    """)
    suspend fun getWinsByCategory(
        winnerId: Long,
        winnerType: String,
        category: String
    ): List<NovaAwardEntity>
}

// ════════════════════════════════════════════
// DAO — nova_awards_history
// ════════════════════════════════════════════
@Dao
interface NovaAwardHistoryDao {

    // ── INSERT ────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: NovaAwardHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<NovaAwardHistoryEntity>)

    // ── SELECT ────────────────────────────────
    @Query("""
        SELECT * FROM nova_awards_history
        WHERE year = :year
        ORDER BY 
            CASE category
                WHEN 'SONG_OF_YEAR'          THEN 1
                WHEN 'ARTIST_OF_YEAR'        THEN 2
                WHEN 'ALBUM_OF_YEAR'         THEN 3
                WHEN 'BIGGEST_PROGRESSION'   THEN 4
                WHEN 'REVELATION'            THEN 5
                WHEN 'BEST_LOYALTY'          THEN 6
                WHEN 'BEST_CERTIFICATION'    THEN 7
                WHEN 'LONGEST_STREAK'        THEN 8
                WHEN 'LONGEST_SESSION'       THEN 9
            END ASC
    """)
    suspend fun getByYear(year: Int): List<NovaAwardHistoryEntity>

    @Query("""
        SELECT * FROM nova_awards_history
        WHERE year = :year
        AND category = :category
        LIMIT 1
    """)
    suspend fun getByYearAndCategory(
        year: Int,
        category: String
    ): NovaAwardHistoryEntity?

    @Query("""
        SELECT DISTINCT year FROM nova_awards_history
        ORDER BY year DESC
    """)
    suspend fun getAllYears(): List<Int>

    @Query("""
        SELECT * FROM nova_awards_history
        WHERE winner_id = :winnerId
        AND winner_type = :winnerType
        ORDER BY year DESC
    """)
    suspend fun getByWinner(
        winnerId: Long,
        winnerType: String
    ): List<NovaAwardHistoryEntity>

    @Query("""
        SELECT * FROM nova_awards_history
        ORDER BY finalized_at DESC
        LIMIT 1
    """)
    suspend fun getLatest(): NovaAwardHistoryEntity?

    @Query("""
        SELECT COUNT(*) FROM nova_awards_history
        WHERE winner_id = :winnerId
        AND winner_type = :winnerType
    """)
    suspend fun getTotalWins(
        winnerId: Long,
        winnerType: String
    ): Int

    // ── STATS ─────────────────────────────────
    @Query("SELECT COUNT(DISTINCT year) FROM nova_awards_history")
    suspend fun getTotalYears(): Int

    @Query("SELECT MAX(year) FROM nova_awards_history")
    suspend fun getLatestYear(): Int?
}