package com.itn.livetv.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.itn.livetv.player.AspectRatioMode
import com.itn.livetv.player.PlayerViewModel
import com.itn.livetv.ui.components.BufferingOverlay
import com.itn.livetv.ui.components.ConnectionErrorOverlay
import com.itn.livetv.ui.components.ITNBrandingWatermark
import com.itn.livetv.ui.components.PlayerControlsOverlay

/**
 * Fullscreen Live Player Screen
 * Distraction-free: ONLY the live TV player and minimal ITN branding.
 */
@OptIn(UnstableApi::class)
@Composable
fun LivePlayerScreen(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isOverlayVisible by viewModel.isOverlayVisible.collectAsState()
    val aspectRatioMode by viewModel.aspectRatioMode.collectAsState()

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                viewModel.toggleOverlay()
            }
    ) {
        // 1. ExoPlayer Surface View via AndroidView interop
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PlayerView(context).apply {
                    useController = false // Custom Compose UI replaces stock controller
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    keepScreenOn = true
                    resizeMode = when (aspectRatioMode) {
                        AspectRatioMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                        AspectRatioMode.ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        AspectRatioMode.STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                    }
                    player = viewModel.exoPlayer
                }
            },
            update = { playerView ->
                playerView.player = viewModel.exoPlayer
                playerView.resizeMode = when (aspectRatioMode) {
                    AspectRatioMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                    AspectRatioMode.ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    AspectRatioMode.STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                }
            }
        )

        // 2. Persistent Minimal Watermark (Top Right)
        ITNBrandingWatermark(
            modifier = Modifier.align(Alignment.TopEnd)
        )

        // 3. Loading & Buffering Overlays
        if (uiState.isLoading || uiState.isBuffering) {
            BufferingOverlay(
                isInitialLoading = uiState.isLoading,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 4. Connection Error Overlay with Reconnect & Retry
        uiState.errorMessage?.let { errorMsg ->
            ConnectionErrorOverlay(
                errorMessage = errorMsg,
                retryAttempt = uiState.retryAttempt,
                onRetry = { viewModel.retry() },
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 5. Touch & Android TV Remote Control Overlay (Auto-hiding)
        AnimatedVisibility(
            visible = isOverlayVisible && uiState.errorMessage == null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            PlayerControlsOverlay(
                isPlaying = uiState.isPlaying,
                isMuted = uiState.isMuted,
                aspectRatioMode = aspectRatioMode,
                streamResolution = uiState.streamResolution,
                onPlayPauseToggle = { viewModel.togglePlayPause() },
                onMuteToggle = { viewModel.toggleMute() },
                onAspectRatioToggle = { viewModel.cycleAspectRatio() },
                onReload = { viewModel.retry() },
                onUserActivity = { viewModel.showOverlayTemporarily() }
            )
        }
    }
}
