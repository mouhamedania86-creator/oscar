package com.alphaflix.app.ui.series

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphaflix.app.api.ApiResult
import com.alphaflix.app.api.MediaRepository
import com.alphaflix.app.ui.movies.MediaGridState
import kotlinx.coroutines.launch

class SeriesViewModel : ViewModel() {

    private val repository = MediaRepository()
    private val _state = MutableLiveData(MediaGridState())
    val state: LiveData<MediaGridState> = _state

    init { load(1) }

    fun refresh() { _state.value = MediaGridState(); load(1) }

    fun loadMore() {
        val s = _state.value ?: return
        if (s.isLoading || s.currentPage >= s.totalPages) return
        load(s.currentPage + 1)
    }

    private fun load(page: Int) {
        viewModelScope.launch {
            _state.value = _state.value?.copy(isLoading = true, error = null)
            when (val r = repository.getSeries(page = page, limit = 20)) {
                is ApiResult.Success -> {
                    val prev = if (page == 1) emptyList() else _state.value?.items ?: emptyList()
                    _state.value = _state.value?.copy(
                        isLoading = false,
                        items = prev + r.data.data,
                        currentPage = page,
                        totalPages = r.data.pagination?.totalPages ?: 1
                    )
                }
                is ApiResult.Error -> _state.value = _state.value?.copy(isLoading = false, error = r.message)
                else -> {}
            }
        }
    }
}
