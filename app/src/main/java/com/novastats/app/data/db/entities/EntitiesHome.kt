package com.novastats.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ════════════════════════════════════════════
// TABLE 27 — now_playing
// ════════════════════════════════════════════
@Entity(
    tableName = "now_playing",
    foreignKeys = [
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = ["track_id"],
            childColumns = ["track_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("track_id"),
        Index("updated_at")
    ]
)
data class NowPlayingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val track_id: Long,
    val started_at: Long = System.currentTimeMillis(),
    val progress_ms: Long = 0,
    val source_app: String? = null,
    val scrobble_status: String = "PENDING", // PENDING / VALIDATED
    val updated_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 28 — daily_stats
// ════════════════════════════════════════════
@Entity(
    tableName = "daily_stats",
    indices = [
        Index(value = ["date"], unique = true)
    ]
)
data class DailyStatsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // Format : "2024-06-27"
    val play_count: Int = 0,
    val total_duration_ms: Long = 0,
    val distinct_artists: Int = 0,
    val distinct_albums: Int = 0,
    val distinct_tracks: Int = 0
)

// ════════════════════════════════════════════
// TABLE 29 — notifications_feed
// ════════════════════════════════════════════
@Entity(
    tableName = "notifications_feed",
    indices = [
        Index("type"),
        Index("entity_id"),
        Index("entity_type"),
        Index("is_read"),
        Index("created_at")
    ]
)
data class NotificationFeedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,        // CERTIFICATION / PANTHEON / HOF / RECORD
    val entity_id: Long,
    val entity_type: String, // TRACK / ARTIST / ALBUM
    val message: String,
    val is_read: Boolean = false,
    val created_at: Long = System.currentTimeMillis()
)