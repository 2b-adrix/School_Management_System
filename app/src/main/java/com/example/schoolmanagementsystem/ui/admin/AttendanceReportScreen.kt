package com.example.schoolmanagementsystem.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.model.AttendanceRecord
import com.example.schoolmanagementsystem.domain.model.SchoolClass
import com.example.schoolmanagementsystem.ui.components.AppCard
import com.example.schoolmanagementsystem.ui.components.LoadingScreen
import com.example.schoolmanagementsystem.ui.components.SchoolTopAppBar
import com.example.schoolmanagementsystem.ui.theme.spacing
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AttendanceReportScreen(
    onNavigateBack: () -> Unit,
    viewModel: AttendanceReportViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedClass by remember { mutableStateOf<SchoolClass?>(null) }
    var selectedDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SchoolTopAppBar(
                title = "Attendance Report",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedClass?.name ?: "Select Class")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        state.classes.forEach { schoolClass ->
                            DropdownMenuItem(
                                text = { Text(schoolClass.name) },
                                onClick = {
                                    selectedClass = schoolClass
                                    expanded = false
                                    viewModel.loadAttendanceReport(schoolClass.id, selectedDate)
                                }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { 
                        selectedDate = it 
                        selectedClass?.let { cls -> viewModel.loadAttendanceReport(cls.id, it) }
                    },
                    label = { Text("Date") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            if (state.isLoading) {
                LoadingScreen()
            } else if (selectedClass != null) {
                // Summary Card
                AttendanceSummaryCard(
                    present = state.presentCount,
                    absent = state.absentCount,
                    percentage = state.attendancePercentage
                )

                // Records List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.records) { record ->
                        AttendanceRecordItem(record)
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Select a class to view report", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun AttendanceSummaryCard(present: Int, absent: Int, percentage: Float) {
    AppCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryItem("Present", present.toString(), Color(0xFF4CAF50))
            SummaryItem("Absent", absent.toString(), Color(0xFFF44336))
            SummaryItem("Rate", "${percentage.toInt()}%", MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
fun AttendanceRecordItem(record: AttendanceRecord) {
    AppCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Student ID: ${record.studentId.takeLast(8)}", fontWeight = FontWeight.Medium)
                Text(record.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Badge(
                containerColor = if (record.isPresent) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                contentColor = if (record.isPresent) Color(0xFF4CAF50) else Color(0xFFF44336)
            ) {
                Text(
                    if (record.isPresent) "PRESENT" else "ABSENT",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
