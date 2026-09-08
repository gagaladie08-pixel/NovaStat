package com.novastats.app.data.db.dao

import androidx.room.*
import com.novastats.app.data.db.entities.*
import kotlinx.coroutines.flow.Flow

// ════════════════════════════════════════════
// DAO — tracks
// ════════════════════════════════════════════
@Dao
interface TrackDao {

    // ── INSERT / UPDATE / DELETE ──────────────
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(track: TrackEntity): Long

    @Update
    suspend fun update(track: TrackEntity)

    @Upsert
    suspend fun upsert(track: TrackEntity): Long

    @Delete
    suspend fun delete(track: TrackEntity)

    // ── SELECT ────────────────────────────────
    @Query("SELECT * FROM tracks WHERE track_id = :id")
    suspend fun getById(id: Long): TrackEntity?

    @Query("SELECT * FROM tracks WHERE track_id = :id")
    fun getByIdFlow(id: Long): Flow<TrackEntity?>

    @Query("SELECT * FROM tracks ORDER BY play_count DESC LIMIT :limit")
    fun getTopTracks(limit: Int = 300): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE title LIKE '%' || :query || '%' LIMIT 50")
    suspend fun search(query: String): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE needs_review = 1")
    fun getNeedsReview(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE confidence_score < 70")
    fun getLowConfidence(): Flow<List<TrackEntity>>

    @Query("""
        SELECT * FROM tracks 
        ORDER BY play_count DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getPaginated(limit: Int, offset: Int): List<TrackEntity>

    // ── UPDATE PARTIELS ───────────────────────
    @Query("""
        UPDATE tracks 
        SET play_count = play_count + 1,
            total_duration_ms = total_duration_ms + :duration,
            last_played_at = :timestamp
        WHERE track_id = :id
    """)
    suspend fun incrementPlayCount(id: Long, duration: Long, timestamp: Long)

    @Query("UPDATE tracks SET cover_url = :url, cover_source = :source WHERE track_id = :id")
    suspend fun updateCover(id: Long, url: String, source: String)

    @Query("UPDATE tracks SET confidence_score = :score WHERE track_id = :id")
    suspend fun updateConfidence(id: Long, score: Int)

    @Query("UPDATE tracks SET needs_review = :value WHERE track_id = :id")
    suspend fun setNeedsReview(id: Long, value: Boolean)

    @Query("UPDATE tracks SET title = :title WHERE track_id = :id")
    suspend fun rename(id: Long, title: String)

    @Query("""
        UPDATE tracks 
        SET current_streak = :streak,
            best_streak = CASE WHEN :streak > best_streak THEN :streak ELSE best_streak END,
            best_streak_date = CASE WHEN :streak > best_streak THEN :date ELSE best_streak_date END
        WHERE track_id = :id
    """)
    suspend fun updateStreak(id: Long, streak: Int, date: String)

    // ── STATS ─────────────────────────────────
    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun getTotalCount(): Int

    @Query("SELECT SUM(play_count) FROM tracks")
    suspend fun getTotalPlayCount(): Int

    @Query("SELECT * FROM tracks WHERE first_played_at >= :from AND first_played_at <= :to")
    suspend fun getDiscoveredBetween(from: Long, to: Long): List<TrackEntity>
}

// ════════════════════════════════════════════
// DAO — artists
// ════════════════════════════════════════════
@Dao
interface ArtistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(artist: ArtistEntity): Long

    @Update
    suspend fun update(artist: ArtistEntity)

    @Upsert
    suspend fun upsert(artist: ArtistEntity): Long

    @Delete
    suspend fun delete(artist: ArtistEntity)

    @Query("SELECT * FROM artists WHERE artist_id = :id")
    suspend fun getById(id: Long): ArtistEntity?

    @Query("SELECT * FROM artists WHERE artist_id = :id")
    fun getByIdFlow(id: Long): Flow<ArtistEntity?>

    @Query("SELECT * FROM artists WHERE is_merged = 0 ORDER BY play_count DESC LIMIT :limit")
    fun getTopArtists(limit: Int = 300): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM artists WHERE name LIKE '%' || :query || '%' AND is_merged = 0 LIMIT 50")
    suspend fun search(query: String): List<ArtistEntity>

    @Query("SELECT * FROM artists WHERE pantheon_status IS NOT NULL AND is_merged = 0")
    fun getPantheonArtists(): Flow<List<ArtistEntity>>

    @Query("""
        UPDATE artists 
        SET play_count = play_count + 1,
            total_duration_ms = total_duration_ms + :duration,
            last_played_at = :timestamp
        WHERE artist_id = :id
    """)
    suspend fun incrementPlayCount(id: Long, duration: Long, timestamp: Long)

    @Query("UPDATE artists SET photo_url = :url, photo_source = :source WHERE artist_id = :id")
    suspend fun updatePhoto(id: Long, url: String, source: String)

    @Query("UPDATE artists SET pantheon_status = :status, pantheon_date = :date WHERE artist_id = :id")
    suspend fun updatePantheonStatus(id: Long, status: String, date: Long)

    @Query("UPDATE artists SET is_merged = 1, merged_into_id = :targetId WHERE artist_id = :id")
    suspend fun markAsMerged(id: Long, targetId: Long)

    @Query("UPDATE artists SET name = :name WHERE artist_id = :id")
    suspend fun rename(id: Long, name: String)

    @Query("UPDATE artists SET distinct_tracks = :tracks, distinct_albums = :albums WHERE artist_id = :id")
    suspend fun updateDistincts(id: Long, tracks: Int, albums: Int)

    @Query("SELECT COUNT(*) FROM artists WHERE is_merged = 0")
    suspend fun getTotalCount(): Int

    @Query("SELECT * FROM artists WHERE name = :name AND is_merged = 0 LIMIT 1")
    suspend fun getByName(name: String): ArtistEntity?
}

// ════════════════════════════════════════════
// DAO — albums
// ════════════════════════════════════════════
@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(album: AlbumEntity): Long

    @Update
    suspend fun update(album: AlbumEntity)

    @Upsert
    suspend fun upsert(album: AlbumEntity): Long

    @Delete
    suspend fun delete(album: AlbumEntity)

    @Query("SELECT * FROM albums WHERE album_id = :id")
    suspend fun getById(id: Long): AlbumEntity?

    @Query("SELECT * FROM albums WHERE album_id = :id")
    fun getByIdFlow(id: Long): Flow<AlbumEntity?>

    @Query("""
        SELECT * FROM albums 
        WHERE is_compilation = 0 
        ORDER BY play_count DESC 
        LIMIT :limit
    """)
    fun getTopAlbums(limit: Int = 300): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE title LIKE '%' || :query || '%' LIMIT 50")
    suspend fun search(query: String): List<AlbumEntity>

    @Query("SELECT * FROM albums WHERE artist_id = :artistId AND is_compilation = 0")
    suspend fun getByArtist(artistId: Long): List<AlbumEntity>

    @Query("""
        UPDATE albums 
        SET play_count = play_count + 1,
            total_duration_ms = total_duration_ms + :duration,
            last_played_at = :timestamp
        WHERE album_id = :id
    """)
    suspend fun incrementPlayCount(id: Long, duration: Long, timestamp: Long)

    @Query("UPDATE albums SET cover_url = :url, cover_source = :source WHERE album_id = :id")
    suspend fun updateCover(id: Long, url: String, source: String)

    @Query("UPDATE albums SET title = :title WHERE album_id = :id")
    suspend fun rename(id: Long, title: String)

    @Query("UPDATE albums SET distinct_tracks_played = :count WHERE album_id = :id")
    suspend fun updateDistinctTracks(id: Long, count: Int)

    @Query("SELECT COUNT(*) FROM albums WHERE is_compilation = 0")
    suspend fun getTotalCount(): Int

    @Query("""
        SELECT * FROM albums 
        WHERE title = :title AND artist_id = :artistId 
        LIMIT 1
    """)
    suspend fun getByTitleAndArtist(title: String, artistId: Long): AlbumEntity?
}

// ════════════════════════════════════════════
// DAO — track_artists
// ════════════════════════════════════════════
@Dao
interface TrackArtistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(trackArtist: TrackArtistEntity): Long

    @Delete
    suspend fun delete(trackArtist: TrackArtistEntity)

    @Query("SELECT * FROM track_artists WHERE track_id = :trackId")
    suspend fun getByTrack(trackId: Long): List<TrackArtistEntity>

    @Query("SELECT * FROM track_artists WHERE artist_id = :artistId")
    suspend fun getByArtist(artistId: Long): List<TrackArtistEntity>

    @Query("SELECT * FROM track_artists WHERE track_id = :trackId AND is_primary = 1")
    suspend fun getPrimaryArtist(trackId: Long): TrackArtistEntity?

    @Query("DELETE FROM track_artists WHERE track_id = :trackId")
    suspend fun deleteByTrack(trackId: Long)

    @Query("""
        UPDATE track_artists 
        SET artist_id = :newArtistId 
        WHERE artist_id = :oldArtistId
    """)
    suspend fun migrateArtist(oldArtistId: Long, newArtistId: Long)
}

// ════════════════════════════════════════════
// DAO — track_albums
// ════════════════════════════════════════════
@Dao
interface TrackAlbumDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(trackAlbum: TrackAlbumEntity): Long

    @Delete
    suspend fun delete(trackAlbum: TrackAlbumEntity)

    @Query("SELECT * FROM track_albums WHERE track_id = :trackId")
    suspend fun getByTrack(trackId: Long): List<TrackAlbumEntity>

    @Query("SELECT * FROM track_albums WHERE album_id = :albumId")
    suspend fun getByAlbum(albumId: Long): List<TrackAlbumEntity>

    @Query("SELECT * FROM track_albums WHERE track_id = :trackId AND is_primary = 1")
    suspend fun getPrimaryAlbum(trackId: Long): TrackAlbumEntity?

    @Query("DELETE FROM track_albums WHERE track_id = :trackId")
    suspend fun deleteByTrack(trackId: Long)

    @Query("""
        UPDATE track_albums 
        SET album_id = :newAlbumId 
        WHERE album_id = :oldAlbumId
    """)
    suspend fun migrateAlbum(oldAlbumId: Long, newAlbumId: Long)

    @Query("SELECT COUNT(DISTINCT track_id) FROM track_albums WHERE album_id = :albumId")
    suspend fun getTrackCount(albumId: Long): Int
}