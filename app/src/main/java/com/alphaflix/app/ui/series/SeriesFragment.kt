package com.alphaflix.app.ui.series

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alphaflix.app.databinding.FragmentMediaGridBinding
import com.alphaflix.app.ui.adapters.MediaGridAdapter
import com.alphaflix.app.ui.detail.DetailActivity


class SeriesFragment : Fragment() {

    private var _binding: FragmentMediaGridBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SeriesViewModel by viewModels()
    private lateinit var adapter: MediaGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvScreenTitle.text = "المسلسلات"

        adapter = MediaGridAdapter { item ->
            startActivity(Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("mediaId", item.id)
                putExtra("isSeries", true)
            })
        }

        binding.recyclerView.apply {
            adapter = this@SeriesFragment.adapter
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    val lm = rv.layoutManager as GridLayoutManager
                    if (lm.findLastVisibleItemPosition() >= lm.itemCount - 4) viewModel.loadMore()
                }
            })
        }

        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = state.isLoading && state.items.isEmpty()
            binding.progressBar.visibility =
                if (state.isLoading && state.items.isNotEmpty()) View.VISIBLE else View.GONE
            adapter.submitList(state.items.toList())
            binding.tvError.visibility = if (state.error != null) View.VISIBLE else View.GONE
            binding.tvError.text = state.error ?: ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
