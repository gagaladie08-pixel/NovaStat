package com.novastats.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ════════════════════════════════════════════
// TABLE 18 — certifications
// ════════════════════════════════════════════
@Entity(
    tableName = "certifications",
    foreignKeys = [
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = ["track_id"],
            childColumns = ["entity_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("entity_id"),
        Index("entity_type"),
        Index("level"),
        Index("certified_at"),
        Index(value = ["entity_id", "entity_type", "level", "multiplier"], unique = true)
    ]
)
data class CertificationEntity(
    @PrimaryKey(autoGenerate = true)
    val certification_id: Long = 0,
    val entity_id: Long,
    val entity_type: String, // TRACK / ALBUM
    val level: String, // SILVER / GOLD / PLATINUM / DIAMOND
    val multiplier: Int = 1, // 1x / 2x / 3x...
    val play_count_at_cert: Int = 0,
    val certified_at: Long = System.currentTimeMillis(),
    val time_to_certify_ms: Long = 0,
    val created_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 19 — certification_history
// ════════════════════════════════════════════
@Entity(
    tableName = "certification_history",
    indices = [
        Index("entity_id"),
        Index("entity_type"),
        Index("level"),
        Index("certified_at")
    ]
)
data class CertificationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entity_id: Long,
    val entity_type: String, // TRACK / ALBUM
    val level: String, // SILVER / GOLD / PLATINUM / DIAMOND
    val multiplier: Int = 1,
    val certified_at: Long = System.currentTimeMillis(),
    val play_count_at_cert: Int = 0,
    val time_to_certify_ms: Long = 0
)