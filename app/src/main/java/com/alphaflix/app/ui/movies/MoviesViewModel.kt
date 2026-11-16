package com.alphaflix.app.ui.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphaflix.app.api.ApiResult
import com.alphaflix.app.api.MediaRepository
import com.alphaflix.app.models.MediaItem
import kotlinx.coroutines.launch

data class MediaGridState(
    val isLoading: Boolean = false,
    val items: List<MediaItem> = emptyList(),
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val error: String? = null
)

class MoviesViewModel : ViewModel() {

    private val repository = MediaRepository()
    private val _state = MutableLiveData(MediaGridState())
    val state: LiveData<MediaGridState> = _state

    init { loadMovies(1) }

    fun refresh() {
        _state.value = MediaGridState()
        loadMovies(1)
    }

    fun loadMore() {
        val s = _state.value ?: return
        if (s.isLoading || s.currentPage >= s.totalPages) return
        loadMovies(s.currentPage + 1)
    }

    private fun loadMovies(page: Int) {
        viewModelScope.launch {
            _state.value = _state.value?.copy(isLoading = true, error = null)
            when (val result = repository.getMovies(page = page, limit = 20)) {
                is ApiResult.Success -> {
                    val current = if (page == 1) emptyList() else _state.value?.items ?: emptyList()
                    _state.value = _state.value?.copy(
                        isLoading = false,
                        items = current + result.data.data,
                        currentPage = page,
                        totalPages = result.data.pagination?.totalPages ?: 1
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value?.copy(isLoading = false, error = result.message)
                }
                else -> {}
            }
        }
    }
}
