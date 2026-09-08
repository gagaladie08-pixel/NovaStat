package com.novastats.app.data.db.dao

import androidx.room.*
import com.novastats.app.data.db.entities.*
import kotlinx.coroutines.flow.Flow

// ════════════════════════════════════════════
// DAO — records_cache
// ════════════════════════════════════════════
@Dao
interface RecordCacheDao {

    // ── INSERT / UPDATE / DELETE ──────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RecordCacheEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RecordCacheEntity>)

    @Update
    suspend fun update(item: RecordCacheEntity)

    @Delete
    suspend fun delete(item: RecordCacheEntity)

    // ── SELECT PAR TYPE ───────────────────────
    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = :recordType
        AND period_type = :period
        AND category = :category
        ORDER BY value DESC
        LIMIT 10
    """)
    suspend fun getTop10(
        recordType: String,
        period: String,
        category: String
    ): List<RecordCacheEntity>

    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = :recordType
        AND period_type = :period
        AND category = :category
        AND subcategory = :subcategory
        ORDER BY value DESC
        LIMIT 10
    """)
    suspend fun getTop10WithSub(
        recordType: String,
        period: String,
        category: String,
        subcategory: String
    ): List<RecordCacheEntity>

    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = :recordType
        AND period_type = :period
        AND category = :category
        ORDER BY value DESC
        LIMIT 10
    """)
    fun getTop10Flow(
        recordType: String,
        period: String,
        category: String
    ): Flow<List<RecordCacheEntity>>

    // ── SELECT PAR ENTITÉ ─────────────────────
    @Query("""
        SELECT * FROM records_cache
        WHERE entity_id = :entityId
        AND category = :category
    """)
    suspend fun getByEntity(
        entityId: Long,
        category: String
    ): List<RecordCacheEntity>

    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = :recordType
        AND entity_id = :entityId
        LIMIT 1
    """)
    suspend fun getByTypeAndEntity(
        recordType: String,
        entityId: Long
    ): RecordCacheEntity?

    // ── RECORDS SPÉCIAUX ──────────────────────

    // Record 1 — Most Cumulative
    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = 'MOST_CUMULATIVE'
        AND period_type = :period
        AND category = :category
        ORDER BY value DESC
        LIMIT 10
    """)
    suspend fun getMostCumulative(
        period: String,
        category: String
    ): List<RecordCacheEntity>

    // Record 3 — Most Time at #1
    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = 'MOST_TIME_AT_1'
        AND period_type = :period
        AND category = :category
        ORDER BY value DESC
        LIMIT 10
    """)
    suspend fun getMostTimeAt1(
        period: String,
        category: String
    ): List<RecordCacheEntity>

    // Record 10 — Biggest Day/Week/Month
    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = 'BIGGEST_PERIOD'
        AND period_type = :period
        AND category = :category
        ORDER BY value DESC
        LIMIT 10
    """)
    suspend fun getBiggestPeriod(
        period: String,
        category: String
    ): List<RecordCacheEntity>

    // Record 15 — Biggest Comeback
    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = 'BIGGEST_COMEBACK'
        AND period_type = :period
        AND category = :category
        ORDER BY value DESC
        LIMIT 10
    """)
    suspend fun getBiggestComeback(
        period: String,
        category: String
    ): List<RecordCacheEntity>

    // Record 16 — Fastest Rise
    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = 'FASTEST_RISE'
        AND period_type = :period
        AND category = :category
        ORDER BY value ASC
        LIMIT 10
    """)
    suspend fun getFastestRise(
        period: String,
        category: String
    ): List<RecordCacheEntity>

    // Record 17 — Most Consistent
    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = 'MOST_CONSISTENT'
        AND period_type = :period
        AND category = :category
        AND subcategory = :zone
        ORDER BY value DESC
        LIMIT 10
    """)
    suspend fun getMostConsistent(
        period: String,
        category: String,
        zone: String // TOP_5 / TOP_10 / TOTAL
    ): List<RecordCacheEntity>

    // Record 18 — Biggest Jump
    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = 'BIGGEST_JUMP'
        AND period_type = :period
        AND category = :category
        ORDER BY value DESC
        LIMIT 10
    """)
    suspend fun getBiggestJump(
        period: String,
        category: String
    ): List<RecordCacheEntity>

    // Record 19 — Biggest Fall
    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = 'BIGGEST_FALL'
        AND period_type = :period
        AND category = :category
        ORDER BY value DESC
        LIMIT 10
    """)
    suspend fun getBiggestFall(
        period: String,
        category: String
    ): List<RecordCacheEntity>

    // Record 21 — Multi Chart Domination
    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = 'MULTI_CHART'
        AND category = :category
        ORDER BY value DESC
        LIMIT 10
    """)
    suspend fun getMultiChart(category: String): List<RecordCacheEntity>

    // Record 22 — Most Weeks Blocked Top 5
    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = 'MOST_BLOCKED_TOP5'
        AND period_type = :period
        AND category = :category
        ORDER BY value DESC
        LIMIT 10
    """)
    suspend fun getMostBlockedTop5(
        period: String,
        category: String
    ): List<RecordCacheEntity>

    // Record 23 — Most Simultaneous Songs
    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = 'MOST_SIMULTANEOUS'
        AND period_type = :period
        AND category = :category
        AND subcategory = :zone
        ORDER BY value DESC
        LIMIT 10
    """)
    suspend fun getMostSimultaneous(
        period: String,
        category: String,
        zone: String // TOP_5 / TOP_10 / TOP_20 / TOP_50 / GLOBAL
    ): List<RecordCacheEntity>

    // Record 24 — Most Successive #1
    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = 'MOST_SUCCESSIVE_1'
        AND period_type = :period
        AND category = :category
        ORDER BY value DESC
        LIMIT 10
    """)
    suspend fun getMostSuccessive1(
        period: String,
        category: String
    ): List<RecordCacheEntity>

    // ── INVALIDATION DU CACHE ─────────────────
    @Query("""
        DELETE FROM records_cache
        WHERE record_type = :recordType
        AND period_type = :period
        AND category = :category
    """)
    suspend fun invalidate(
        recordType: String,
        period: String,
        category: String
    )

    @Query("DELETE FROM records_cache")
    suspend fun invalidateAll()

    @Query("""
        DELETE FROM records_cache
        WHERE calculated_at < :threshold
    """)
    suspend fun deleteOlderThan(threshold: Long)

    // ── STATS ─────────────────────────────────
    @Query("SELECT COUNT(*) FROM records_cache")
    suspend fun getTotalCount(): Int

    @Query("""
        SELECT * FROM records_cache
        WHERE record_type = :recordType
        AND period_type = :period
        AND category = :category
        AND entity_id = :entityId
        LIMIT 1
    """)
    suspend fun getExact(
        recordType: String,
        period: String,
        category: String,
        entityId: Long
    ): RecordCacheEntity?
}