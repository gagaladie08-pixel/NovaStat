package com.novastats.app.data.db.dao

import androidx.room.*
import com.novastats.app.data.db.entities.*
import kotlinx.coroutines.flow.Flow

// ════════════════════════════════════════════
// DAO — api_cache
// ════════════════════════════════════════════
@Dao
interface ApiCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ApiCacheEntity): Long

    @Update
    suspend fun update(item: ApiCacheEntity)

    @Delete
    suspend fun delete(item: ApiCacheEntity)

    @Query("""
        SELECT * FROM api_cache
        WHERE entity_id = :entityId
        AND entity_type = :entityType
        AND data_type = :dataType
        AND is_blacklisted = 0
        AND expires_at > :now
        ORDER BY confidence_score DESC
        LIMIT 1
    """)
    suspend fun getBest(
        entityId: Long,
        entityType: String,
        dataType: String,
        now: Long = System.currentTimeMillis()
    ): ApiCacheEntity?

    @Query("""
        SELECT * FROM api_cache
        WHERE entity_id = :entityId
        AND entity_type = :entityType
        AND source = :source
        AND data_type = :dataType
        LIMIT 1
    """)
    suspend fun getExact(
        entityId: Long,
        entityType: String,
        source: String,
        dataType: String
    ): ApiCacheEntity?

    @Query("""
        SELECT * FROM api_cache
        WHERE entity_id = :entityId
        AND entity_type = :entityType
        ORDER BY confidence_score DESC
    """)
    suspend fun getAllByEntity(
        entityId: Long,
        entityType: String
    ): List<ApiCacheEntity>

    @Query("""
        SELECT * FROM api_cache
        WHERE expires_at < :now
        AND is_blacklisted = 0
    """)
    suspend fun getExpired(
        now: Long = System.currentTimeMillis()
    ): List<ApiCacheEntity>

    @Query("""
        UPDATE api_cache
        SET is_rejected = 1
        WHERE id = :id
    """)
    suspend fun markRejected(id: Long)

    @Query("""
        UPDATE api_cache
        SET is_blacklisted = 1
        WHERE id = :id
    """)
    suspend fun markBlacklisted(id: Long)

    @Query("""
        DELETE FROM api_cache
        WHERE expires_at < :now
        AND is_blacklisted = 0
    """)
    suspend fun deleteExpired(now: Long = System.currentTimeMillis())

    @Query("""
        SELECT COUNT(*) FROM api_cache
        WHERE is_blacklisted = 0
    """)
    suspend fun getTotalCount(): Int
}

// ════════════════════════════════════════════
// DAO — api_reliability
// ════════════════════════════════════════════
@Dao
interface ApiReliabilityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ApiReliabilityEntity): Long

    @Update
    suspend fun update(item: ApiReliabilityEntity)

    @Query("""
        SELECT * FROM api_reliability
        ORDER BY current_priority ASC
    """)
    suspend fun getAllSorted(): List<ApiReliabilityEntity>

    @Query("""
        SELECT * FROM api_reliability
        WHERE api_name = :apiName
        LIMIT 1
    """)
    suspend fun getByName(apiName: String): ApiReliabilityEntity?

    @Query("""
        UPDATE api_reliability
        SET success_count = success_count + 1,
            success_rate = (success_count + 1) * 100.0 / (success_count + fail_count + 1),
            updated_at = :now
        WHERE api_name = :apiName
    """)
    suspend fun recordSuccess(
        apiName: String,
        now: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE api_reliability
        SET fail_count = fail_count + 1,
            success_rate = success_count * 100.0 / (success_count + fail_count + 1),
            updated_at = :now
        WHERE api_name = :apiName
    """)
    suspend fun recordFailure(
        apiName: String,
        now: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE api_reliability
        SET current_priority = :priority
        WHERE api_name = :apiName
    """)
    suspend fun updatePriority(apiName: String, priority: Int)

    @Query("""
        SELECT * FROM api_reliability
        ORDER BY success_rate DESC
    """)
    fun getAllFlow(): Flow<List<ApiReliabilityEntity>>
}

// ════════════════════════════════════════════
// DAO — edit_history
// ════════════════════════════════════════════
@Dao
interface EditHistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: EditHistoryEntity): Long

    @Delete
    suspend fun delete(item: EditHistoryEntity)

    @Query("""
        SELECT * FROM edit_history
        ORDER BY created_at DESC
        LIMIT :limit
    """)
    fun getRecent(limit: Int = 50): Flow<List<EditHistoryEntity>>

    @Query("""
        SELECT * FROM edit_history
        ORDER BY created_at DESC
        LIMIT 1
    """)
    suspend fun getLatest(): EditHistoryEntity?

    @Query("""
        SELECT * FROM edit_history
        WHERE entity_id = :entityId
        AND entity_type = :entityType
        ORDER BY created_at DESC
    """)
    suspend fun getByEntity(
        entityId: Long,
        entityType: String
    ): List<EditHistoryEntity>

    @Query("""
        SELECT * FROM edit_history
        WHERE type = :type
        ORDER BY created_at DESC
        LIMIT :limit
    """)
    suspend fun getByType(type: String, limit: Int = 20): List<EditHistoryEntity>

    @Query("SELECT COUNT(*) FROM edit_history")
    suspend fun getTotalCount(): Int

    @Query("""
        DELETE FROM edit_history
        WHERE id NOT IN (
            SELECT id FROM edit_history
            ORDER BY created_at DESC
            LIMIT 50
        )
    """)
    suspend fun keepOnly50()

    @Query("DELETE FROM edit_history")
    suspend fun clearAll()
}

// ════════════════════════════════════════════
// DAO — user_corrections
// ════════════════════════════════════════════
@Dao
interface UserCorrectionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: UserCorrectionEntity): Long

    @Update
    suspend fun update(item: UserCorrectionEntity)

    @Delete
    suspend fun delete(item: UserCorrectionEntity)

    @Query("""
        SELECT * FROM user_corrections
        WHERE original_value = :original
        AND correction_type = :type
        LIMIT 1
    """)
    suspend fun findCorrection(
        original: String,
        type: String
    ): UserCorrectionEntity?

    @Query("""
        SELECT * FROM user_corrections
        WHERE correction_type = :type
        ORDER BY times_applied DESC
    """)
    suspend fun getByType(type: String): List<UserCorrectionEntity>

    @Query("""
        UPDATE user_corrections
        SET times_applied = times_applied + 1
        WHERE id = :id
    """)
    suspend fun incrementApplied(id: Long)

    @Query("SELECT COUNT(*) FROM user_corrections")
    suspend fun getTotalCount(): Int

    @Query("SELECT * FROM user_corrections ORDER BY times_applied DESC")
    fun getAllFlow(): Flow<List<UserCorrectionEntity>>
}

// ════════════════════════════════════════════
// DAO — migration_log
// ════════════════════════════════════════════
@Dao
interface MigrationLogDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: MigrationLogEntity): Long

    @Query("""
        SELECT * FROM migration_log
        ORDER BY migrated_at DESC
    """)
    suspend fun getAll(): List<MigrationLogEntity>

    @Query("""
        SELECT * FROM migration_log
        ORDER BY migrated_at DESC
        LIMIT 1
    """)
    suspend fun getLatest(): MigrationLogEntity?

    @Query("""
        SELECT * FROM migration_log
        WHERE status = :status
        ORDER BY migrated_at DESC
    """)
    suspend fun getByStatus(status: String): List<MigrationLogEntity>

    @Query("SELECT COUNT(*) FROM migration_log")
    suspend fun getTotalCount(): Int
}

// ════════════════════════════════════════════
// DAO — service_health
// ════════════════════════════════════════════
@Dao
interface ServiceHealthDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ServiceHealthEntity): Long

    @Update
    suspend fun update(item: ServiceHealthEntity)

    @Query("""
        SELECT * FROM service_health
        WHERE date = :date
        LIMIT 1
    """)
    suspend fun getByDate(date: String): ServiceHealthEntity?

    @Query("""
        SELECT * FROM service_health
        ORDER BY date DESC
        LIMIT 1
    """)
    fun getLatestFlow(): Flow<ServiceHealthEntity?>

    @Query("""
        SELECT * FROM service_health
        ORDER BY date DESC
        LIMIT :limit
    """)
    suspend fun getRecent(limit: Int = 30): List<ServiceHealthEntity>

    @Query("""
        SELECT AVG(health_score) FROM service_health
        WHERE date >= :from
        AND date <= :to
    """)
    suspend fun getAverageScore(from: String, to: String): Double

    @Query("""
        UPDATE service_health
        SET health_score = :score,
            uptime_ms = :uptime,
            restart_count = restart_count + 1
        WHERE date = :date
    """)
    suspend fun updateHealth(date: String, score: Int, uptime: Long)

    @Query("""
        UPDATE service_health
        SET detection_count = detection_count + 1
        WHERE date = :date
    """)
    suspend fun incrementDetection(date: String)

    @Query("""
        DELETE FROM service_health
        WHERE date < :threshold
    """)
    suspend fun deleteOlderThan(threshold: String)
}

// ════════════════════════════════════════════
// DAO — service_incidents
// ════════════════════════════════════════════
@Dao
interface ServiceIncidentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: ServiceIncidentEntity): Long

    @Update
    suspend fun update(item: ServiceIncidentEntity)

    @Delete
    suspend fun delete(item: ServiceIncidentEntity)

    @Query("""
        SELECT * FROM service_incidents
        ORDER BY occurred_at DESC
        LIMIT :limit
    """)
    fun getRecent(limit: Int = 50): Flow<List<ServiceIncidentEntity>>

    @Query("""
        SELECT * FROM service_incidents
        WHERE is_resolved = 0
        ORDER BY severity DESC, occurred_at DESC
    """)
    fun getUnresolved(): Flow<List<ServiceIncidentEntity>>

    @Query("""
        SELECT * FROM service_incidents
        WHERE severity = :severity
        ORDER BY occurred_at DESC
        LIMIT :limit
    """)
    suspend fun getBySeverity(
        severity: String,
        limit: Int = 20
    ): List<ServiceIncidentEntity>

    @Query("""
        UPDATE service_incidents
        SET is_resolved = 1,
            resolved_at = :resolvedAt
        WHERE id = :id
    """)
    suspend fun markResolved(id: Long, resolvedAt: Long)

    @Query("""
        SELECT COUNT(*) FROM service_incidents
        WHERE is_resolved = 0
    """)
    fun getUnresolvedCount(): Flow<Int>

    @Query("""
        DELETE FROM service_incidents
        WHERE occurred_at < :threshold
        AND is_resolved = 1
    """)
    suspend fun deleteOldResolved(threshold: Long)

    @Query("SELECT COUNT(*) FROM service_incidents")
    suspend fun getTotalCount(): Int
}