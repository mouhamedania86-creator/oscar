package com.alphaflix.app.api

import com.alphaflix.app.models.*

sealed class ApiResult<T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error<T>(val message: String, val code: Int = -1) : ApiResult<T>()
    class Loading<T> : ApiResult<T>()
}

class MediaRepository {

    private val api = ApiClient.api

    private suspend fun <T> wrap(block: suspend () -> retrofit2.Response<T>): ApiResult<T> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty body", response.code())
            } else {
                ApiResult.Error("HTTP ${response.code()}: ${response.message()}", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Network error")
        }
    }

    suspend fun getMovies(
        page: Int = 1,
        limit: Int = 20,
        sort: String? = null,
        genre: String? = null,
        country: String? = null,
        contentType: String? = null
    ) = wrap {
        api.getMovies(page, limit, sort = sort, genre = genre, country = country, contentType = contentType)
    }

    suspend fun searchMovies(query: String, page: Int = 1) =
        wrap { api.searchMovies(query = query, page = page) }

    suspend fun getSeries(
        page: Int = 1,
        limit: Int = 20,
        sort: String? = null,
        genre: String? = null,
        country: String? = null
    ) = wrap { api.getSeries(page, limit, sort = sort, genre = genre, country = country) }

    suspend fun searchSeries(query: String, page: Int = 1) =
        wrap { api.searchSeries(query = query, page = page) }

    suspend fun getMovieDetail(id: Int) = wrap { api.getMovieDetail(id) }

    suspend fun getSeriesDetail(id: Int) = wrap { api.getSeriesDetail(id) }

    suspend fun getEpisodes(seasonId: Int, page: Int = 1) =
        wrap { api.getEpisodes(seasonId = seasonId, page = page) }

    suspend fun getChannels(
        page: Int = 1,
        limit: Int = 50,
        category: String? = null,
        country: String? = null
    ) = wrap { api.getChannels(page, limit, category, country) }

    suspend fun searchChannels(query: String, page: Int = 1) =
        wrap { api.searchChannels(query = query, page = page) }

    suspend fun getChannelDetail(id: Int) = wrap { api.getChannelDetail(id) }

    suspend fun getCollections(page: Int = 1) = wrap { api.getCollections(page) }

    suspend fun getHomeData(): Triple<ApiResult<MediaListResponse>, ApiResult<MediaListResponse>, ApiResult<ChannelsResponse>> {
        val movies = try {
            val r = api.getFeaturedMovies()
            if (r.isSuccessful && r.body() != null) ApiResult.Success(r.body()!!)
            else ApiResult.Error("${r.code()}")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "")
        }
        val series = try {
            val r = api.getFeaturedSeries()
            if (r.isSuccessful && r.body() != null) ApiResult.Success(r.body()!!)
            else ApiResult.Error("${r.code()}")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "")
        }
        val channels = try {
            val r = api.getChannels(limit = 20)
            if (r.isSuccessful && r.body() != null) ApiResult.Success(r.body()!!)
            else ApiResult.Error("${r.code()}")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "")
        }
        return Triple(movies, series, channels)
    }
}
