package com.novastats.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ════════════════════════════════════════════
// TABLE 15 — billboard_history_tracks
// ════════════════════════════════════════════
@Entity(
    tableName = "billboard_history_tracks",
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
        Index("period_type"),
        Index("peak_position"),
        Index("is_active"),
        Index(value = ["track_id", "period_type"], unique = true)
    ]
)
data class BillboardHistoryTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val track_id: Long,
    val period_type: String, // DAILY/WEEKLY/MONTHLY/YEARLY/GLOBAL
    val first_entry_date: String? = null,
    val first_entry_position: Int? = null,
    val peak_position: Int? = null,
    val peak_date: String? = null,
    val times_at_peak: Int = 0,
    val total_days_in_chart: Int = 0,
    val total_weeks_in_chart: Int = 0,
    val total_months_in_chart: Int = 0,
    val total_days_top10: Int = 0,
    val total_weeks_top10: Int = 0,
    val total_months_top10: Int = 0,
    val total_days_top5: Int = 0,
    val total_weeks_top5: Int = 0,
    val total_months_top5: Int = 0,
    val total_days_at_1: Int = 0,
    val total_weeks_at_1: Int = 0,
    val total_months_at_1: Int = 0,
    val consecutive_days_top5: Int = 0,
    val consecutive_weeks_top10: Int = 0,
    val consecutive_days_in_chart: Int = 0,
    val reentry_count: Int = 0,
    val last_position: Int? = null,
    val last_seen_date: String? = null,
    val exit_date: String? = null,
    val is_active: Boolean = true,
    val debut_play_count: Int = 0,
    val biggest_jump: Int = 0,
    val biggest_fall: Int = 0,
    val weeks_blocked_top5: Int = 0
)

// ════════════════════════════════════════════
// TABLE 16 — billboard_history_artists
// ════════════════════════════════════════════
@Entity(
    tableName = "billboard_history_artists",
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
        Index("period_type"),
        Index("peak_position"),
        Index("is_active"),
        Index(value = ["artist_id", "period_type"], unique = true)
    ]
)
data class BillboardHistoryArtistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val artist_id: Long,
    val period_type: String, // DAILY/WEEKLY/MONTHLY/YEARLY/GLOBAL
    val first_entry_date: String? = null,
    val first_entry_position: Int? = null,
    val peak_position: Int? = null,
    val peak_date: String? = null,
    val times_at_peak: Int = 0,
    val total_days_in_chart: Int = 0,
    val total_weeks_in_chart: Int = 0,
    val total_months_in_chart: Int = 0,
    val total_days_top10: Int = 0,
    val total_weeks_top10: Int = 0,
    val total_days_at_1: Int = 0,
    val total_weeks_at_1: Int = 0,
    val consecutive_weeks_top10: Int = 0,
    val reentry_count: Int = 0,
    val distinct_songs_in_chart: Int = 0,
    val distinct_albums_in_chart: Int = 0,
    val distinct_songs_top10: Int = 0,
    val distinct_albums_top10: Int = 0,
    val distinct_songs_at_1: Int = 0,
    val distinct_albums_at_1: Int = 0,
    val debut_songs_at_1: Int = 0,
    val debut_songs_top10: Int = 0,
    val debut_albums_at_1: Int = 0,
    val debut_albums_top10: Int = 0,
    val max_simultaneous_songs: Int = 0,
    val max_simultaneous_albums: Int = 0,
    val max_successive_1_songs: Int = 0,
    val max_successive_1_albums: Int = 0,
    val biggest_jump: Int = 0,
    val biggest_fall: Int = 0,
    val is_active: Boolean = true,
    val last_seen_date: String? = null
)

// ════════════════════════════════════════════
// TABLE 17 — billboard_history_albums
// ════════════════════════════════════════════
@Entity(
    tableName = "billboard_history_albums",
    foreignKeys = [
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["album_id"],
            childColumns = ["album_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("album_id"),
        Index("period_type"),
        Index("peak_position"),
        Index("is_active"),
        Index(value = ["album_id", "period_type"], unique = true)
    ]
)
data class BillboardHistoryAlbumEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val album_id: Long,
    val period_type: String, // DAILY/WEEKLY/MONTHLY/YEARLY/GLOBAL
    val first_entry_date: String? = null,
    val first_entry_position: Int? = null,
    val peak_position: Int? = null,
    val peak_date: String? = null,
    val times_at_peak: Int = 0,
    val total_days_in_chart: Int = 0,
    val total_weeks_in_chart: Int = 0,
    val total_months_in_chart: Int = 0,
    val total_days_top10: Int = 0,
    val total_weeks_top10: Int = 0,
    val total_days_at_1: Int = 0,
    val total_weeks_at_1: Int = 0,
    val consecutive_weeks_top10: Int = 0,
    val reentry_count: Int = 0,
    val distinct_songs_in_chart: Int = 0,
    val distinct_songs_top10: Int = 0,
    val distinct_songs_at_1: Int = 0,
    val debut_songs_at_1: Int = 0,
    val debut_songs_top10: Int = 0,
    val biggest_jump: Int = 0,
    val biggest_fall: Int = 0,
    val weeks_blocked_top5: Int = 0,
    val is_active: Boolean = true,
    val last_seen_date: String? = null
)