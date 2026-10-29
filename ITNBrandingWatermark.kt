package com.itn.livetv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ITNBrandingWatermark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertizontally) {
            // Ruby Red emblem
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFFD32F2F), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "ITN NATIONAL",
                    color = Color(0xFFFFD700),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
