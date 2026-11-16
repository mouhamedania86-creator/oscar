package com.alphaflix.app.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.alphaflix.app.api.ApiResult
import com.alphaflix.app.api.MediaRepository
import com.alphaflix.app.databinding.FragmentSearchBinding
import com.alphaflix.app.models.MediaItem
import com.alphaflix.app.ui.adapters.MediaGridAdapter
import com.alphaflix.app.ui.detail.DetailActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val repo = MediaRepository()
    val results = MutableLiveData<List<MediaItem>>(emptyList())
    val isLoading = MutableLiveData(false)

    private var debounceJob: Job? = null

    fun search(query: String) {
        debounceJob?.cancel()
        if (query.isBlank()) { results.value = emptyList(); return }
        debounceJob = viewModelScope.launch {
            delay(400)
            isLoading.value = true
            val movies = when (val r = repo.searchMovies(query)) {
                is ApiResult.Success -> r.data.data
                else -> emptyList()
            }
            val series = when (val r = repo.searchSeries(query)) {
                is ApiResult.Success -> r.data.data
                else -> emptyList()
            }
            results.value = movies + series
            isLoading.value = false
        }
    }
}

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: MediaGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MediaGridAdapter { item ->
            startActivity(Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("mediaId", item.id)
                putExtra("isSeries", item.isSeries)
            })
        }

        binding.recyclerView.apply {
            adapter = this@SearchFragment.adapter
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.search(s?.toString() ?: "")
            }
        })

        viewModel.results.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        binding.etSearch.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
