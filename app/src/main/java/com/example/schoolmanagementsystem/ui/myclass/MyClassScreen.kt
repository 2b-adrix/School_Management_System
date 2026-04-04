package com.example.schoolmanagementsystem.ui.myclass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.ui.dashboard.DashboardBottomNavigation
import com.example.schoolmanagementsystem.ui.navigation.Screen
import com.example.schoolmanagementsystem.ui.theme.EliteGoldGradient
import com.example.schoolmanagementsystem.ui.theme.PremiumBlueGradient
import com.example.schoolmanagementsystem.ui.theme.glassmorphic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyClassScreen(
    onNavigate: (String) -> Unit,
    viewModel: MyClassViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFF0F172A), // Premium Dark Blue background
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PremiumBlueGradient)
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        "My Academic Hub",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )
                    Text(
                        "Manage your learning journey",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }
        },
        bottomBar = {
            DashboardBottomNavigation(currentRoute = Screen.MyClass.route, onNavigate = onNavigate)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            // 1. Attendance Progress Card
            item {
                AcademicFeatureCard(
                    title = "Academic Attendance",
                    subtitle = "Current progress this semester",
                    value = state.attendancePercentage,
                    insight = state.attendanceInsight,
                    icon = Icons.Rounded.AssignmentTurnedIn,
                    accentColor = Color(0xFF4ADE80),
                    onClick = { /* Navigate to Attendance */ }
                )
            }

            // 2. Timetable & Schedule Card
            item {
                AcademicFeatureCard(
                    title = "Class Timetable",
                    subtitle = "Your daily learning schedule",
                    value = state.timetableClasses,
                    insight = state.timetableInsight,
                    icon = Icons.Rounded.Schedule,
                    accentColor = Color(0xFFFACC15),
                    onClick = { onNavigate(Screen.TimetableList.route) }
                )
            }

            // 3. Subjects & Resources
            item {
                AcademicFeatureCard(
                    title = "Learning Modules",
                    subtitle = "Enrolled subjects and materials",
                    value = "${state.subjectsCount} Subjects",
                    insight = "Access your curriculum and resources in one click.",
                    icon = Icons.Rounded.AutoStories,
                    accentColor = Color(0xFF60A5FA),
                    onClick = { onNavigate(Screen.SubjectList.route) }
                )
            }
            
            // 4. Elite Insights Section (Placeholder for AI/Progress)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Elite Insights",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassmorphic(
                            backgroundColor = Color(0xFFD4AF37).copy(alpha = 0.1f),
                            borderColor = Color(0xFFD4AF37).copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(EliteGoldGradient, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Next Milestone",
                                color = Color(0xFFD4AF37),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                "Complete 3 more assignments to boost your 'Elite Rank' by 5%.",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AcademicFeatureCard(
    title: String,
    subtitle: String,
    value: String,
    insight: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .glassmorphic(
                backgroundColor = Color.White.copy(alpha = 0.03f),
                borderColor = Color.White.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(subtitle, color = Color.Gray, fontSize = 12.sp)
                }
                Text(
                    value,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Elite AI Insight Tag
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.Info,
                    contentDescription = null,
                    tint = Color(0xFFD4AF37),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    insight,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
