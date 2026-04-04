package com.example.schoolmanagementsystem.frontend.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.frontend.ui.theme.EliteGoldGradient
import com.example.schoolmanagementsystem.frontend.ui.theme.PremiumBlueGradient
import com.example.schoolmanagementsystem.frontend.ui.theme.glassmorphic
import com.example.schoolmanagementsystem.frontend.ui.theme.spacing

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFF0F172A), // Premium Deep Slate
        topBar = {
            Column(
                modifier = Modifier
                    .background(PremiumBlueGradient)
                    .statusBarsPadding()
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    IconButton(onClick = { /* Edit Profile */ }) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }
                
                Text(
                    text = "Elite Profile",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Text(
                    text = "Manage your academic identity and preferences",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image Section with Elite Ring
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(EliteGoldGradient)
                    .padding(4.dp)
                    .background(Color(0xFF0F172A), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                // Active status badge
                Surface(
                    color = Color(0xFF4CAF50),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                        .border(3.dp, Color(0xFF0F172A), CircleShape)
                ) {}
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = state.name,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                color = Color(0xFFD4AF37).copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFD4AF37).copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Badge, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ID: ${state.id}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD4AF37)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphic(
                        backgroundColor = Color.White.copy(alpha = 0.03f),
                        borderColor = Color.White.copy(alpha = 0.08f)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "PERSONAL CREDENTIALS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFD4AF37)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    ProfileInfoRow(Icons.Rounded.Email, "Official Email", state.email)
                    ProfileInfoRow(Icons.Rounded.Phone, "Primary Phone", state.phone)
                    ProfileInfoRow(Icons.Rounded.School, "Academic Role", state.subtitle)
                    ProfileInfoRow(Icons.Rounded.CalendarMonth, "Commencement Date", state.joinedDate)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Performance/Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphic(
                        backgroundColor = Color.White.copy(alpha = 0.03f),
                        borderColor = Color.White.copy(alpha = 0.08f)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "SUBSCRIPTION TIER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.5f),
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            "Elite Institution",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(EliteGoldGradient, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Verified,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TextButton(onClick = { /* Logout */ }) {
                Icon(Icons.Rounded.Logout, contentDescription = null, tint = Color.Red.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out", color = Color.Red.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.05f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                label, 
                style = MaterialTheme.typography.labelSmall, 
                color = Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold
            )
            Text(
                value, 
                style = MaterialTheme.typography.bodyLarge, 
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

