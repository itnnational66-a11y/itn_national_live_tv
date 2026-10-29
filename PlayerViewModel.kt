package com.itn.livetv.player

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * State of ITN National Live Broadcast Player
 */
data class PlayerUiState(
    val isLoading: Boolean = true,
    val isBuffering: Boolean = false,
    val isPlaying: Boolean = false,
    val isMuted: Boolean = false,
    val errorMessage: String? = null,
    val retryAttempt: Int = 0,
    val streamResolution: String = "1280x720 (HD)",
    val streamUrl: String = "https://newsliveindia.com:4433/itn/index.m3u8"
)

enum class AspectRatioMode {
    FIT,       // Standard letterbox 16:9
    ZOOM,      // Crop edges to fill ultra-wide screens
    STRETCH    // Fill display bounds
}

@OptIn(UnstableApi::class)
class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val _isOverlayVisible = MutableStateFlow(true)
    val isOverlayVisible: StateFlow<Boolean> = _isOverlayVisible.asStateFlow()

    private val _aspectRatioMode = MutableStateFlow(AspectRatioMode.FIT)
    val aspectRatioMode: StateFlow<AspectRatioMode> = _aspectRatioMode.asStateFlow()

    var exoPlayer: ExoPlayer? = null
        private set

    private val autoHideHandler = Handler(Looper.getMainLooper())
    private val hideOverlayRunnable = Runnable { _isOverlayVisible.value = false }

    companion object {
        const val HLS_STREAM_URL = "https://newsliveindia.com:4433/itn/index.m3u8"
        const val AUTO_HIDE_DELAY_MS = 4500L
        const val MAX_AUTOMATIC_RETRIES = 5
    }

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        val context = getApplication<Application>()

        // 1. Low-latency live buffer tuning
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 2000,
                /* maxBufferMs = */ 10000,
                /* bufferForPlaybackMs = */ 1500,
                /* bufferForPlaybackAfterRebufferMs = */ 2500
            )
            .build()

        // 2. HTTP Data Source with user agent
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("ITN-National-AndroidTV/1.0 (Linux; Android)")
            .setConnectTimeoutMs(10000)
            .setReadTimeoutMs(10000)
            .setAllowCrossProtocolRedirects(true)

        // 3. HLS Media Source Factory
        val hlsMediaSourceFactory = HlsMediaSource.Factory(httpDataSourceFactory)
            .setAllowChunklessPreparation(true)

        // 4. Configure Audio Attributes for TV and Mobile
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        // 5. Build ExoPlayer
        val player = ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .setMediaSourceFactory(hlsMediaSourceFactory)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus = */ true)
            .build()
            .apply {
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_OFF
            }

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _uiState.value = _uiState.value.copy(
                            isBuffering = true,
                            errorMessage = null
                        )
                    }
                    Player.STATE_READY -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isBuffering = false,
                            errorMessage = null,
                            retryAttempt = 0
                        )
                        scheduleControlsAutoHide()
                    }
                    Player.STATE_ENDED -> {
                        // Live stream ended or dropped; attempt reconnect
                        retry()
                    }
                    Player.STATE_IDLE -> {
                        // Idle state
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
            }

            override fun onPlayerError(error: PlaybackException) {
                val currentAttempt = _uiState.value.retryAttempt + 1
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isBuffering = false,
                    retryAttempt = currentAttempt,
                    errorMessage = "Broadcast connection interrupted (${error.errorCodeName}). Reconnecting..."
                )

                // Automatic reconnect with backoff
                if (currentAttempt <= MAX_AUTOMATIC_RETRIES) {
                    val backoffDelay = (currentAttempt * 2000L).coerceAtMost(10000L)
                    autoHideHandler.postDelayed({
                        prepareAndPlay(HLS_STREAM_URL)
                    }, backoffDelay)
                }
            }
        })

        exoPlayer = player
        prepareAndPlay(HLS_STREAM_URL)
    }

    fun prepareAndPlay(url: String) {
        exoPlayer?.let { player ->
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val mediaItem = MediaItem.Builder()
                .setUri(url)
                .setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8)
                .build()
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun togglePlayPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
            showOverlayTemporarily()
        }
    }

    fun toggleMute() {
        exoPlayer?.let { player ->
            val newMute = !_uiState.value.isMuted
            player.volume = if (newMute) 0f else 1f
            _uiState.value = _uiState.value.copy(isMuted = newMute)
            showOverlayTemporarily()
        }
    }

    fun cycleAspectRatio() {
        _aspectRatioMode.value = when (_aspectRatioMode.value) {
            AspectRatioMode.FIT -> AspectRatioMode.ZOOM
            AspectRatioMode.ZOOM -> AspectRatioMode.STRETCH
            AspectRatioMode.STRETCH -> AspectRatioMode.FIT
        }
        showOverlayTemporarily()
    }

    fun retry() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            isBuffering = false,
            errorMessage = null,
            retryAttempt = 0
        )
        prepareAndPlay(HLS_STREAM_URL)
    }

    fun toggleOverlay() {
        _isOverlayVisible.value = !_isOverlayVisible.value
        if (_isOverlayVisible.value) {
            scheduleControlsAutoHide()
        } else {
            autoHideHandler.removeCallbacks(hideOverlayRunnable)
        }
    }

    fun showOverlayTemporarily() {
        _isOverlayVisible.value = true
        scheduleControlsAutoHide()
    }

    fun hideOverlay() {
        _isOverlayVisible.value = false
        autoHideHandler.removeCallbacks(hideOverlayRunnable)
    }

    private fun scheduleControlsAutoHide() {
        autoHideHandler.removeCallbacks(hideOverlayRunnable)
        autoHideHandler.postDelayed(hideOverlayRunnable, AUTO_HIDE_DELAY_MS)
    }

    override fun onCleared() {
        super.onCleared()
        autoHideHandler.removeCallbacks(hideOverlayRunnable)
        exoPlayer?.release()
        exoPlayer = null
    }
}
