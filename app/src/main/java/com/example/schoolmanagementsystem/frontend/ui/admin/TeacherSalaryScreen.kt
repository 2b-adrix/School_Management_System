package com.example.schoolmanagementsystem.frontend.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.backend.domain.model.SalaryRecord
import com.example.schoolmanagementsystem.backend.domain.model.Teacher
import com.example.schoolmanagementsystem.frontend.ui.components.AppCard
import com.example.schoolmanagementsystem.frontend.ui.components.LoadingScreen
import com.example.schoolmanagementsystem.frontend.ui.components.SchoolTopAppBar
import com.example.schoolmanagementsystem.frontend.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TeacherSalaryScreen(
    onNavigateBack: () -> Unit,
    viewModel: TeacherSalaryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is TeacherSalaryViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SchoolTopAppBar(
                title = "Teacher Salary",
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Salary Record")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                LoadingScreen()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.salaries) { record ->
                        SalaryRecordItem(
                            record = record,
                            onStatusUpdate = { newStatus -> 
                                viewModel.updateStatus(record.id, newStatus)
                            }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddSalaryDialog(
                teachers = state.teachers,
                onDismiss = { showAddDialog = false },
                onConfirm = { teacherId, teacherName, amount, month ->
                    viewModel.addSalaryRecord(teacherId, teacherName, amount, month)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun SalaryRecordItem(record: SalaryRecord, onStatusUpdate: (String) -> Unit) {
    AppCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(record.teacherName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(record.month, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text("Amount: ₹ ${record.amount}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            
            val isPaid = record.status == "PAID"
            IconButton(onClick = { onStatusUpdate(if (isPaid) "PENDING" else "PAID") }) {
                Icon(
                    imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.Pending,
                    contentDescription = record.status,
                    tint = if (isPaid) Color(0xFF4CAF50) else Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
fun AddSalaryDialog(
    teachers: List<Teacher>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, String) -> Unit
) {
    var selectedTeacher by remember { mutableStateOf<Teacher?>(null) }
    var amount by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("October 2025") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Salary Record") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedTeacher?.let { "${it.firstName} ${it.lastName}" } ?: "Select Teacher")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        teachers.forEach { teacher ->
                            DropdownMenuItem(
                                text = { Text("${teacher.firstName} ${teacher.lastName}") },
                                onClick = {
                                    selectedTeacher = teacher
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = month,
                    onValueChange = { month = it },
                    label = { Text("Month (e.g. October 2025)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedTeacher?.let { 
                        onConfirm(it.id, "${it.firstName} ${it.lastName}", amount.toDoubleOrNull() ?: 0.0, month)
                    }
                },
                enabled = selectedTeacher != null && amount.isNotEmpty()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

