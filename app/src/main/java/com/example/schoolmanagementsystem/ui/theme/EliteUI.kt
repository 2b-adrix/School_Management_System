package com.example.schoolmanagementsystem.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Elite Gold Gradient
val EliteGoldGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFB08D28), // Darker Gold
        Color(0xFFD4AF37), // Elite Gold
        Color(0xFFFCE18D)  // Shine Gold
    )
)

val PremiumBlueGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1A237E), // Deep Blue
        Color(0xFF3949AB)  // Royal Blue
    )
)

val PremiumSurfaceGradient = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.08f),
        Color.White.copy(alpha = 0.02f)
    )
)

// Glassmorphic Modifier
fun Modifier.glassmorphic(
    backgroundColor: Color = Color.White.copy(alpha = 0.05f),
    borderColor: Color = Color.White.copy(alpha = 0.1f)
) = this
    .background(backgroundColor)
    .border(0.5.dp, borderColor)
