package com.novastats.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ════════════════════════════════════════════
// TABLE 6 — scrobbles
// ════════════════════════════════════════════
@Entity(
    tableName = "scrobbles",
    foreignKeys = [
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = ["track_id"],
            childColumns = ["track_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["artist_id"],
            childColumns = ["artist_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["album_id"],
            childColumns = ["album_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("track_id"),
        Index("artist_id"),
        Index("album_id"),
        Index("started_at"),
        Index("status"),
        Index("confidence_score")
    ]
)
data class ScrobbleEntity(
    @PrimaryKey(autoGenerate = true)
    val scrobble_id: Long = 0,
    val track_id: Long,
    val artist_id: Long? = null,
    val album_id: Long? = null,
    val started_at: Long,
    val validated_at: Long? = null,
    val ended_at: Long? = null,
    val duration_listened_ms: Long = 0,
    val source_app: String? = null,
    val detection_source: String? = null, // MediaSession / Notification
    val confidence_score: Int = 0,
    val status: String = "PENDING", // PENDING / CONFIRMED / CANCELLED
    val volume_level: Int? = null,
    val is_skip: Boolean = false,
    val created_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 7 — sessions
// ════════════════════════════════════════════
@Entity(
    tableName = "sessions",
    indices = [
        Index("started_at"),
        Index("source_app")
    ]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val session_id: Long = 0,
    val started_at: Long,
    val ended_at: Long? = null,
    val total_duration_ms: Long = 0,
    val track_count: Int = 0,
    val source_app: String? = null,
    val created_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 8 — pending_queue
// ════════════════════════════════════════════
@Entity(
    tableName = "pending_queue",
    indices = [
        Index("status"),
        Index("started_at"),
        Index("expires_at")
    ]
)
data class PendingQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val queue_id: Long = 0,
    val track_title: String,
    val artist_name: String? = null,
    val album_name: String? = null,
    val started_at: Long,
    val source_app: String? = null,
    val status: String = "PENDING", // PENDING / PROCESSING
    val retry_count: Int = 0,
    val created_at: Long = System.currentTimeMillis(),
    val expires_at: Long = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)
)

// ════════════════════════════════════════════
// TABLE 9 — daily_plays
// ════════════════════════════════════════════
@Entity(
    tableName = "daily_plays",
    foreignKeys = [
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = ["track_id"],
            childColumns = ["track_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["artist_id"],
            childColumns = ["artist_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["album_id"],
            childColumns = ["album_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("track_id"),
        Index("artist_id"),
        Index("album_id"),
        Index("date"),
        Index(value = ["track_id", "date"], unique = true)
    ]
)
data class DailyPlayEntity(
    @PrimaryKey(autoGenerate = true)
    val daily_id: Long = 0,
    val track_id: Long,
    val artist_id: Long? = null,
    val album_id: Long? = null,
    val date: String, // Format : "2024-06-27"
    val play_count: Int = 0,
    val total_duration_ms: Long = 0
)

// ════════════════════════════════════════════
// TABLE 10 — daily_streaks
// ════════════════════════════════════════════
@Entity(
    tableName = "daily_streaks",
    indices = [
        Index(value = ["date"], unique = true)
    ]
)
data class DailyStreakEntity(
    @PrimaryKey(autoGenerate = true)
    val streak_id: Long = 0,
    val date: String, // Format : "2024-06-27"
    val has_play: Boolean = false,
    val current_streak: Int = 0,
    val best_streak: Int = 0,
    val best_streak_date: String? = null
)