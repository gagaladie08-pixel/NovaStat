package com.novastats.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ════════════════════════════════════════════
// TABLE 11 — snapshots
// ════════════════════════════════════════════
@Entity(
    tableName = "snapshots",
    indices = [
        Index("type"),
        Index("date"),
        Index(value = ["type", "date"], unique = true)
    ]
)
data class SnapshotEntity(
    @PrimaryKey(autoGenerate = true)
    val snapshot_id: Long = 0,
    val type: String, // DAILY / WEEKLY / MONTHLY / YEARLY
    val date: String, // Format : "2024-06-27"
    val week_number: Int? = null,
    val month: Int? = null,
    val year: Int? = null,
    val created_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 12 — snapshot_tracks
// ════════════════════════════════════════════
@Entity(
    tableName = "snapshot_tracks",
    foreignKeys = [
        ForeignKey(
            entity = SnapshotEntity::class,
            parentColumns = ["snapshot_id"],
            childColumns = ["snapshot_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = ["track_id"],
            childColumns = ["track_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("snapshot_id"),
        Index("track_id"),
        Index("position"),
        Index("peak_position"),
        Index(value = ["snapshot_id", "track_id"], unique = true)
    ]
)
data class SnapshotTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val snapshot_id: Long,
    val track_id: Long,
    val position: Int,
    val play_count: Int = 0,
    val total_duration_ms: Long = 0,
    val previous_position: Int? = null,
    val movement: Int = 0,
    val is_new: Boolean = false,
    val is_reentry: Boolean = false,
    val days_in_chart: Int = 0,
    val weeks_in_chart: Int = 0,
    val months_in_chart: Int = 0,
    val peak_position: Int? = null,
    val peak_date: String? = null,
    val times_at_peak: Int = 0,
    val debut_position: Int? = null,
    val debut_date: String? = null,
    val variation_plays: Int = 0
)

// ════════════════════════════════════════════
// TABLE 13 — snapshot_artists
// ════════════════════════════════════════════
@Entity(
    tableName = "snapshot_artists",
    foreignKeys = [
        ForeignKey(
            entity = SnapshotEntity::class,
            parentColumns = ["snapshot_id"],
            childColumns = ["snapshot_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["artist_id"],
            childColumns = ["artist_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("snapshot_id"),
        Index("artist_id"),
        Index("position"),
        Index("peak_position"),
        Index(value = ["snapshot_id", "artist_id"], unique = true)
    ]
)
data class SnapshotArtistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val snapshot_id: Long,
    val artist_id: Long,
    val position: Int,
    val play_count: Int = 0,
    val total_duration_ms: Long = 0,
    val previous_position: Int? = null,
    val movement: Int = 0,
    val is_new: Boolean = false,
    val is_reentry: Boolean = false,
    val days_in_chart: Int = 0,
    val weeks_in_chart: Int = 0,
    val months_in_chart: Int = 0,
    val peak_position: Int? = null,
    val peak_date: String? = null,
    val times_at_peak: Int = 0,
    val debut_position: Int? = null,
    val debut_date: String? = null,
    val variation_plays: Int = 0,
    val distinct_tracks: Int = 0,
    val distinct_albums: Int = 0
)

// ════════════════════════════════════════════
// TABLE 14 — snapshot_albums
// ════════════════════════════════════════════
@Entity(
    tableName = "snapshot_albums",
    foreignKeys = [
        ForeignKey(
            entity = SnapshotEntity::class,
            parentColumns = ["snapshot_id"],
            childColumns = ["snapshot_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["album_id"],
            childColumns = ["album_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("snapshot_id"),
        Index("album_id"),
        Index("position"),
        Index("peak_position"),
        Index(value = ["snapshot_id", "album_id"], unique = true)
    ]
)
data class SnapshotAlbumEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val snapshot_id: Long,
    val album_id: Long,
    val position: Int,
    val play_count: Int = 0,
    val total_duration_ms: Long = 0,
    val previous_position: Int? = null,
    val movement: Int = 0,
    val is_new: Boolean = false,
    val is_reentry: Boolean = false,
    val days_in_chart: Int = 0,
    val weeks_in_chart: Int = 0,
    val months_in_chart: Int = 0,
    val peak_position: Int? = null,
    val peak_date: String? = null,
    val times_at_peak: Int = 0,
    val debut_position: Int? = null,
    val debut_date: String? = null,
    val variation_plays: Int = 0,
    val distinct_tracks: Int = 0
)