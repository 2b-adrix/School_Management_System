package com.example.schoolmanagementsystem.frontend.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolmanagementsystem.frontend.ui.theme.EliteGoldGradient

@Composable
fun EliteLoader(
    modifier: Modifier = Modifier
) {
    // Elite Premium Animation (Shimmering Gold Rings)
    val infiniteTransition = rememberInfiniteTransition(label = "EliteLoader")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .alpha(alphaAnim)
                .clip(CircleShape)
                .background(EliteGoldGradient)
        )
        Box(
            modifier = Modifier
                .fillMaxSize(0.7f)
                .clip(CircleShape)
                .background(Color(0xFF121212)) // Inner dark hole
        )
    }
}

@Composable
fun EliteLoadingScreen(
    message: String = "Processing Elite Request..."
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212).copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EliteLoader(modifier = Modifier.size(120.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = message.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    brush = EliteGoldGradient,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

