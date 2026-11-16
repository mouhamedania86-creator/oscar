package com.alphaflix.app.ui.channels

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alphaflix.app.api.ApiResult
import com.alphaflix.app.api.MediaRepository
import com.alphaflix.app.databinding.FragmentChannelsBinding
import com.alphaflix.app.models.Channel
import com.alphaflix.app.ui.adapters.ChannelAdapter
import com.alphaflix.app.ui.player.PlayerActivity
import kotlinx.coroutines.launch

class ChannelsViewModel : ViewModel() {
    private val repo = MediaRepository()
    val channels = MutableLiveData<List<Channel>>(emptyList())
    val isLoading = MutableLiveData(false)
    val error = MutableLiveData<String?>()

    init {
        load()
    }

    fun load(category: String? = null, country: String? = null) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            when (val r = repo.getChannels(limit = 100, category = category, country = country)) {
                is ApiResult.Success -> channels.value = r.data.data
                is ApiResult.Error -> error.value = r.message
                else -> {}
            }
            isLoading.value = false
        }
    }

    fun resolveAndPlay(channel: Channel, onResult: (String, String, Boolean) -> Unit, onFail: (String) -> Unit) {
        viewModelScope.launch {
            when (val r = repo.getChannelDetail(channel.id)) {
                is ApiResult.Success -> {
                    val best = r.data.data?.bestStream
                    val url = best?.streamUrl
                    if (url.isNullOrBlank()) {
                        onFail("لا يوجد بث متاح لهذه القناة")
                    } else {
                        onResult(url, channel.displayName, best.preferHls)
                    }
                }
                is ApiResult.Error -> onFail(r.message)
                else -> onFail("خطأ غير معروف")
            }
        }
    }
}

class ChannelsFragment : Fragment() {

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChannelsViewModel by viewModels()
    private lateinit var adapter: ChannelAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChannelAdapter { channel ->
            viewModel.resolveAndPlay(
                channel,
                onResult = { url, title, isHls ->
                    startActivity(Intent(requireContext(), PlayerActivity::class.java).apply {
                        putExtra("streamUrl", url)
                        putExtra("title", title)
                        putExtra("isLive", true)
                        putExtra("isHls", isHls)
                    })
                },
                onFail = { msg ->
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.rvChannels.apply {
            adapter = this@ChannelsFragment.adapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }

        viewModel.channels.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.isLoading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }
        viewModel.error.observe(viewLifecycleOwner) {
            binding.tvError.visibility = if (it != null) View.VISIBLE else View.GONE
            binding.tvError.text = it ?: ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
