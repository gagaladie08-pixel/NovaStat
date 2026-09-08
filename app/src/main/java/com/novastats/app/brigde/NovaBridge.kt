package com.novastats.app.bridge

import android.app.Activity
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson
import com.novastats.app.core.theme.ThemeEngine
import com.novastats.app.data.db.NovaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// ════════════════════════════════════════════
// MODÈLES DE REQUÊTE / RÉPONSE
// ════════════════════════════════════════════
data class BridgeRequest(
    val type: String = "",
    val period: String = "WEEKLY",
    val category: String = "TRACK",
    val chart: String = "hot100",
    val recordType: String = "",
    val year: Int = 0,
    val page: Int = 1,
    val limit: Int = 100
)

data class BridgeAction(
    val type: String = "",       // openPopup / share / navigate
    val entityType: String = "", // track / artist / album
    val entityId: Long = 0,
    val tab: String = "",
    val shareType: String = ""
)

// ════════════════════════════════════════════
// NOVABRIDGEMANAGER
// ════════════════════════════════════════════
@Singleton
class NovaBridgeManager @Inject constructor(
    private val db: NovaDatabase
) {
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)

    // WebViews actives (pour push updates)
    private val activeWebViews = mutableListOf<WebView>()

    // Callback vers l'UI native
    var onOpenPopup: ((String, Long) -> Unit)? = null
    var onNavigate: ((String) -> Unit)? = null

    // ── Enregistrer une WebView ───────────────
    fun registerWebView(webView: WebView) {
        if (!activeWebViews.contains(webView)) {
            activeWebViews.add(webView)
        }
    }

    fun unregisterWebView(webView: WebView) {
        activeWebViews.remove(webView)
    }

    // ── Notifier toutes les WebViews ──────────
    fun notifyWebViews(event: String, data: String) {
        activeWebViews.forEach { webView ->
            webView.post {
                webView.evaluateJavascript(
                    "if(window.onNovaEvent) window.onNovaEvent('$event', $data)",
                    null
                )
            }
        }
    }

    // ── Notifier nouvelle écoute ──────────────
    fun notifyNewScrobble(trackId: Long) {
        scope.launch {
            val track = db.trackDao().getById(trackId)
            track?.let {
                val data = gson.toJson(mapOf(
                    "trackId" to it.track_id,
                    "title" to it.title,
                    "playCount" to it.play_count
                ))
                notifyWebViews("newScrobble", data)
            }
        }
    }

    // ── Créer l'interface JS ──────────────────
    fun createInterface(activity: Activity): NovaBridge {
        return NovaBridge(db, activity, this, gson)
    }
}

// ════════════════════════════════════════════
// NOVABRIDGE — Interface JavaScript
// ════════════════════════════════════════════
class NovaBridge(
    private val db: NovaDatabase,
    private val activity: Activity,
    private val manager: NovaBridgeManager,
    private val gson: Gson
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    // ════════════════════════════════════════
    // THÈME
    // ════════════════════════════════════════
    @JavascriptInterface
    fun getTheme(): String {
        return ThemeEngine.toJson()
    }

    // ════════════════════════════════════════
    // DONNÉES PRINCIPALES
    // ════════════════════════════════════════
    @JavascriptInterface
    fun getData(requestJson: String): String {
        return try {
            val request = gson.fromJson(requestJson, BridgeRequest::class.java)
            var result = "{}"

            // Exécuter de manière synchrone depuis le thread IO
            val latch = java.util.concurrent.CountDownLatch(1)

            scope.launch {
                result = when (request.type) {
                    "billboard" -> getBillboardData(request)
                    "records"   -> getRecordsData(request)
                    "certs"     -> getCertsData(request)
                    "hof"       -> getHofData(request)
                    "pantheon"  -> getPantheonData(request)
                    "awards"    -> getAwardsData(request)
                    else        -> "{}"
                }
                latch.countDown()
            }

            latch.await(5, java.util.concurrent.TimeUnit.SECONDS)
            result

        } catch (e: Exception) {
            """{"error": "${e.message}"}"""
        }
    }

    // ════════════════════════════════════════
    // BILLBOARD
    // ════════════════════════════════════════
    private suspend fun getBillboardData(req: BridgeRequest): String {
        return try {
            val snapshot = db.snapshotDao().getLatestByType(req.period)
                ?: return """{"items": [], "period": "${req.period}"}"""

            val items = when (req.category) {
                "TRACK" -> {
                    val tracks = db.snapshotTrackDao()
                        .getBySnapshot(snapshot.snapshot_id)
                    tracks.map { st ->
                        val track = db.trackDao().getById(st.track_id)
                        val artist = track?.artist_id?.let {
                            db.artistDao().getById(it)
                        }
                        mapOf(
                            "position"      to st.position,
                            "previousPos"   to (st.previous_position ?: 0),
                            "movement"      to st.movement,
                            "isNew"         to st.is_new,
                            "isReentry"     to st.is_reentry,
                            "trackId"       to st.track_id,
                            "title"         to (track?.title ?: ""),
                            "artist"        to (artist?.name ?: ""),
                            "cover"         to (track?.cover_url ?: ""),
                            "plays"         to st.play_count,
                            "variationPlays" to st.variation_plays,
                            "peakPosition"  to (st.peak_position ?: st.position),
                            "timesAtPeak"   to st.times_at_peak,
                            "daysInChart"   to st.days_in_chart,
                            "weeksInChart"  to st.weeks_in_chart
                        )
                    }
                }
                "ARTIST" -> {
                    val artists = db.snapshotArtistDao()
                        .getBySnapshot(snapshot.snapshot_id)
                    artists.map { sa ->
                        val artist = db.artistDao().getById(sa.artist_id)
                        mapOf(
                            "position"      to sa.position,
                            "previousPos"   to (sa.previous_position ?: 0),
                            "movement"      to sa.movement,
                            "isNew"         to sa.is_new,
                            "isReentry"     to sa.is_reentry,
                            "artistId"      to sa.artist_id,
                            "name"          to (artist?.name ?: ""),
                            "photo"         to (artist?.photo_url ?: ""),
                            "plays"         to sa.play_count,
                            "variationPlays" to sa.variation_plays,
                            "peakPosition"  to (sa.peak_position ?: sa.position),
                            "weeksInChart"  to sa.weeks_in_chart
                        )
                    }
                }
                "ALBUM" -> {
                    val albums = db.snapshotAlbumDao()
                        .getBySnapshot(snapshot.snapshot_id)
                    albums.map { sa ->
                        val album = db.albumDao().getById(sa.album_id)
                        val artist = album?.artist_id?.let {
                            db.artistDao().getById(it)
                        }
                        mapOf(
                            "position"      to sa.position,
                            "previousPos"   to (sa.previous_position ?: 0),
                            "movement"      to sa.movement,
                            "isNew"         to sa.is_new,
                            "isReentry"     to sa.is_reentry,
                            "albumId"       to sa.album_id,
                            "title"         to (album?.title ?: ""),
                            "artist"        to (artist?.name ?: ""),
                            "cover"         to (album?.cover_url ?: ""),
                            "plays"         to sa.play_count,
                            "peakPosition"  to (sa.peak_position ?: sa.position),
                            "weeksInChart"  to sa.weeks_in_chart
                        )
                    }
                }
                else -> emptyList()
            }

            gson.toJson(mapOf(
                "items"    to items,
                "period"   to req.period,
                "category" to req.category,
                "date"     to snapshot.date,
                "total"    to items.size
            ))

        } catch (e: Exception) {
            """{"error": "${e.message}", "items": []}"""
        }
    }

    // ════════════════════════════════════════
    // RECORDS
    // ════════════════════════════════════════
    private suspend fun getRecordsData(req: BridgeRequest): String {
        return try {
            val records = db.recordCacheDao().getTop10(
                recordType = req.recordType,
                period = req.period,
                category = req.category
            )

            val items = records.map { record ->
                mapOf(
                    "entityId"    to record.entity_id,
                    "recordType"  to record.record_type,
                    "value"       to record.value,
                    "valueDate"   to (record.value_date ?: ""),
                    "extraData"   to (record.extra_data ?: "{}")
                )
            }

            gson.toJson(mapOf(
                "items"      to items,
                "recordType" to req.recordType,
                "period"     to req.period,
                "category"   to req.category
            ))

        } catch (e: Exception) {
            """{"error": "${e.message}", "items": []}"""
        }
    }

    // ════════════════════════════════════════
    // CERTIFICATIONS
    // ════════════════════════════════════════
    private suspend fun getCertsData(req: BridgeRequest): String {
        return try {
            val entityType = req.category // TRACK ou ALBUM
            val certs = db.certificationDao().getAllByType(entityType)

            // Collecter toutes les certs
            val certList = mutableListOf<Map<String, Any?>>()
            // Flow → on prend juste une snapshot
            // (simplification pour le bridge)

            gson.toJson(mapOf(
                "entityType" to entityType,
                "message"    to "Certifications chargées"
            ))

        } catch (e: Exception) {
            """{"error": "${e.message}"}"""
        }
    }

    // ════════════════════════════════════════
    // HALL OF FAME
    // ════════════════════════════════════════
    private suspend fun getHofData(req: BridgeRequest): String {
        return try {
            val entries = db.hallOfFameDao().getEntryCountByEntity(
                entityId = 0,
                entityType = req.category
            )

            gson.toJson(mapOf(
                "period"     to req.period,
                "entityType" to req.category,
                "total"      to entries
            ))

        } catch (e: Exception) {
            """{"error": "${e.message}"}"""
        }
    }

    // ════════════════════════════════════════
    // PANTHÉON
    // ════════════════════════════════════════
    private suspend fun getPantheonData(req: BridgeRequest): String {
        return try {
            val counts = mapOf(
                "MYTHIQUE"   to db.pantheonStatusDao().getCountByStatus("MYTHIQUE"),
                "LEGENDE"    to db.pantheonStatusDao().getCountByStatus("LEGENDE"),
                "MEGASTAR"   to db.pantheonStatusDao().getCountByStatus("MEGASTAR"),
                "SUPERSTAR"  to db.pantheonStatusDao().getCountByStatus("SUPERSTAR"),
                "STAR"       to db.pantheonStatusDao().getCountByStatus("STAR")
            )

            gson.toJson(mapOf(
                "counts"  to counts,
                "total"   to db.pantheonStatusDao().getTotalCount()
            ))

        } catch (e: Exception) {
            """{"error": "${e.message}"}"""
        }
    }

    // ════════════════════════════════════════
    // NOVA AWARDS
    // ════════════════════════════════════════
    private suspend fun getAwardsData(req: BridgeRequest): String {
        return try {
            val year = if (req.year == 0) {
                java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            } else req.year

            val years = db.novaAwardDao().getAllYears()

            gson.toJson(mapOf(
                "year"       to year,
                "allYears"   to years,
                "latestYear" to (db.novaAwardDao().getLatestYear() ?: year)
            ))

        } catch (e: Exception) {
            """{"error": "${e.message}"}"""
        }
    }

    // ════════════════════════════════════════
    // ACTIONS JS → NATIF
    // ════════════════════════════════════════
    @JavascriptInterface
    fun action(actionJson: String) {
        try {
            val action = gson.fromJson(actionJson, BridgeAction::class.java)

            activity.runOnUiThread {
                when (action.type) {
                    "openPopup" -> {
                        manager.onOpenPopup?.invoke(
                            action.entityType,
                            action.entityId
                        )
                    }
                    "navigate" -> {
                        manager.onNavigate?.invoke(action.tab)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ════════════════════════════════════════
    // INFOS APP
    // ════════════════════════════════════════
    @JavascriptInterface
    fun getAppInfo(): String {
        return gson.toJson(mapOf(
            "version"    to "1.0",
            "theme"      to ThemeEngine.current.id,
            "platform"   to "android"
        ))
    }
}