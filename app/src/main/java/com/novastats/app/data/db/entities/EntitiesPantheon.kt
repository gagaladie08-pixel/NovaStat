package com.novastats.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ════════════════════════════════════════════
// TABLE 22 — pantheon_status
// ════════════════════════════════════════════
@Entity(
    tableName = "pantheon_status",
    foreignKeys = [
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["artist_id"],
            childColumns = ["artist_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["artist_id"], unique = true),
        Index("current_status"),
        Index("status_date")
    ]
)
data class PantheonStatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val artist_id: Long,
    val current_status: String, // STAR / SUPERSTAR / MEGASTAR / LEGENDE / MYTHIQUE
    val status_date: Long = System.currentTimeMillis(),
    val time_to_status_ms: Long = 0,
    val star_threshold: Int = 425,
    val superstar_threshold: Int = 650,
    val megastar_threshold: Int = 1250,
    val legende_threshold: Int = 3650,
    val mythique_threshold: Int = 7000,
    val reached_via_plays: Boolean = false,
    val created_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 23 — pantheon_history
// ════════════════════════════════════════════
@Entity(
    tableName = "pantheon_history",
    foreignKeys = [
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["artist_id"],
            childColumns = ["artist_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("artist_id"),
        Index("status"),
        Index("date_reached"),
        Index(value = ["artist_id", "status"], unique = true)
    ]
)
data class PantheonHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val artist_id: Long,
    val status: String, // STAR / SUPERSTAR / MEGASTAR / LEGENDE / MYTHIQUE
    val date_reached: Long = System.currentTimeMillis(),
    val time_to_reach_ms: Long = 0,
    val play_count_at_status: Int = 0
)