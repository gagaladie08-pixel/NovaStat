package com.novastats.app.data.db.dao

import androidx.room.*
import com.novastats.app.data.db.entities.*
import kotlinx.coroutines.flow.Flow

// ════════════════════════════════════════════
// DAO — certifications
// ════════════════════════════════════════════
@Dao
interface CertificationDao {

    // ── INSERT / UPDATE / DELETE ──────────────
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cert: CertificationEntity): Long

    @Update
    suspend fun update(cert: CertificationEntity)

    @Delete
    suspend fun delete(cert: CertificationEntity)

    // ── SELECT ────────────────────────────────
    @Query("""
        SELECT * FROM certifications 
        WHERE entity_id = :entityId 
        AND entity_type = :entityType
        ORDER BY 
            CASE level 
                WHEN 'DIAMOND' THEN 4 
                WHEN 'PLATINUM' THEN 3 
                WHEN 'GOLD' THEN 2 
                WHEN 'SILVER' THEN 1 
            END DESC,
            multiplier DESC
    """)
    suspend fun getByEntity(
        entityId: Long,
        entityType: String
    ): List<CertificationEntity>

    @Query("""
        SELECT * FROM certifications 
        WHERE entity_id = :entityId 
        AND entity_type = :entityType
        ORDER BY 
            CASE level 
                WHEN 'DIAMOND' THEN 4 
                WHEN 'PLATINUM' THEN 3 
                WHEN 'GOLD' THEN 2 
                WHEN 'SILVER' THEN 1 
            END DESC,
            multiplier DESC
    """)
    fun getByEntityFlow(
        entityId: Long,
        entityType: String
    ): Flow<List<CertificationEntity>>

    @Query("""
        SELECT * FROM certifications 
        WHERE entity_type = :entityType
        ORDER BY 
            CASE level 
                WHEN 'DIAMOND' THEN 4 
                WHEN 'PLATINUM' THEN 3 
                WHEN 'GOLD' THEN 2 
                WHEN 'SILVER' THEN 1 
            END DESC,
            multiplier DESC
    """)
    fun getAllByType(entityType: String): Flow<List<CertificationEntity>>

    @Query("""
        SELECT * FROM certifications 
        WHERE entity_type = :entityType
        AND level = 'DIAMOND'
        ORDER BY multiplier DESC
    """)
    fun getDiamonds(entityType: String): Flow<List<CertificationEntity>>

    @Query("""
        SELECT * FROM certifications 
        WHERE entity_id = :entityId
        AND entity_type = :entityType
        AND level = :level
        AND multiplier = :multiplier
        LIMIT 1
    """)
    suspend fun getExact(
        entityId: Long,
        entityType: String,
        level: String,
        multiplier: Int
    ): CertificationEntity?

    // ── RADAR — 5 plus proches du prochain palier ──
    @Query("""
        SELECT * FROM certifications
        WHERE entity_type = :entityType
        ORDER BY certified_at DESC
        LIMIT 5
    """)
    suspend fun getRadar(entityType: String): List<CertificationEntity>

    // ── FASTEST ────────────────────────────────
    @Query("""
        SELECT * FROM certifications
        WHERE entity_type = :entityType
        AND level = :level
        AND multiplier = 1
        ORDER BY time_to_certify_ms ASC
        LIMIT 10
    """)
    suspend fun getFastest(
        entityType: String,
        level: String
    ): List<CertificationEntity>

    // ── MOST CERTIFICATIONS ───────────────────
    @Query("""
        SELECT entity_id, COUNT(*) as count 
        FROM certifications 
        WHERE entity_type = :entityType
        AND level = :level
        GROUP BY entity_id
        ORDER BY count DESC
        LIMIT 10
    """)
    suspend fun getMostCertified(
        entityType: String,
        level: String
    ): List<EntityCountResult>

    // ── UPDATE ────────────────────────────────
    @Query("""
        UPDATE certifications
        SET multiplier = :multiplier,
            play_count_at_cert = :playCount,
            certified_at = :certifiedAt
        WHERE entity_id = :entityId
        AND entity_type = :entityType
        AND level = 'DIAMOND'
    """)
    suspend fun updateDiamondMultiplier(
        entityId: Long,
        entityType: String,
        multiplier: Int,
        playCount: Int,
        certifiedAt: Long
    )

    // ── STATS ─────────────────────────────────
    @Query("""
        SELECT COUNT(*) FROM certifications 
        WHERE entity_type = :entityType
        AND level = :level
    """)
    suspend fun getCountByLevel(entityType: String, level: String): Int

    @Query("""
        SELECT COUNT(DISTINCT entity_id) FROM certifications
        WHERE entity_type = :entityType
    """)
    suspend fun getCertifiedEntityCount(entityType: String): Int

    @Query("""
        SELECT * FROM certifications
        WHERE entity_type = :entityType
        ORDER BY certified_at DESC
        LIMIT 1
    """)
    suspend fun getLatest(entityType: String): CertificationEntity?
}

// ════════════════════════════════════════════
// DAO — certification_history
// ════════════════════════════════════════════
@Dao
interface CertificationHistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: CertificationHistoryEntity): Long

    @Query("""
        SELECT * FROM certification_history 
        WHERE entity_id = :entityId 
        AND entity_type = :entityType
        ORDER BY certified_at ASC
    """)
    suspend fun getByEntity(
        entityId: Long,
        entityType: String
    ): List<CertificationHistoryEntity>

    @Query("""
        SELECT * FROM certification_history
        WHERE entity_type = :entityType
        AND level = :level
        AND multiplier = 1
        ORDER BY time_to_certify_ms ASC
        LIMIT 10
    """)
    suspend fun getFastest(
        entityType: String,
        level: String
    ): List<CertificationHistoryEntity>

    @Query("""
        SELECT * FROM certification_history
        ORDER BY certified_at DESC
        LIMIT :limit
    """)
    fun getRecent(limit: Int = 20): Flow<List<CertificationHistoryEntity>>

    @Query("""
        SELECT COUNT(*) FROM certification_history
        WHERE entity_id = :entityId
        AND entity_type = :entityType
    """)
    suspend fun getCountByEntity(entityId: Long, entityType: String): Int
}

// ════════════════════════════════════════════
// DATA CLASS — Résultat COUNT
// ════════════════════════════════════════════
data class EntityCountResult(
    val entity_id: Long,
    val count: Int
)