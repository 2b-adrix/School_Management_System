package com.example.schoolmanagementsystem.frontend.ui.myclass

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.frontend.ui.dashboard.DashboardBottomNavigation
import com.example.schoolmanagementsystem.frontend.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyClassScreen(
    onNavigate: (String) -> Unit,
    viewModel: MyClassViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFF090C0E), // Ultra dark background
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    "My Academic Hub",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    "SIKSHA Elite Learning Portal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Attendance Progress Card
            item {
                AcademicFeatureCard(
                    title = "Academic Attendance",
                    subtitle = "Current progress this semester",
                    value = state.attendancePercentage,
                    insight = state.attendanceInsight,
                    icon = Icons.Rounded.AssignmentTurnedIn,
                    accentColor = Color(0xFF4CAF50), // Siksha Green
                    onClick = { onNavigate(Screen.AttendanceHistory.route) },
                    content = {
                        if (state.attendanceTrend.isNotEmpty()) {
                            AttendanceMiniChart(
                                trend = state.attendanceTrend,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
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
                    accentColor = Color(0xFF4FC3F7), // Siksha Blue
                    onClick = { onNavigate(Screen.TimetableList.createRoute(state.classId)) }
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
                    accentColor = Color(0xFF9C27B0), // Siksha Purple
                    onClick = { onNavigate(Screen.SubjectList.route) }
                )
            }
            
            // 4. AI Elite Insights Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.AutoAwesome, 
                        contentDescription = null, 
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFFFFD54F) // Siksha Yellow
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "AI ELITE INSIGHTS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold, 
                            letterSpacing = 1.2.sp,
                            color = Color(0xFFFFD54F)
                        )
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111619)),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A2127))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = Color(0xFFFFD54F).copy(alpha = 0.15f)
                        ) {
                            Icon(Icons.Rounded.Stars, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.padding(12.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Next Milestone",
                                color = Color(0xFFFFD54F),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                "Complete 3 more assignments to boost your 'Academic Rank' by 5%.",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
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
    onClick: () -> Unit,
    content: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111619))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = accentColor.copy(alpha = 0.15f)
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.padding(14.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title, 
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        subtitle, 
                        style = MaterialTheme.typography.bodySmall, 
                        color = Color.Gray
                    )
                }
                Text(
                    value,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                )
            }
            
            if (content != null) {
                Spacer(modifier = Modifier.height(20.dp))
                content()
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Insight Tag
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A2127), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.Info,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    insight,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
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

@Composable
fun AttendanceMiniChart(trend: List<Float>, color: Color) {
    Column {
        Text(
            "WEEKLY ACTIVITY",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            val width = size.width
            val height = size.height
            val spacing = width / (trend.size.coerceAtLeast(1))
            
            trend.forEachIndexed { index, value ->
                val barHeight = if (value > 0) height else height * 0.2f
                val startX = index * spacing + (spacing * 0.2f)
                val barWidth = spacing * 0.6f
                
                drawRoundRect(
                    color = if (value > 0) color else color.copy(alpha = 0.2f),
                    topLeft = Offset(startX, height - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
    }
}
