package com.novastats.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ════════════════════════════════════════════
// TABLE 20 — hall_of_fame
// ════════════════════════════════════════════
@Entity(
    tableName = "hall_of_fame",
    indices = [
        Index("entity_id"),
        Index("entity_type"),
        Index("period_type"),
        Index("entry_type"),
        Index("entry_date"),
        Index(value = ["entity_id", "entity_type", "period_type", "entry_type"], unique = true)
    ]
)
data class HallOfFameEntity(
    @PrimaryKey(autoGenerate = true)
    val hof_id: Long = 0,
    val entity_id: Long,
    val entity_type: String,  // TRACK / ARTIST / ALBUM
    val period_type: String,  // WEEKLY / MONTHLY / GLOBAL
    val entry_type: String,   // DIRECT_DEBUT / LONG_RUN / TRIPLE_DEBUT / LEGENDARY_RUN
    val entry_date: String,
    val reign_start: String? = null,
    val reign_end: String? = null,
    val weeks_at_1: Int = 0,
    val play_count_at_entry: Int = 0,
    val created_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 21 — hall_of_fame_badges
// ════════════════════════════════════════════
@Entity(
    tableName = "hall_of_fame_badges",
    foreignKeys = [
        ForeignKey(
            entity = HallOfFameEntity::class,
            parentColumns = ["hof_id"],
            childColumns = ["hof_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("hof_id"),
        Index("badge_type"),
        Index(value = ["hof_id", "badge_type"], unique = true)
    ]
)
data class HallOfFameBadgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hof_id: Long,
    val badge_type: String, // DIRECT_DEBUT / LONG_RUN / TRIPLE_DEBUT / LEGENDARY_RUN
    val badge_date: String
)