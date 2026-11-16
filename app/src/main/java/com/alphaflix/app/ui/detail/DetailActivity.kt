package com.alphaflix.app.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.alphaflix.app.R
import com.alphaflix.app.api.ApiResult
import com.alphaflix.app.api.MediaRepository
import com.alphaflix.app.databinding.ActivityDetailBinding
import com.alphaflix.app.models.Episode
import com.alphaflix.app.ui.adapters.EpisodeAdapter
import com.alphaflix.app.ui.player.PlayerActivity
import kotlinx.coroutines.launch
import android.util.Log

class DetailViewModel : ViewModel() {
    private val repo = MediaRepository()

    val title = MutableLiveData<String>()
    val overview = MutableLiveData<String>()
    val bannerUrl = MutableLiveData<String>()
    val posterUrl = MutableLiveData<String>()
    val rating = MutableLiveData<String>()
    val metaLine = MutableLiveData<String>()
    val genres = MutableLiveData<String>()
    val watchUrl = MutableLiveData<String?>()
    val isHls = MutableLiveData(false)
    val episodes = MutableLiveData<List<Episode>>(emptyList())
    val isLoading = MutableLiveData(true)
    val isSeries = MutableLiveData(false)
    val error = MutableLiveData<String?>()

    fun load(id: Int, series: Boolean) {
        isSeries.value = series
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            Log.d("DetailVM", "load(id=$id, series=$series)")
            if (series) {
                when (val r = repo.getSeriesDetail(id)) {
                    is ApiResult.Success -> {
                        Log.d("DetailVM", "series success: ${r.data}")
                        val d = r.data.data
                        if (d == null) {
                            Log.e("DetailVM", "series data is null")
                            error.value = "لا توجد بيانات"
                        } else {
                            title.value = d.displayTitle
                            overview.value = d.story ?: ""
                            bannerUrl.value = d.fullBannerUrl
                            posterUrl.value = d.fullPosterUrl
                            genres.value = d.genres?.joinToString(" · ") ?: ""
                            metaLine.value = buildString {
                                d.releaseYear?.let { append("$it") }
                                d.episodeCount?.let {
                                    if (isNotEmpty()) append(" · ")
                                    append("$it حلقة")
                                }
                                d.ageRating?.let {
                                    if (isNotEmpty()) append(" · ")
                                    append(it)
                                }
                            }
                            val seasonId = d.firstSeasonId
                            if (seasonId != null) {
                                when (val ep = repo.getEpisodes(seasonId)) {
                                    is ApiResult.Success -> episodes.value = ep.data.data
                                    is ApiResult.Error -> error.value = ep.message
                                    else -> {}
                                }
                            }
                        }
                    }
                    is ApiResult.Error -> {
                        Log.e("DetailVM", "series error: ${r.message}")
                        error.value = r.message
                    }
                    else -> {}
                }
            } else {
                when (val r = repo.getMovieDetail(id)) {
                    is ApiResult.Success -> {
                        Log.d("DetailVM", "movie success: ${r.data}")
                        val d = r.data.data
                        if (d == null) {
                            Log.e("DetailVM", "movie data is null")
                            error.value = "لا توجد بيانات"
                        } else {
                            title.value = d.displayTitle
                            overview.value = d.story ?: ""
                            bannerUrl.value = d.fullBannerUrl
                            posterUrl.value = d.fullPosterUrl
                            genres.value = d.genres?.joinToString(" · ") ?: ""
                            rating.value = when (val rv = d.rating) {
                                is Double -> "%.1f".format(rv)
                                is Number -> "%.1f".format(rv.toDouble())
                                is String -> rv
                                else -> ""
                            }
                            metaLine.value = buildString {
                                d.releaseYear?.let { append("$it") }
                                d.runtime?.let {
                                    if (isNotEmpty()) append(" · ")
                                    append("${it}د")
                                }
                                d.ageRating?.let {
                                    if (isNotEmpty()) append(" · ")
                                    append(it)
                                }
                                d.qualitySource?.let {
                                    if (isNotEmpty()) append(" · ")
                                    append(it.uppercase())
                                }
                            }
                            val link = d.bestWatchLink
                            watchUrl.value = link?.streamUrl
                            isHls.value = link?.preferHls == true || d.isHls == 1
                        }
                    }
                    is ApiResult.Error -> {
                        Log.e("DetailVM", "movie error: ${r.message}")
                        error.value = r.message
                    }
                    else -> {}
                }
            }
            isLoading.value = false
        }
    }
}

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var episodeAdapter: EpisodeAdapter
    private var mediaId: Int = 0
    private var series: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaId = intent.getIntExtra("mediaId", 0)
        series = intent.getBooleanExtra("isSeries", false)
        Log.d("DetailVM", "Activity received: mediaId=$mediaId, series=$series")

        episodeAdapter = EpisodeAdapter { ep ->
            val url = ep.playUrl
            if (url.isNotBlank()) {
                openPlayer(url, ep.displayTitle, ep.playIsHls)
            }
        }
        binding.rvEpisodes.apply {
            adapter = episodeAdapter
            layoutManager = LinearLayoutManager(this@DetailActivity)
            setHasFixedSize(true)
        }

        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        viewModel.load(mediaId, series)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
            binding.contentLayout.visibility = if (!it) View.VISIBLE else View.GONE
        }
        viewModel.title.observe(this) { binding.tvTitle.text = it }
        viewModel.overview.observe(this) { binding.tvOverview.text = it }
        viewModel.metaLine.observe(this) { binding.tvMeta.text = it }
        viewModel.genres.observe(this) { binding.tvGenres.text = it }
        viewModel.rating.observe(this) { binding.tvRating.text = it }

        // ✅ زدنا هاد البلوك باش نشوفو الأخطاء
        viewModel.error.observe(this) { err ->
            if (!err.isNullOrBlank()) {
                Log.e("DetailVM", "Error: $err")
                Toast.makeText(this, "خطأ: $err", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.bannerUrl.observe(this) {
            Glide.with(this).load(it).centerCrop()
                .placeholder(R.drawable.placeholder_banner).into(binding.ivBanner)
        }
        viewModel.posterUrl.observe(this) {
            Glide.with(this).load(it).centerCrop()
                .placeholder(R.drawable.placeholder_poster).into(binding.ivPoster)
        }

        viewModel.watchUrl.observe(this) { url ->
            binding.btnWatch.setOnClickListener {
                if (!url.isNullOrBlank()) {
                    openPlayer(url, viewModel.title.value ?: "", viewModel.isHls.value == true)
                }
            }
            binding.btnWatch.isEnabled = !url.isNullOrBlank()
        }

        viewModel.episodes.observe(this) { eps ->
            episodeAdapter.submitList(eps)
            binding.tvEpisodesTitle.visibility = if (eps.isNotEmpty()) View.VISIBLE else View.GONE
            binding.rvEpisodes.visibility = if (eps.isNotEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isSeries.observe(this) { isSeries ->
            binding.btnWatch.visibility = if (!isSeries) View.VISIBLE else View.GONE
        }
    }

    private fun openPlayer(url: String, title: String, isHls: Boolean) {
        startActivity(Intent(this, PlayerActivity::class.java).apply {
            putExtra("streamUrl", url)
            putExtra("title", title)
            putExtra("isLive", false)
            putExtra("isHls", isHls)
        })
    }
}
