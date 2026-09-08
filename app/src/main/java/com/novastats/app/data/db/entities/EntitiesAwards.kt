package com.novastats.app.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// ════════════════════════════════════════════
// TABLE 25 — nova_awards
// ════════════════════════════════════════════
@Entity(
    tableName = "nova_awards",
    indices = [
        Index("year"),
        Index("category"),
        Index("winner_id"),
        Index("winner_type"),
        Index(value = ["year", "category"], unique = true)
    ]
)
data class NovaAwardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val year: Int,
    val category: String,     // SONG_OF_YEAR / ARTIST_OF_YEAR / ALBUM_OF_YEAR
    // BIGGEST_PROGRESSION / REVELATION / BEST_LOYALTY
    // BEST_CERTIFICATION / LONGEST_STREAK / LONGEST_SESSION
    val winner_id: Long,
    val winner_type: String,  // TRACK / ARTIST / ALBUM
    val value: Double = 0.0,
    val message: String? = null,
    val is_final: Boolean = false,
    val calculated_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 26 — nova_awards_history
// ════════════════════════════════════════════
@Entity(
    tableName = "nova_awards_history",
    indices = [
        Index("year"),
        Index("category"),
        Index("winner_id"),
        Index(value = ["year", "category"], unique = true)
    ]
)
data class NovaAwardHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val year: Int,
    val category: String,
    val winner_id: Long,
    val winner_type: String,
    val value: Double = 0.0,
    val message: String? = null,
    val finalized_at: Long = System.currentTimeMillis()
)