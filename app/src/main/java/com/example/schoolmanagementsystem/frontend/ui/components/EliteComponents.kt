package com.example.schoolmanagementsystem.frontend.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.composed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolmanagementsystem.frontend.ui.theme.*

@Composable
fun EliteCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.Transparent,
    gradient: List<Color>? = null,
    borderAlpha: Float = 0.1f,
    content: @Composable ColumnScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    
    // Optimization: Pre-calculate Brush and Shape
    val backgroundBrush = remember(gradient, containerColor) {
        if (gradient != null) Brush.linearGradient(gradient)
        else Brush.verticalGradient(listOf(containerColor, containerColor))
    }
    val cardShape = remember { RoundedCornerShape(24.dp) }

    val baseModifier = modifier
        .fillMaxWidth()
        .clip(cardShape)
        .background(backgroundBrush)
        .border(1.dp, Color.White.copy(alpha = borderAlpha), cardShape)
        .then(
            if (onClick != null) {
                Modifier.clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            } else Modifier
        )

    Column(
        modifier = baseModifier.padding(20.dp),
        content = content
    )
}

@Composable
fun EliteGlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    EliteCard(
        modifier = modifier,
        onClick = onClick,
        containerColor = Color.White.copy(alpha = 0.05f),
        borderAlpha = 0.15f,
        content = content
    )
}

@Composable
fun EliteButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: List<Color> = PremiumGradientGold,
    icon: (@Composable () -> Unit)? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = if (enabled) gradient else listOf(Color.Gray, Color.DarkGray)
                )
            )
            .clickable(enabled = enabled && !isLoading) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        color = Color.Transparent
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null) {
                        icon()
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun EliteEntranceAnimation(
    index: Int,
    content: @Composable () -> Unit
) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible.value = true
    }

    val animationProgress by animateFloatAsState(
        targetValue = if (visible.value) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600, 
            delayMillis = index * 80, // Slightly faster stagger
            easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f) // Smooth, natural entry
        ),
        label = "entrance"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha = animationProgress
                translationY = (1f - animationProgress) * 40.dp.toPx()
                scaleX = 0.95f + (animationProgress * 0.05f)
                scaleY = 0.95f + (animationProgress * 0.05f)
            }
    ) {
        content()
    }
}

/**
 * High-performance Shimmer Modifier for Elite Skeletal Loading.
 * Uses GPU-accelerated drawing.
 */
fun Modifier.eliteShimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.0f),
        Color.White.copy(alpha = 0.08f),
        Color.White.copy(alpha = 0.0f),
    )

    this.drawBehind {
        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim, translateAnim),
            end = Offset(translateAnim + 500f, translateAnim + 500f)
        )
        drawRect(brush = brush)
    }
}

@Composable
fun EliteSkeletonCard(modifier: Modifier = Modifier) {
    EliteGlassCard(
        modifier = modifier
            .height(120.dp)
            .eliteShimmer()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                )
            }
        }
    }
}

@Composable
fun EliteStatCard(
    title: String,
    value: String,
    icon: @Composable () -> Unit,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    EliteCard(
        modifier = modifier,
        gradient = gradient,
        borderAlpha = 0.2f
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black.copy(alpha = 0.6f),
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                )
            }
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    icon()
                }
            }
        }
    }
}
