package com.alphaflix.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphaflix.app.api.ApiResult
import com.alphaflix.app.api.MediaRepository
import com.alphaflix.app.models.Channel
import com.alphaflix.app.models.MediaItem
import kotlinx.coroutines.launch

data class HomeState(
    val isLoading: Boolean = false,
    val featuredItem: MediaItem? = null,
    val latestMovies: List<MediaItem> = emptyList(),
    val latestSeries: List<MediaItem> = emptyList(),
    val liveChannels: List<Channel> = emptyList(),
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val repository = MediaRepository()
    private val _state = MutableLiveData(HomeState())
    val state: LiveData<HomeState> = _state

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _state.value = _state.value?.copy(isLoading = true, error = null)
            val (moviesResult, seriesResult, channelsResult) = repository.getHomeData()

            val movies = when (moviesResult) {
                is ApiResult.Success -> moviesResult.data.data
                else -> emptyList()
            }
            val series = when (seriesResult) {
                is ApiResult.Success -> seriesResult.data.data
                else -> emptyList()
            }
            val channels = when (channelsResult) {
                is ApiResult.Success -> channelsResult.data.data
                else -> emptyList()
            }

            val featured = movies.firstOrNull()

            _state.value = HomeState(
                isLoading = false,
                featuredItem = featured,
                latestMovies = movies,
                latestSeries = series,
                liveChannels = channels,
                error = if (movies.isEmpty() && series.isEmpty()) "فشل تحميل المحتوى" else null
            )
        }
    }
}
