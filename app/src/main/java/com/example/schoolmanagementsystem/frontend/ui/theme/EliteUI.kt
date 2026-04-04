package com.example.schoolmanagementsystem.frontend.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Siksha Gold Gradient (Refined for SIKSHA branding)
val EliteGoldGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFD700), // Vibrant Gold
        Color(0xFFD4AF37), // Metallic Gold
        Color(0xFFFFE082)  // Soft Shine
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

