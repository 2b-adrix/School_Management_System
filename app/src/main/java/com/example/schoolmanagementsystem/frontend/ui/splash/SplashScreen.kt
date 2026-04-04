package com.example.schoolmanagementsystem.frontend.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.frontend.ui.auth.AuthViewModel
import com.example.schoolmanagementsystem.frontend.ui.theme.EliteGoldGradient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(key1 = true) {
        // 1. Start Animations in parallel
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = { OvershootInterpolator(2.5f).getInterpolation(it) }
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
        
        // 2. Premium Experience Delay
        delay(2500L)
        
        // 3. Navigation with smooth transition
        if (currentUser != null) {
            onNavigateToDashboard()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF121212)
                    )
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alpha.value)
        ) {
            // Branded Logo with Gold Ring
            Surface(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale.value),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(2.dp, EliteGoldGradient)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.School,
                        contentDescription = "Elite Logo",
                        tint = Color(0xFFD4AF37),
                        modifier = Modifier.size(70.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "SIKSHA",
                style = MaterialTheme.typography.headlineLarge.copy(
                    brush = EliteGoldGradient,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 8.sp
                ),
                modifier = Modifier.scale(scale.value)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "KNOWLEDGE IS POWER",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.scale(scale.value)
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            // Premium Loading Indicator
            LinearProgressIndicator(
                modifier = Modifier
                    .width(150.dp)
                    .height(2.dp)
                    .alpha(alpha.value),
                color = Color(0xFFD4AF37),
                trackColor = Color.White.copy(alpha = 0.05f)
            )
        }
        
        // Bottom Branding
        Text(
            text = "POWERED BY SIKSHA CORE",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(alpha.value * 0.5f),
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                letterSpacing = 2.sp
            )
        )
    }
}

private class OvershootInterpolator(private val tension: Float) {
    fun getInterpolation(t: Float): Float {
        var tVar = t
        tVar -= 1.0f
        return tVar * tVar * ((tension + 1) * tVar + tension) + 1.0f
    }
}

