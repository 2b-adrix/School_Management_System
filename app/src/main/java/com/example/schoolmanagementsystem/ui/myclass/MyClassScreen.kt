package com.example.schoolmanagementsystem.ui.myclass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.AssignmentTurnedIn
import androidx.compose.material.icons.rounded.LibraryBooks
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.ui.dashboard.DashboardBottomNavigation
import com.example.schoolmanagementsystem.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyClassScreen(
    onNavigate: (String) -> Unit,
    viewModel: MyClassViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = { Text("My Class", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        },
        bottomBar = {
            DashboardBottomNavigation(currentRoute = Screen.MyClass.route, onNavigate = onNavigate)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Attendance Row as per Screenshot 18
            item {
                MyClassItemCard(
                    title = "My Attendance",
                    icon = Icons.Rounded.AssignmentTurnedIn,
                    iconColor = Color(0xFF81C784),
                    badge = state.attendancePercentage,
                    onClick = { /* Navigate to Attendance Details */ }
                )
                Text(
                    text = "The reports have been blocked due to unpaid fees.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                )
            }

            // Timetable Row
            item {
                MyClassItemCard(
                    title = "Timetable",
                    icon = Icons.Rounded.Alarm,
                    iconColor = Color(0xFFFFB74D),
                    badge = state.timetableClasses,
                    onClick = { onNavigate(Screen.TimetableList.route) }
                )
            }

            // Subjects Row
            item {
                MyClassItemCard(
                    title = "My Subjects",
                    icon = Icons.Rounded.LibraryBooks,
                    iconColor = Color(0xFF4DD0E1),
                    badge = state.subjectsCount.toString(),
                    onClick = { onNavigate(Screen.SubjectList.route) }
                )
            }
        }
    }
}

@Composable
fun MyClassItemCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    badge: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = badge,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
