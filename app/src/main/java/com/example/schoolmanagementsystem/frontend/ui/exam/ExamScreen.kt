package com.example.schoolmanagementsystem.frontend.ui.exam

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.backend.domain.model.Exam
import com.example.schoolmanagementsystem.backend.domain.model.Subject
import com.example.schoolmanagementsystem.backend.domain.model.UserRole
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import com.example.schoolmanagementsystem.frontend.ui.components.ErrorScreen
import com.example.schoolmanagementsystem.frontend.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    onNavigateBack: () -> Unit,
    onAddExamClick: (String) -> Unit,
    onMarkEntryClick: (String) -> Unit,
    viewModel: ExamViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = { Text("Exams", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (state.user?.role == UserRole.SCHOOL_ADMIN || state.user?.role == UserRole.TEACHER) {
                        IconButton(onClick = { /* Filter */ }) {
                            Icon(Icons.Rounded.FilterList, contentDescription = "Filter", tint = Color.White)
                        }
                        IconButton(onClick = {
                            // Navigation logic for adding exam
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Exam", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val result = state.exams) {
                is Resource.Loading -> LoadingScreen()
                is Resource.Success -> {
                    val exams = result.data ?: emptyList()
                    if (exams.isEmpty()) {
                        EmptyExamState()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(exams, key = { it.id }) { exam ->
                                ExamItemCard(
                                    exam = exam,
                                    subjects = state.subjects.data ?: emptyList(),
                                    onExamClick = { examId -> onMarkEntryClick(examId) },
                                    onDeleteClick = { viewModel.deleteExam(it) },
                                    isTeacherOrAdmin = state.user?.role == UserRole.TEACHER || state.user?.role == UserRole.SCHOOL_ADMIN
                                )
                            }
                        }
                    }
                }
                is Resource.Error -> ErrorScreen(
                    message = result.message ?: "An error occurred",
                    onRetry = { viewModel.getExamsByClass("") } // Adjust with actual classId if available
                )
            }
        }
    }
}

@Composable
fun ExamItemCard(
    exam: Exam,
    subjects: List<Subject>,
    onExamClick: (String) -> Unit,
    onDeleteClick: (Exam) -> Unit,
    isTeacherOrAdmin: Boolean
) {
    val subjectName = subjects.find { it.id == exam.subjectId }?.name ?: "Unknown Subject"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isTeacherOrAdmin) onExamClick(exam.id)
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(exam.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Subject: $subjectName", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Date: ${exam.date}", color = Color.Gray, fontSize = 13.sp)
                }

                if (isTeacherOrAdmin) {
                    IconButton(onClick = { onDeleteClick(exam) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Exam", tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyExamState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.School, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No exams scheduled", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

