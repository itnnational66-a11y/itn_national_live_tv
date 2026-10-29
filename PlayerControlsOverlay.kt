package com.itn.livetv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itn.livetv.player.AspectRatioMode

@Composable
fun PlayerControlsOverlay(
    isPlaying: Boolean,
    isMuted: Boolean,
    aspectRatioMode: AspectRatioMode,
    streamResolution: String,
    onPlayPauseToggle: () -> Unit,
    onMuteToggle: () -> Unit,
    onAspectRatioToggle: () -> Unit,
    onReload: () -> Unit,
    onUserActivity: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.85f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.90f)
                    )
                )
            )
    ) {
        // Top Bar: Channel Header & Live Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertizontally,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertizontally) {
                // Pulsing Red LIVE Badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFFD32F2F), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "● LIVE",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "ITN NATIONAL",
                        color = Color(0xFFFFD700),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Live Stream Broadcast • 24x7 National News",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp
                    )
                }
            }

            // Quality & Mode Indicator
            Row(verticalAlignment = Alignment.CenterVertizontally) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = streamResolution,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Center: Large Play / Pause button
        TvFocusableIconButton(
            onClick = {
                onUserActivity()
                onPlayPauseToggle()
            },
            icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            modifier = Modifier
                .align(Alignment.Center)
                .size(72.dp)
        )

        // Bottom Bar: Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertizontally
        ) {
            // Left: Audio Mute / Unmute
            Row(verticalAlignment = Alignment.CenterVertizontally) {
                TvFocusableIconButton(
                    onClick = {
                        onUserActivity()
                        onMuteToggle()
                    },
                    icon = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                    contentDescription = if (isMuted) "Unmute" else "Mute"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMuted) "MUTED" else "AUDIO ON",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Right: Aspect Ratio & Reload Stream
            Row(verticalAlignment = Alignment.CenterVertizontally) {
                TvFocusableIconButton(
                    onClick = {
                        onUserActivity()
                        onAspectRatioToggle()
                    },
                    icon = Icons.Default.AspectRatio,
                    contentDescription = "Aspect Ratio: ${aspectRatioMode.name}"
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = aspectRatioMode.name,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.width(16.dp))

                TvFocusableIconButton(
                    onClick = {
                        onUserActivity()
                        onReload()
                    },
                    icon = Icons.Default.Refresh,
                    contentDescription = "Reload Live Stream"
                )
            }
        }
    }
}

/**
 * TV Remote focus-aware button.
 * Displays high-contrast amber border when focused via Android TV D-Pad.
 */
@Composable
fun TvFocusableIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    val focusBorderModifier = if (isFocused) {
        Modifier.border(2.5.dp, Color(0xFFFFD700), CircleShape)
    } else {
        Modifier
    }

    IconButton(
        onClick = onClick,
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .then(focusBorderModifier)
            .background(
                if (isFocused) Color(0x33FFD700) else Color.White.copy(alpha = 0.15f),
                CircleShape
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isFocused) Color(0xFFFFD700) else Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}
