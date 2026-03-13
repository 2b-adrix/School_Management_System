package com.example.schoolmanagementsystem.ui.exam

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.model.Exam
import com.example.schoolmanagementsystem.domain.model.UserRole
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.AppCard
import com.example.schoolmanagementsystem.ui.components.ErrorScreen
import com.example.schoolmanagementsystem.ui.components.LoadingScreen
import com.example.schoolmanagementsystem.ui.components.SchoolTopAppBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ExamListScreen(
    classId: String,
    onAddExamClick: (String) -> Unit,
    onMarkEntryClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ExamListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ExamListViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SchoolTopAppBar(
                title = "Exams",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            // Only show FAB for Admin/Teacher
            // Note: We need user info in state to check role properly here
            // For now, let's assume we show it if classId is provided from Admin Portal
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val result = state) {
                is Resource.Loading -> LoadingScreen()
                is Resource.Success -> {
                    val exams = result.data ?: emptyList()
                    if (exams.isEmpty()) {
                        EmptyExamsPlaceholder()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(exams) { exam ->
                                ExamItem(
                                    exam = exam, 
                                    onActionClick = { 
                                        // If student, download. If teacher, enter marks.
                                        // This logic will be improved once role is in state
                                        onMarkEntryClick(exam.id)
                                    },
                                    onDownloadClick = {
                                        viewModel.downloadReportCard(context, exam.id)
                                    },
                                    onDelete = { viewModel.deleteExam(exam) }
                                )
                            }
                        }
                    }
                }
                is Resource.Error -> ErrorScreen(
                    message = result.message ?: "Error",
                    onRetry = { viewModel.getExams() }
                )
            }
        }
    }
}

@Composable
fun ExamItem(
    exam: Exam, 
    onActionClick: () -> Unit, 
    onDownloadClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Exam") },
            text = { Text("Are you sure you want to delete ${exam.title}?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteConfirm = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    AppCard(onClick = onActionClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exam.title, 
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Date: ${exam.date}", 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total Marks: ${exam.totalMarks}", 
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Row {
                IconButton(onClick = onDownloadClick) {
                    Icon(
                        imageVector = Icons.Rounded.Download,
                        contentDescription = "Download Report Card",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyExamsPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "No exams scheduled for this class",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
