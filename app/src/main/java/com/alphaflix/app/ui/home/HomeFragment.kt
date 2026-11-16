package com.alphaflix.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.alphaflix.app.api.ApiResult
import com.alphaflix.app.api.MediaRepository
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.alphaflix.app.R
import com.alphaflix.app.databinding.FragmentHomeBinding
import com.alphaflix.app.models.MediaItem
import com.alphaflix.app.ui.adapters.ChannelAdapter
import com.alphaflix.app.ui.adapters.MediaHorizontalAdapter
import com.alphaflix.app.ui.detail.DetailActivity
import com.alphaflix.app.ui.player.PlayerActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var moviesAdapter: MediaHorizontalAdapter
    private lateinit var seriesAdapter: MediaHorizontalAdapter
    private lateinit var channelsAdapter: ChannelAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeViewModel()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadHome()
        }

        binding.btnSearch.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_search)
        }
    }

    private fun setupRecyclerViews() {
        moviesAdapter = MediaHorizontalAdapter { item -> navigateToDetail(item) }
        seriesAdapter = MediaHorizontalAdapter { item -> navigateToDetail(item) }
        channelsAdapter = ChannelAdapter { channel ->
            lifecycleScope.launch {
                when (val r = MediaRepository().getChannelDetail(channel.id)) {
                    is ApiResult.Success -> {
                        val best = r.data.data?.bestStream
                        val url = best?.streamUrl
                        if (url.isNullOrBlank()) {
                            Toast.makeText(requireContext(), "لا يوجد بث", Toast.LENGTH_SHORT).show()
                        } else {
                            openPlayer(url, channel.displayName, true, best.preferHls)
                        }
                    }
                    is ApiResult.Error -> Toast.makeText(requireContext(), r.message, Toast.LENGTH_SHORT).show()
                    else -> {}
                }
            }
        }

        binding.rvMovies.apply {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        binding.rvSeries.apply {
            adapter = seriesAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        binding.rvChannels.apply {
            adapter = channelsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = state.isLoading
            binding.shimmerLayout.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            binding.contentScroll.visibility = if (!state.isLoading) View.VISIBLE else View.GONE

            state.featuredItem?.let { item ->
                binding.tvFeaturedTitle.text = item.displayTitle
                binding.tvFeaturedTagline.text =
                    item.tagline ?: item.genres?.joinToString(" · ") ?: ""
                binding.tvFeaturedMeta.text = buildString {
                    append(item.displayRating)
                    item.releaseYear?.let { append(" · $it") }
                    item.ageRating?.let { append(" · $it") }
                }
                Glide.with(this)
                    .load(item.fullBannerUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_banner)
                    .into(binding.ivFeaturedBanner)

                binding.btnFeaturedWatch.setOnClickListener { navigateToDetail(item) }
            }

            moviesAdapter.submitList(state.latestMovies)
            seriesAdapter.submitList(state.latestSeries)
            channelsAdapter.submitList(state.liveChannels)

            binding.groupChannels.visibility =
                if (state.liveChannels.isNotEmpty()) View.VISIBLE else View.GONE

            state.error?.let {
                binding.tvError.text = it
                binding.tvError.visibility = View.VISIBLE
            } ?: run {
                binding.tvError.visibility = View.GONE
            }
        }
    }

    private fun navigateToDetail(item: MediaItem) {
        startActivity(Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra("mediaId", item.id)
            putExtra("isSeries", item.isSeries)
        })
    }

    private fun openPlayer(streamUrl: String, title: String, isLive: Boolean, isHls: Boolean) {
        if (streamUrl.isBlank()) return
        startActivity(Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra("streamUrl", streamUrl)
            putExtra("title", title)
            putExtra("isLive", isLive)
            putExtra("isHls", isHls)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
