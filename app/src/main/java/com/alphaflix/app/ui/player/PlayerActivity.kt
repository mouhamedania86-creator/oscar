package com.alphaflix.app.ui.player

import android.app.PictureInPictureParams
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import com.alphaflix.app.api.ApiConstants
import com.alphaflix.app.api.IronHeadersGenerator
import com.alphaflix.app.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null

    private var streamUrl = ""
    private var title = ""
    private var isLive = false
    private var isHls = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        streamUrl = intent.getStringExtra("streamUrl") ?: ""
        title = intent.getStringExtra("title") ?: ""
        isLive = intent.getBooleanExtra("isLive", false)
        isHls = intent.getBooleanExtra("isHls", false)

        binding.tvTitle.text = title
        binding.btnBack.setOnClickListener { finish() }
        binding.btnPip.setOnClickListener { enterPip() }
        binding.btnPip.visibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) View.VISIBLE else View.GONE

        if (streamUrl.isBlank()) {
            finish()
            return
        }
        initPlayer()
    }

    private fun initPlayer() {
        val headers = buildStreamHeaders(streamUrl)
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(headers)
            .setUserAgent("okhttp/4.12.0")
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(30_000)
            .setAllowCrossProtocolRedirects(true)

        player = ExoPlayer.Builder(this).build().also { exo ->
            binding.playerView.player = exo
            binding.playerView.useController = true
            binding.playerView.controllerShowTimeoutMs = 3000

            val mediaItem = MediaItem.fromUri(streamUrl)

            if (isHls || streamUrl.contains(".m3u8", ignoreCase = true) || streamUrl.contains(".css", ignoreCase = true)) {
                val source = HlsMediaSource.Factory(httpDataSourceFactory)
                    .createMediaSource(mediaItem)
                exo.setMediaSource(source)
            } else {
                exo.setMediaItem(mediaItem)
            }

            exo.prepare()
            exo.play()

            exo.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    binding.progressBar.visibility = when (state) {
                        Player.STATE_BUFFERING -> View.VISIBLE
                        else -> View.GONE
                    }
                }
            })
        }
    }

    private fun buildStreamHeaders(url: String): Map<String, String> {
        val iron = IronHeadersGenerator(
            certSha256 = ApiConstants.CERT_SHA256,
            pkgName = ApiConstants.PKG_NAME
        ).generate(url)

        return mapOf(
            "Authorization" to "Bearer ${ApiConstants.BEARER_TOKEN}",
            "X-Device-Id" to ApiConstants.DEVICE_ID,
            "X-Key-Id" to ApiConstants.KEY_ID,
            "X-App-Ver" to ApiConstants.APP_VERSION,
            "X-Iron-Sig" to (iron["x-iron-sig"] ?: ""),
            "X-Iron-Ts" to (iron["x-iron-ts"] ?: ""),
            "X-Iron-Nonce" to (iron["x-iron-nonce"] ?: ""),
            "User-Agent" to "okhttp/4.12.0",
            "Accept-Encoding" to "gzip",
            "Connection" to "keep-alive"
        )
    }

    private fun enterPip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()
            enterPictureInPictureMode(params)
        }
    }

    override fun onPictureInPictureModeChanged(isInPipMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPipMode)
        binding.playerView.useController = !isInPipMode
        binding.topBar.visibility = if (isInPipMode) View.GONE else View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onStart() {
        super.onStart()
        player?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
