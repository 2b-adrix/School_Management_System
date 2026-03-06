package com.example.schoolmanagementsystem.ui.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.ui.components.AppCard
import com.example.schoolmanagementsystem.ui.components.LoadingScreen
import com.example.schoolmanagementsystem.ui.components.SchoolTopAppBar
import com.example.schoolmanagementsystem.ui.theme.spacing

@Composable
fun AttendanceMarkScreen(
    onNavigateBack: () -> Unit,
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AttendanceViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is AttendanceViewModel.UiEvent.ShowSnackbar -> {
                    // Show snackbar
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SchoolTopAppBar(
                title = "Mark Attendance",
                onBackClick = onNavigateBack,
                actions = {
                    Button(
                        onClick = { viewModel.saveAttendance() },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Save", style = MaterialTheme.typography.labelLarge)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            if (state.isLoading) {
                LoadingScreen()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Summary Header
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(spacing().spacing4)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            val presentCount = state.attendanceRecords.values.count { it.isPresent }
                            val totalCount = state.students.size
                            AttendanceSummaryItem("Total", totalCount.toString(), MaterialTheme.colorScheme.primary)
                            AttendanceSummaryItem("Present", presentCount.toString(), Color(0xFF4CAF50))
                            AttendanceSummaryItem("Absent", (totalCount - presentCount).toString(), MaterialTheme.colorScheme.error)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(spacing().spacing4),
                        verticalArrangement = Arrangement.spacedBy(spacing().spacing3)
                    ) {
                        items(state.students) { student ->
                            val record = state.attendanceRecords[student.id]
                            AttendanceRow(
                                studentName = "${student.firstName} ${student.lastName}",
                                rollNumber = student.rollNumber,
                                isPresent = record?.isPresent ?: false,
                                onToggle = { viewModel.onAttendanceChanged(student.id, it) }
                            )
                        }
                    }
                }
            }
            
            if (state.isSaving) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceSummaryItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun AttendanceRow(studentName: String, rollNumber: String, isPresent: Boolean, onToggle: (Boolean) -> Unit) {
    AppCard {
        Row(
            modifier = Modifier
                .padding(spacing().spacing4)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = if (isPresent) Color(0xFF4CAF50).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPresent) Icons.Rounded.CheckCircle else Icons.Rounded.Person,
                        contentDescription = null,
                        tint = if (isPresent) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(spacing().spacing4))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = studentName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Roll No: $rollNumber",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = isPresent,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4CAF50),
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}
