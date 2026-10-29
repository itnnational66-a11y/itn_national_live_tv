package com.itn.livetv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConnectionErrorOverlay(
    errorMessage: String,
    retryAttempt: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRetryFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(Color(0xFF1E1E1E).copy(alpha = 0.95f), RoundedCornerShape(16.dp))
            .border(1.5.dp, Color(0xFFD32F2F), RoundedCornerShape(16.dp))
            .padding(horizontal = 32.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Stream Error",
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(44.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "BROADCAST SIGNAL LOST",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = errorMessage,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            if (retryAttempt > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Automatic reconnect attempt #$retryAttempt...",
                    color = Color(0xFFFFD700),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // TV Remote Focusable Retry Button
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRetryFocused) Color(0xFFFFD700) else Color(0xFFD32F2F),
                    contentColor = if (isRetryFocused) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .onFocusChanged { isRetryFocused = it.isFocused }
                    .focusable()
                    .then(
                        if (isRetryFocused) {
                            Modifier.border(2.dp, Color.White, RoundedCornerShape(8.dp))
                        } else {
                            Modifier
                        }
                    )
            ) {
                Row(verticalAlignment = Alignment.CenterVertizontally) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RECONNECT NOW",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
