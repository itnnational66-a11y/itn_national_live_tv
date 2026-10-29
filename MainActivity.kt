package com.itn.livetv

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.itn.livetv.player.PlayerViewModel
import com.itn.livetv.ui.LivePlayerScreen
import com.itn.livetv.ui.theme.ITNNationalTheme

/**
 * ITN National Live TV
 * Single-purpose, distraction-free live player activity.
 * Supports both handheld touch Android phones and Android TV / Fire TV remote controls.
 */
class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Lock to landscape orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        // 2. Keep the screen awake during live broadcast
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 3. Set immersive sticky full-screen (hide status & navigation bars)
        setupImmersiveMode()

        // 4. Render Jetpack Compose UI
        setContent {
            ITNNationalTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    color = Color.Black
                ) {
                    LivePlayerScreen(viewModel = playerViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupImmersiveMode()
        playerViewModel.play()
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pause()
    }

    /**
     * Android TV & Media Remote Control Key Handling.
     * Maps hardware D-Pad Center, Play/Pause, and Back buttons.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                playerViewModel.togglePlayPause()
                true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                playerViewModel.play()
                true
            }
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                playerViewModel.pause()
                true
            }
            KeyEvent.KEYCODE_MEDIA_STOP -> {
                playerViewModel.pause()
                true
            }
            KeyEvent.KEYCODE_BACK -> {
                if (playerViewModel.isOverlayVisible.value) {
                    playerViewModel.hideOverlay()
                    true
                } else {
                    super.onKeyDown(keyCode, event)
                }
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun setupImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }
}
