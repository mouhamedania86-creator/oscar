package com.alphaflix.app.models

import android.os.Parcelable
import com.alphaflix.app.api.ApiConstants
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

// ── Pagination ────────────────────────────────────────────────────────────────

data class Pagination(
    @SerializedName("page") val page: Int = 1,
    @SerializedName("limit") val limit: Int = 20,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("total_pages") val totalPages: Int = 0
)

// ── Media Item (movies + series share list structure) ─────────────────────────

@Parcelize
data class MediaItem(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title_ar") val titleAr: String? = null,
    @SerializedName("title_en") val titleEn: String? = null,
    @SerializedName("original_title") val originalTitle: String? = null,
    @SerializedName("story") val story: String? = null,
    @SerializedName("poster") val poster: String? = null,
    @SerializedName("banner") val banner: String? = null,
    @SerializedName("trailer_url") val trailerUrl: String? = null,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("rating_count") val ratingCount: Int? = null,
    @SerializedName("views_count") val viewsCount: Int? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("release_year") val releaseYear: @RawValue Any? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("runtime") val runtime: Int? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("content_type") val contentType: String? = null,
    @SerializedName("content_type_label") val contentTypeLabel: String? = null,
    @SerializedName("age_rating") val ageRating: String? = null,
    @SerializedName("is_featured") val isFeatured: Int? = null,
    @SerializedName("adult") val adult: Int? = null,
    @SerializedName("tagline") val tagline: String? = null,
    @SerializedName("quality_source") val qualitySource: String? = null,
    @SerializedName("genres") val genres: List<String>? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("episode_count") val episodeCount: Int? = null,
    @SerializedName("is_hls") val isHls: Int? = null,
    @SerializedName("hls_abr_enabled") val hlsAbrEnabled: Int? = null,
    @SerializedName("omdb_imdb_rating") val imdbRating: String? = null
) : Parcelable {
    val displayTitle: String get() = titleAr ?: titleEn ?: "بدون عنوان"
    val fullPosterUrl: String get() = "${ApiConstants.IMAGE_BASE}${poster ?: ""}"
    val fullBannerUrl: String get() = "${ApiConstants.IMAGE_BASE}${banner ?: ""}"
    val displayRating: String get() {
        return when (val r = rating) {
            is Double -> "%.1f".format(r)
            is Number -> "%.1f".format(r.toDouble())
            is String -> r
            else -> imdbRating ?: "N/A"
        }
    }
    val isSeries: Boolean get() = type == "series" || episodeCount != null
}

data class MediaListResponse(
    @SerializedName("status") val status: String = "",
    @SerializedName("data") val data: List<MediaItem> = emptyList(),
    @SerializedName("pagination") val pagination: Pagination? = null
)

// ── Watch / Stream links ──────────────────────────────────────────────────────

@Parcelize
data class WatchLink(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("server_name") val serverName: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("quality") val quality: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("quality_source") val qualitySource: String? = null,
    @SerializedName("order_index") val orderIndex: Int? = null,
    @SerializedName("is_hls") val isHls: Int? = null,
    @SerializedName("is_abr") val isAbr: Int? = null
) : Parcelable {
    val streamUrl: String get() = url ?: ""
    val preferHls: Boolean
        get() = isHls == 1 || (url?.contains(".m3u8", true) == true) ||
            (url?.endsWith(".css", true) == true)
}

@Parcelize
data class StreamLink(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("server_name") val serverName: String? = null,
    @SerializedName("stream_url") val streamUrl: String? = null,
    @SerializedName("stream_type") val streamType: String? = null,
    @SerializedName("quality") val quality: String? = null,
    @SerializedName("is_xui") val isXui: Boolean? = null,
    @SerializedName("is_r2") val isR2: Boolean? = null,
    @SerializedName("premium_only") val premiumOnly: Boolean? = null,
    @SerializedName("referrer") val referrer: String? = null,
    @SerializedName("user_agent") val userAgent: String? = null
) : Parcelable {
    val preferHls: Boolean
        get() = streamUrl?.contains(".m3u8", true) == true ||
            streamUrl?.endsWith(".css", true) == true
}

// ── Movie Detail ──────────────────────────────────────────────────────────────

data class MovieDetailResponse(
    @SerializedName("status") val status: String = "",
    @SerializedName("data") val data: MovieDetail? = null
)

@Parcelize
data class MovieDetail(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title_ar") val titleAr: String? = null,
    @SerializedName("title_en") val titleEn: String? = null,
    @SerializedName("story") val story: String? = null,
    @SerializedName("poster") val poster: String? = null,
    @SerializedName("banner") val banner: String? = null,
    @SerializedName("trailer_url") val trailerUrl: String? = null,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("genres") val genres: List<String>? = null,
    @SerializedName("runtime") val runtime: Int? = null,
    @SerializedName("release_year") val releaseYear: @RawValue Any? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("content_type") val contentType: String? = null,
    @SerializedName("age_rating") val ageRating: String? = null,
    @SerializedName("quality_source") val qualitySource: String? = null,
    @SerializedName("is_hls") val isHls: Int? = null,
    @SerializedName("watch_links") val watchLinks: List<WatchLink>? = null,
    @SerializedName("download_links") val downloadLinks: List<WatchLink>? = null,
    @SerializedName("tagline") val tagline: String? = null
) : Parcelable {
    val displayTitle: String get() = titleAr ?: titleEn ?: "بدون عنوان"
    val fullPosterUrl: String get() = "${ApiConstants.IMAGE_BASE}${poster ?: ""}"
    val fullBannerUrl: String get() = "${ApiConstants.IMAGE_BASE}${banner ?: ""}"
    val bestWatchLink: WatchLink?
        get() = watchLinks
            ?.filter { !it.url.isNullOrBlank() }
            ?.sortedByDescending { link ->
                when {
                    link.quality?.contains("1080", true) == true -> 3
                    link.quality?.contains("720", true) == true -> 2
                    link.quality?.contains("480", true) == true -> 1
                    else -> 0
                }
            }
            ?.firstOrNull()
}

// ── Series Detail ─────────────────────────────────────────────────────────────

data class SeriesDetailResponse(
    @SerializedName("status") val status: String = "",
    @SerializedName("data") val data: SeriesDetail? = null
)

@Parcelize
data class Season(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("series_id") val seriesId: Int? = null,
    @SerializedName("season_number") val seasonNumber: Int = 1,
    @SerializedName("title") val title: String? = null,
    @SerializedName("poster") val poster: String? = null,
    @SerializedName("release_year") val releaseYear: @RawValue Any? = null,
    @SerializedName("episodes_count") val episodesCount: Int? = null
) : Parcelable

@Parcelize
data class SeriesDetail(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title_ar") val titleAr: String? = null,
    @SerializedName("title_en") val titleEn: String? = null,
    @SerializedName("story") val story: String? = null,
    @SerializedName("poster") val poster: String? = null,
    @SerializedName("banner") val banner: String? = null,
    @SerializedName("genres") val genres: List<String>? = null,
    @SerializedName("episode_count") val episodeCount: Int? = null,
    @SerializedName("season_count") val seasonCount: Int? = null,
    @SerializedName("status") val seriesStatus: String? = null,
    @SerializedName("age_rating") val ageRating: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("release_year") val releaseYear: @RawValue Any? = null,
    @SerializedName("is_hls") val isHls: Int? = null,
    @SerializedName("seasons") val seasons: List<Season>? = null
) : Parcelable {
    val displayTitle: String get() = titleAr ?: titleEn ?: "بدون عنوان"
    val fullPosterUrl: String get() = "${ApiConstants.IMAGE_BASE}${poster ?: ""}"
    val fullBannerUrl: String get() = "${ApiConstants.IMAGE_BASE}${banner ?: ""}"
    val firstSeasonId: Int? get() = seasons?.firstOrNull()?.id
}

// ── Episodes ──────────────────────────────────────────────────────────────────

data class EpisodesResponse(
    @SerializedName("status") val status: String = "",
    @SerializedName("data") val data: List<Episode> = emptyList()
)

@Parcelize
data class Episode(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title_ar") val titleAr: String? = null,
    @SerializedName("title_en") val titleEn: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("episode_number") val episodeNumber: Int = 0,
    @SerializedName("season_number") val seasonNumber: Int = 1,
    @SerializedName("duration") val duration: Int? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("poster") val poster: String? = null,
    @SerializedName("watch_url") val watchUrl: String? = null,
    @SerializedName("stream_url") val streamUrlField: String? = null,
    @SerializedName("download_url") val downloadUrl: String? = null,
    @SerializedName("is_hls") val isHls: Int? = null,
    @SerializedName("watch_links") val watchLinks: List<WatchLink>? = null
) : Parcelable {
    val displayTitle: String
        get() = titleAr ?: titleEn ?: title ?: "الحلقة $episodeNumber"
    val fullThumbnailUrl: String
        get() = "${ApiConstants.IMAGE_BASE}${thumbnail ?: poster ?: ""}"
    val playUrl: String
        get() = watchLinks?.firstOrNull { !it.url.isNullOrBlank() }?.url
            ?: watchUrl
            ?: streamUrlField
            ?: ""
    val playIsHls: Boolean
        get() = isHls == 1 || playUrl.contains(".m3u8", true) || playUrl.endsWith(".css", true)
}

// ── Channels ──────────────────────────────────────────────────────────────────

data class ChannelsResponse(
    @SerializedName("status") val status: String = "",
    @SerializedName("data") val data: List<Channel> = emptyList(),
    @SerializedName("pagination") val pagination: Pagination? = null,
    @SerializedName("filters") val filters: ChannelFilters? = null
)

@Parcelize
data class Channel(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("name_ar") val nameAr: String? = null,
    @SerializedName("logo") val logo: String? = null,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("stream_url") val streamUrl: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("category_label") val categoryLabel: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("country_name") val countryName: String? = null,
    @SerializedName("flag_emoji") val flagEmoji: String? = null,
    @SerializedName("is_hls") val isHls: Int? = null,
    @SerializedName("streams_count") val streamsCount: Int? = null
) : Parcelable {
    val displayName: String get() = nameAr ?: name ?: "قناة"
    val fullLogoUrl: String get() = "${ApiConstants.IMAGE_BASE}${logo ?: ""}"
}

data class ChannelDetailResponse(
    @SerializedName("status") val status: String = "",
    @SerializedName("data") val data: ChannelDetail? = null
)

@Parcelize
data class ChannelDetail(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("logo") val logo: String? = null,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("category_label") val categoryLabel: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("country_name") val countryName: String? = null,
    @SerializedName("streams") val streams: List<StreamLink>? = null
) : Parcelable {
    val displayName: String get() = name ?: "قناة"
    val bestStream: StreamLink?
        get() = streams
            ?.filter { !it.streamUrl.isNullOrBlank() }
            ?.sortedByDescending { s ->
                when {
                    s.quality?.contains("1080", true) == true -> 3
                    s.quality?.contains("720", true) == true -> 2
                    s.quality?.contains("480", true) == true -> 1
                    else -> 0
                }
            }
            ?.firstOrNull()
}

data class ChannelFilters(
    @SerializedName("countries") val countries: List<CountryFilter>? = null,
    @SerializedName("categories") val categories: List<CategoryFilter>? = null
)

data class CountryFilter(
    @SerializedName("code") val code: String = "",
    @SerializedName("name_ar") val nameAr: String = "",
    @SerializedName("flag_emoji") val flagEmoji: String = "",
    @SerializedName("count") val count: Int = 0
)

data class CategoryFilter(
    @SerializedName("key") val key: String = "",
    @SerializedName("count") val count: Int = 0,
    @SerializedName("label") val label: String = ""
)

// ── Collections ───────────────────────────────────────────────────────────────

data class CollectionsResponse(
    @SerializedName("status") val status: String = "",
    @SerializedName("data") val data: List<CollectionItem> = emptyList(),
    @SerializedName("pagination") val pagination: Pagination? = null
)

@Parcelize
data class CollectionItem(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("name_ar") val nameAr: String? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("overview") val overview: String? = null,
    @SerializedName("poster") val poster: String? = null,
    @SerializedName("backdrop") val backdrop: String? = null,
    @SerializedName("movie_count") val movieCount: Int = 0,
    @SerializedName("total_views") val totalViews: Int = 0
) : Parcelable {
    val displayName: String get() = nameAr ?: name ?: "مجموعة"
    val fullPosterUrl: String get() = "${ApiConstants.IMAGE_BASE}${poster ?: ""}"
    val fullBackdropUrl: String get() = "${ApiConstants.IMAGE_BASE}${backdrop ?: ""}"
}
