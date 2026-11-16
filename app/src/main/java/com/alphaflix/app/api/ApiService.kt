package com.alphaflix.app.api

import com.alphaflix.app.models.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // ── Movies ──────────────────────────────────────────────────────────────
    @GET("api/movies/")
    suspend fun getMovies(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("app_version") appVersion: String = ApiConstants.APP_VERSION,
        @Query("sort") sort: String? = null,
        @Query("genre") genre: String? = null,
        @Query("country") country: String? = null,
        @Query("content_type") contentType: String? = null,
        @Query("year") year: Int? = null
    ): Response<MediaListResponse>

    @GET("api/movies/")
    suspend fun searchMovies(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("search") query: String,
        @Query("app_version") appVersion: String = ApiConstants.APP_VERSION
    ): Response<MediaListResponse>

    @GET("api/movies/show.php")
    suspend fun getMovieDetail(
        @Query("id") id: Int,
        @Query("app_version") appVersion: String = ApiConstants.APP_VERSION
    ): Response<MovieDetailResponse>

    // ── Series ───────────────────────────────────────────────────────────────
    @GET("api/series/")
    suspend fun getSeries(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("app_version") appVersion: String = ApiConstants.APP_VERSION,
        @Query("sort") sort: String? = null,
        @Query("genre") genre: String? = null,
        @Query("country") country: String? = null,
        @Query("content_type") contentType: String? = null
    ): Response<MediaListResponse>

    @GET("api/series/")
    suspend fun searchSeries(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("search") query: String,
        @Query("app_version") appVersion: String = ApiConstants.APP_VERSION
    ): Response<MediaListResponse>

    @GET("api/series/show.php")
    suspend fun getSeriesDetail(
        @Query("id") id: Int,
        @Query("app_version") appVersion: String = ApiConstants.APP_VERSION
    ): Response<SeriesDetailResponse>

    // Real episodes endpoint from live traffic / probe
    @GET("api/anime/episodes/")
    suspend fun getEpisodes(
        @Query("season_id") seasonId: Int,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50,
        @Query("sort") sort: String = "asc"
    ): Response<EpisodesResponse>

    // ── Channels ─────────────────────────────────────────────────────────────
    @GET("api/channels/")
    suspend fun getChannels(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("category") category: String? = null,
        @Query("country") country: String? = null
    ): Response<ChannelsResponse>

    @GET("api/channels/")
    suspend fun searchChannels(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("search") query: String
    ): Response<ChannelsResponse>

    @GET("api/channels/show.php")
    suspend fun getChannelDetail(
        @Query("id") id: Int
    ): Response<ChannelDetailResponse>

    // ── Collections ──────────────────────────────────────────────────────────
    @GET("api/movies/collections.php")
    suspend fun getCollections(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<CollectionsResponse>

    @GET("api/movies/collections.php")
    suspend fun searchCollections(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("search") query: String
    ): Response<CollectionsResponse>

    // ── Home / Featured ───────────────────────────────────────────────────────
    @GET("api/movies/")
    suspend fun getFeaturedMovies(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("sort") sort: String = "latest",
        @Query("app_version") appVersion: String = ApiConstants.APP_VERSION
    ): Response<MediaListResponse>

    @GET("api/series/")
    suspend fun getFeaturedSeries(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("sort") sort: String = "latest",
        @Query("app_version") appVersion: String = ApiConstants.APP_VERSION
    ): Response<MediaListResponse>
}
