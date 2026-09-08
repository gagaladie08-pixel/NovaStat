package com.novastats.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ════════════════════════════════════════════
// TABLE 1 — tracks
// ════════════════════════════════════════════
@Entity(
    tableName = "tracks",
    foreignKeys = [
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
        ),
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = ["track_id"],
            childColumns = ["original_track_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("artist_id"),
        Index("album_id"),
        Index("original_track_id"),
        Index("title"),
        Index("play_count"),
        Index("confidence_score"),
        Index("needs_review")
    ]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val track_id: Long = 0,
    val title: String,
    val title_raw: String,
    val artist_id: Long? = null,
    val album_id: Long? = null,
    val duration_ms: Long = 0,
    val genre: String? = null,
    val cover_url: String? = null,
    val cover_source: String? = null,
    val is_remix: Boolean = false,
    val original_track_id: Long? = null,
    val discovery_rank: Int? = null,
    val first_played_at: Long? = null,
    val last_played_at: Long? = null,
    val play_count: Int = 0,
    val total_duration_ms: Long = 0,
    val current_streak: Int = 0,
    val best_streak: Int = 0,
    val best_streak_date: String? = null,
    val confidence_score: Int = 0,
    val needs_review: Boolean = false,
    val acoustid_fingerprint: String? = null,
    val acoustid_resolved: Boolean = false,
    val mbid: String? = null,
    val created_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 2 — artists
// ════════════════════════════════════════════
@Entity(
    tableName = "artists",
    foreignKeys = [
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["artist_id"],
            childColumns = ["merged_into_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("name"),
        Index("play_count"),
        Index("pantheon_status"),
        Index("merged_into_id"),
        Index("is_merged")
    ]
)
data class ArtistEntity(
    @PrimaryKey(autoGenerate = true)
    val artist_id: Long = 0,
    val name: String,
    val name_raw: String,
    val photo_url: String? = null,
    val photo_source: String? = null,
    val bio: String? = null,
    val mbid: String? = null,
    val spotify_id: String? = null,
    val play_count: Int = 0,
    val total_duration_ms: Long = 0,
    val distinct_tracks: Int = 0,
    val distinct_albums: Int = 0,
    val first_played_at: Long? = null,
    val last_played_at: Long? = null,
    val pantheon_status: String? = null,
    val pantheon_date: Long? = null,
    val is_merged: Boolean = false,
    val merged_into_id: Long? = null,
    val created_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 3 — albums
// ════════════════════════════════════════════
@Entity(
    tableName = "albums",
    foreignKeys = [
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["artist_id"],
            childColumns = ["artist_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("artist_id"),
        Index("title"),
        Index("play_count"),
        Index("is_studio"),
        Index("is_compilation")
    ]
)
data class AlbumEntity(
    @PrimaryKey(autoGenerate = true)
    val album_id: Long = 0,
    val title: String,
    val title_raw: String,
    val artist_id: Long? = null,
    val cover_url: String? = null,
    val cover_source: String? = null,
    val release_date: String? = null,
    val is_studio: Boolean = true,
    val is_compilation: Boolean = false,
    val mbid: String? = null,
    val play_count: Int = 0,
    val total_duration_ms: Long = 0,
    val distinct_tracks_played: Int = 0,
    val first_played_at: Long? = null,
    val last_played_at: Long? = null,
    val created_at: Long = System.currentTimeMillis()
)

// ════════════════════════════════════════════
// TABLE 4 — track_artists
// ════════════════════════════════════════════
@Entity(
    tableName = "track_artists",
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
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("track_id"),
        Index("artist_id"),
        Index(value = ["track_id", "artist_id"], unique = true)
    ]
)
data class TrackArtistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val track_id: Long,
    val artist_id: Long,
    val is_primary: Boolean = true,
    val role: String = "main" // main / featured / remix
)

// ════════════════════════════════════════════
// TABLE 5 — track_albums
// ════════════════════════════════════════════
@Entity(
    tableName = "track_albums",
    foreignKeys = [
        ForeignKey(
            entity = TrackEntity::class,
            parentColumns = ["track_id"],
            childColumns = ["track_id"],
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
        Index("track_id"),
        Index("album_id"),
        Index(value = ["track_id", "album_id"], unique = true)
    ]
)
data class TrackAlbumEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val track_id: Long,
    val album_id: Long,
    val track_number: Int? = null,
    val is_primary: Boolean = true
)