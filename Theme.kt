package com.itn.livetv.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val RedPrimary = Color(0xFFD32F2F)
val GoldSecondary = Color(0xFFFFD700)
val DarkBackground = Color(0xFF0A0A0A)
val DarkSurface = Color(0xFF141414)

private val DarkColorScheme = darkColorScheme(
    primary = RedPrimary,
    secondary = GoldSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun ITNNationalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
