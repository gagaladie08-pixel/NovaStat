package com.novastats.app.data.db.dao

import androidx.room.*
import com.novastats.app.data.db.entities.*
import kotlinx.coroutines.flow.Flow

// ════════════════════════════════════════════
// DAO — hall_of_fame
// ════════════════════════════════════════════
@Dao
interface HallOfFameDao {

    // ── INSERT / UPDATE / DELETE ──────────────
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: HallOfFameEntity): Long

    @Update
    suspend fun update(item: HallOfFameEntity)

    @Delete
    suspend fun delete(item: HallOfFameEntity)

    // ── SELECT ────────────────────────────────
    @Query("SELECT * FROM hall_of_fame WHERE hof_id = :id")
    suspend fun getById(id: Long): HallOfFameEntity?

    @Query("""
        SELECT * FROM hall_of_fame
        WHERE entity_type = :entityType
        AND period_type = :period
        ORDER BY 
            CASE entry_type
                WHEN 'TRIPLE_DEBUT'   THEN 1
                WHEN 'LEGENDARY_RUN'  THEN 2
                WHEN 'LONG_RUN'       THEN 3
                WHEN 'DIRECT_DEBUT'   THEN 4
            END ASC,
            weeks_at_1 DESC
    """)
    fun getByTypeAndPeriod(
        entityType: String,
        period: String
    ): Flow<List<HallOfFameEntity>>

    @Query("""
        SELECT * FROM hall_of_fame
        WHERE entity_id = :entityId
        AND entity_type = :entityType
    """)
    suspend fun getByEntity(
        entityId: Long,
        entityType: String
    ): List<HallOfFameEntity>

    @Query("""
        SELECT * FROM hall_of_fame
        WHERE entity_id = :entityId
        AND entity_type = :entityType
        AND period_type = :period
        LIMIT 1
    """)
    suspend fun getByEntityAndPeriod(
        entityId: Long,
        entityType: String,
        period: String
    ): HallOfFameEntity?

    @Query("""
        SELECT * FROM hall_of_fame
        ORDER BY created_at DESC
        LIMIT 1
    """)
    suspend fun getLatest(): HallOfFameEntity?

    @Query("""
        SELECT * FROM hall_of_fame
        WHERE entry_type = 'LEGENDARY_RUN'
        ORDER BY weeks_at_1 DESC
        LIMIT 10
    """)
    suspend fun getLongestRuns(): List<HallOfFameEntity>

    @Query("""
        SELECT * FROM hall_of_fame
        WHERE entry_type = 'DIRECT_DEBUT'
        ORDER BY entry_date DESC
        LIMIT 10
    """)
    suspend fun getDirectDebuts(): List<HallOfFameEntity>

    @Query("""
        SELECT * FROM hall_of_fame
        WHERE period_type = 'GLOBAL'
        ORDER BY weeks_at_1 DESC
    """)
    fun getGlobalEntries(): Flow<List<HallOfFameEntity>>

    // ── UPDATE PARTIELS ───────────────────────
    @Query("""
        UPDATE hall_of_fame
        SET reign_end = :date,
            weeks_at_1 = :weeks
        WHERE hof_id = :id
    """)
    suspend fun updateReign(id: Long, date: String, weeks: Int)

    // ── STATS ─────────────────────────────────
    @Query("SELECT COUNT(*) FROM hall_of_fame")
    suspend fun getTotalCount(): Int

    @Query("""
        SELECT COUNT(*) FROM hall_of_fame
        WHERE entity_type = :entityType
        AND period_type = :period
    """)
    suspend fun getCountByTypeAndPeriod(
        entityType: String,
        period: String
    ): Int

    @Query("""
        SELECT COUNT(*) FROM hall_of_fame
        WHERE entity_id = :entityId
        AND entity_type = :entityType
    """)
    suspend fun getEntryCountByEntity(
        entityId: Long,
        entityType: String
    ): Int
}

// ════════════════════════════════════════════
// DAO — hall_of_fame_badges
// ════════════════════════════════════════════
@Dao
interface HallOfFameBadgeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(badge: HallOfFameBadgeEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(badges: List<HallOfFameBadgeEntity>)

    @Delete
    suspend fun delete(badge: HallOfFameBadgeEntity)

    @Query("""
        SELECT * FROM hall_of_fame_badges 
        WHERE hof_id = :hofId
    """)
    suspend fun getByHof(hofId: Long): List<HallOfFameBadgeEntity>

    @Query("""
        SELECT * FROM hall_of_fame_badges 
        WHERE hof_id = :hofId
    """)
    fun getByHofFlow(hofId: Long): Flow<List<HallOfFameBadgeEntity>>

    @Query("""
        SELECT * FROM hall_of_fame_badges 
        WHERE badge_type = :type
        ORDER BY badge_date DESC
    """)
    suspend fun getByType(type: String): List<HallOfFameBadgeEntity>

    @Query("""
        SELECT COUNT(*) FROM hall_of_fame_badges 
        WHERE hof_id = :hofId
    """)
    suspend fun getCountByHof(hofId: Long): Int

    @Query("DELETE FROM hall_of_fame_badges WHERE hof_id = :hofId")
    suspend fun deleteByHof(hofId: Long)
}