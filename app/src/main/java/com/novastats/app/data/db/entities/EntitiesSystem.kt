package com.novastats.app.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// ════════════════════════════════════════════
// TABLE 30 — api_cache
// ════════════════════════════════════════════
@Entity(
    tableName = "api_cache",
    indices = [
        Index("entity_type"),
        Index("entity_id"),
        Index("source"),
        Index("data_type"),
        Index("expires_at"),
        Index("is_rejected"),
        Index("is_blacklisted"),
        Index(value = ["entity_id", "entity_type", "source", "data_type"], unique = true)
    ]
)
data class ApiCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entity_type: String,   // TRACK / ARTIST / ALBUM
    val entity_id: Long,
    val source: String,        // iTunes / Spotify / LastFm...
    val data_type: String,     // COVER / PHOTO / METADATA / BIO
    val cached_url: String? = null,
    val confidence_score: Int = 0,
    val cached_at: Long = System.currentTimeMillis(),
    val expires_at: Long = System.currentTimeMillis() + (180L * 24 * 60 * 60 * 1000),
    val is_rejected: Boolean = false,
    val is_blacklisted: Boolean = false
)

// ════════════════════════════════════════════
// TABLE 31 — api_reliability
// ════════════════════════════════════════════
@Entity(
    tableName = "api_reliability",
    indices = [
        Index(value = ["api_name"], unique = true),
        Index("current_priority"),
        Index("success_rate")
    ]
)
data class ApiReliabilityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val api_name: String,
    val success_count: Int = 0,
    val fail_count: Int = 0,
    val success_rate: Double = 100.0,
    val current_priority: Int = 1,
    val updated_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 32 — edit_history
// ════════════════════════════════════════════
@Entity(
    tableName = "edit_history",
    indices = [
        Index("type"),
        Index("entity_type"),
        Index("entity_id"),
        Index("created_at")
    ]
)
data class EditHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,        // RENAME / MERGE / COVER_CHANGE / ALBUM_CHANGE / ARTIST_CHANGE
    val entity_type: String, // TRACK / ARTIST / ALBUM
    val entity_id: Long,
    val before: String? = null,
    val after: String? = null,
    val extra_data: String? = null, // JSON
    val created_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 33 — user_corrections
// ════════════════════════════════════════════
@Entity(
    tableName = "user_corrections",
    indices = [
        Index("correction_type"),
        Index("original_value"),
        Index("times_applied")
    ]
)
data class UserCorrectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val original_value: String,
    val corrected_value: String,
    val correction_type: String, // TITLE / ARTIST / ALBUM
    val times_applied: Int = 0,
    val created_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 34 — migration_log
// ════════════════════════════════════════════
@Entity(
    tableName = "migration_log",
    indices = [
        Index("from_version"),
        Index("to_version"),
        Index("status"),
        Index("migrated_at")
    ]
)
data class MigrationLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val from_version: String,
    val to_version: String,
    val migrated_at: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS", // SUCCESS / PARTIAL / FAILED
    val details: String? = null     // JSON
)

// ════════════════════════════════════════════
// TABLE 35 — service_health
// ════════════════════════════════════════════
@Entity(
    tableName = "service_health",
    indices = [
        Index(value = ["date"], unique = true),
        Index("health_score")
    ]
)
data class ServiceHealthEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // Format : "2024-06-27"
    val health_score: Int = 100, // 0-100
    val uptime_ms: Long = 0,
    val downtime_ms: Long = 0,
    val restart_count: Int = 0,
    val mediasession_ok: Boolean = true,
    val notification_ok: Boolean = true,
    val watchdog_ok: Boolean = true,
    val boot_ok: Boolean = true,
    val detection_count: Int = 0,
    val retry_resolved: Int = 0,
    val retry_pending: Int = 0,
    val retry_expired: Int = 0,
    val created_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 36 — service_incidents
// ════════════════════════════════════════════
@Entity(
    tableName = "service_incidents",
    indices = [
        Index("severity"),
        Index("type"),
        Index("occurred_at"),
        Index("is_resolved")
    ]
)
data class ServiceIncidentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,      // CRASH / FREEZE / MISS / DELAY / RESTART
    val severity: String,  // LOW / MEDIUM / HIGH / CRITICAL
    val message: String? = null,
    val occurred_at: Long = System.currentTimeMillis(),
    val resolved_at: Long? = null,
    val is_resolved: Boolean = false,
    val extra_data: String? = null // JSON
)