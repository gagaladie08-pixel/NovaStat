package com.novastats.app.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// ════════════════════════════════════════════
// TABLE 24 — records_cache
// ════════════════════════════════════════════
@Entity(
    tableName = "records_cache",
    indices = [
        Index("record_type"),
        Index("period_type"),
        Index("category"),
        Index("entity_id"),
        Index("value"),
        Index(value = ["record_type", "period_type", "category", "subcategory", "entity_id"], unique = true)
    ]
)
data class RecordCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val record_type: String,   // MOST_CUMULATIVE / BIGGEST_DEBUT / FASTEST_RISE...
    val period_type: String,   // DAILY / WEEKLY / MONTHLY / YEARLY / ALL
    val category: String,      // TRACK / ARTIST / ALBUM
    val subcategory: String? = null, // SONGS / ALBUMS
    val entity_id: Long,
    val value: Double = 0.0,
    val value_date: String? = null,
    val extra_data: String? = null, // JSON pour données supplémentaires
    val calculated_at: Long = System.currentTimeMillis()
)