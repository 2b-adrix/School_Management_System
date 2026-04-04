package com.example.schoolmanagementsystem.frontend.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.backend.domain.model.AttendanceRecord
import com.example.schoolmanagementsystem.frontend.ui.components.AppCard
import com.example.schoolmanagementsystem.frontend.ui.components.LoadingScreen
import com.example.schoolmanagementsystem.frontend.ui.components.SchoolTopAppBar
import com.example.schoolmanagementsystem.frontend.ui.theme.spacing

@Composable
fun StudentAttendanceScreen(
    onNavigateBack: () -> Unit,
    viewModel: StudentAttendanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            SchoolTopAppBar(
                title = "My Attendance",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Header
            AttendanceSummaryHeader(
                percentage = state.attendancePercentage,
                present = state.presentCount,
                total = state.totalClasses
            )

            Text(
                "Attendance Logs",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            if (state.isLoading) {
                LoadingScreen()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.attendanceRecords) { record ->
                        AttendanceLogItem(record)
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceSummaryHeader(percentage: Float, present: Int, total: Int) {
    AppCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Attendance Rate", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text(
                    "${percentage.toInt()}%",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (percentage >= 75) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Classes Attended", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text("$present / $total", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AttendanceLogItem(record: AttendanceRecord) {
    AppCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(record.date, fontWeight = FontWeight.Medium)
                Text("Subject ID: ${record.subjectId}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (record.isPresent) "Present" else "Absent",
                    color = if (record.isPresent) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (record.isPresent) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                    contentDescription = null,
                    tint = if (record.isPresent) Color(0xFF4CAF50) else Color(0xFFF44336),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

