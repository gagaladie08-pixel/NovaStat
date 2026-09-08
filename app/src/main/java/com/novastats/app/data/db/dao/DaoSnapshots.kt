package com.novastats.app.data.db.dao

import androidx.room.*
import com.novastats.app.data.db.entities.*
import kotlinx.coroutines.flow.Flow

// ════════════════════════════════════════════
// DAO — snapshots
// ════════════════════════════════════════════
@Dao
interface SnapshotDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(snapshot: SnapshotEntity): Long

    @Update
    suspend fun update(snapshot: SnapshotEntity)

    @Delete
    suspend fun delete(snapshot: SnapshotEntity)

    @Query("SELECT * FROM snapshots WHERE snapshot_id = :id")
    suspend fun getById(id: Long): SnapshotEntity?

    @Query("""
        SELECT * FROM snapshots 
        WHERE type = :type AND date = :date 
        LIMIT 1
    """)
    suspend fun getByTypeAndDate(type: String, date: String): SnapshotEntity?

    @Query("""
        SELECT * FROM snapshots 
        WHERE type = :type 
        ORDER BY date DESC 
        LIMIT 1
    """)
    suspend fun getLatestByType(type: String): SnapshotEntity?

    @Query("""
        SELECT * FROM snapshots 
        WHERE type = :type 
        ORDER BY date DESC 
        LIMIT :limit
    """)
    suspend fun getRecentByType(type: String, limit: Int): List<SnapshotEntity>

    @Query("""
        SELECT * FROM snapshots 
        WHERE type = :type 
        AND date >= :from 
        AND date <= :to
        ORDER BY date DESC
    """)
    suspend fun getBetween(type: String, from: String, to: String): List<SnapshotEntity>

    @Query("SELECT COUNT(*) FROM snapshots WHERE type = :type")
    suspend fun getCountByType(type: String): Int
}

// ════════════════════════════════════════════
// DAO — snapshot_tracks
// ════════════════════════════════════════════
@Dao
interface SnapshotTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SnapshotTrackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SnapshotTrackEntity>)

    @Update
    suspend fun update(item: SnapshotTrackEntity)

    @Query("""
        SELECT * FROM snapshot_tracks 
        WHERE snapshot_id = :snapshotId 
        ORDER BY position ASC
    """)
    suspend fun getBySnapshot(snapshotId: Long): List<SnapshotTrackEntity>

    @Query("""
        SELECT * FROM snapshot_tracks 
        WHERE snapshot_id = :snapshotId 
        ORDER BY position ASC
    """)
    fun getBySnapshotFlow(snapshotId: Long): Flow<List<SnapshotTrackEntity>>

    @Query("""
        SELECT * FROM snapshot_tracks 
        WHERE track_id = :trackId 
        ORDER BY snapshot_id DESC
    """)
    suspend fun getByTrack(trackId: Long): List<SnapshotTrackEntity>

    @Query("""
        SELECT * FROM snapshot_tracks 
        WHERE snapshot_id = :snapshotId 
        AND position <= :topN
        ORDER BY position ASC
    """)
    suspend fun getTopN(snapshotId: Long, topN: Int): List<SnapshotTrackEntity>

    @Query("""
        SELECT * FROM snapshot_tracks 
        WHERE track_id = :trackId 
        AND snapshot_id = :snapshotId 
        LIMIT 1
    """)
    suspend fun getByTrackAndSnapshot(
        trackId: Long,
        snapshotId: Long
    ): SnapshotTrackEntity?

    @Query("""
        SELECT MIN(peak_position) FROM snapshot_tracks 
        WHERE track_id = :trackId
    """)
    suspend fun getPeakPosition(trackId: Long): Int?

    @Query("DELETE FROM snapshot_tracks WHERE snapshot_id = :snapshotId")
    suspend fun deleteBySnapshot(snapshotId: Long)
}

// ════════════════════════════════════════════
// DAO — snapshot_artists
// ════════════════════════════════════════════
@Dao
interface SnapshotArtistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SnapshotArtistEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SnapshotArtistEntity>)

    @Update
    suspend fun update(item: SnapshotArtistEntity)

    @Query("""
        SELECT * FROM snapshot_artists 
        WHERE snapshot_id = :snapshotId 
        ORDER BY position ASC
    """)
    suspend fun getBySnapshot(snapshotId: Long): List<SnapshotArtistEntity>

    @Query("""
        SELECT * FROM snapshot_artists 
        WHERE snapshot_id = :snapshotId 
        ORDER BY position ASC
    """)
    fun getBySnapshotFlow(snapshotId: Long): Flow<List<SnapshotArtistEntity>>

    @Query("""
        SELECT * FROM snapshot_artists 
        WHERE artist_id = :artistId 
        ORDER BY snapshot_id DESC
    """)
    suspend fun getByArtist(artistId: Long): List<SnapshotArtistEntity>

    @Query("""
        SELECT * FROM snapshot_artists 
        WHERE snapshot_id = :snapshotId 
        AND position <= :topN
        ORDER BY position ASC
    """)
    suspend fun getTopN(snapshotId: Long, topN: Int): List<SnapshotArtistEntity>

    @Query("""
        SELECT * FROM snapshot_artists 
        WHERE artist_id = :artistId 
        AND snapshot_id = :snapshotId 
        LIMIT 1
    """)
    suspend fun getByArtistAndSnapshot(
        artistId: Long,
        snapshotId: Long
    ): SnapshotArtistEntity?

    @Query("""
        SELECT MIN(peak_position) FROM snapshot_artists 
        WHERE artist_id = :artistId
    """)
    suspend fun getPeakPosition(artistId: Long): Int?

    @Query("DELETE FROM snapshot_artists WHERE snapshot_id = :snapshotId")
    suspend fun deleteBySnapshot(snapshotId: Long)
}

// ════════════════════════════════════════════
// DAO — snapshot_albums
// ════════════════════════════════════════════
@Dao
interface SnapshotAlbumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SnapshotAlbumEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SnapshotAlbumEntity>)

    @Update
    suspend fun update(item: SnapshotAlbumEntity)

    @Query("""
        SELECT * FROM snapshot_albums 
        WHERE snapshot_id = :snapshotId 
        ORDER BY position ASC
    """)
    suspend fun getBySnapshot(snapshotId: Long): List<SnapshotAlbumEntity>

    @Query("""
        SELECT * FROM snapshot_albums 
        WHERE snapshot_id = :snapshotId 
        ORDER BY position ASC
    """)
    fun getBySnapshotFlow(snapshotId: Long): Flow<List<SnapshotAlbumEntity>>

    @Query("""
        SELECT * FROM snapshot_albums 
        WHERE album_id = :albumId 
        ORDER BY snapshot_id DESC
    """)
    suspend fun getByAlbum(albumId: Long): List<SnapshotAlbumEntity>

    @Query("""
        SELECT * FROM snapshot_albums 
        WHERE snapshot_id = :snapshotId 
        AND position <= :topN
        ORDER BY position ASC
    """)
    suspend fun getTopN(snapshotId: Long, topN: Int): List<SnapshotAlbumEntity>

    @Query("""
        SELECT * FROM snapshot_albums 
        WHERE album_id = :albumId 
        AND snapshot_id = :snapshotId 
        LIMIT 1
    """)
    suspend fun getByAlbumAndSnapshot(
        albumId: Long,
        snapshotId: Long
    ): SnapshotAlbumEntity?

    @Query("""
        SELECT MIN(peak_position) FROM snapshot_albums 
        WHERE album_id = :albumId
    """)
    suspend fun getPeakPosition(albumId: Long): Int?

    @Query("DELETE FROM snapshot_albums WHERE snapshot_id = :snapshotId")
    suspend fun deleteBySnapshot(snapshotId: Long)
}