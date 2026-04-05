package com.example.schoolmanagementsystem.frontend.ui.subject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.backend.domain.model.Subject
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import com.example.schoolmanagementsystem.frontend.ui.components.ErrorScreen
import com.example.schoolmanagementsystem.frontend.ui.components.LoadingScreen

@Composable
fun SubjectListScreen(
    onAddSubjectClick: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SubjectListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFF090C0E),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(48.dp).background(Color(0xFF111619), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Elite Modules",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    "Academic Curriculum & Resources",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSubjectClick,
                containerColor = Color(0xFF4FC3F7),
                contentColor = Color(0xFF090C0E),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Subject")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val result = state) {
                is Resource.Loading -> LoadingScreen()
                is Resource.Success -> {
                    val subjects = result.data ?: emptyList()
                    if (subjects.isEmpty()) {
                        EmptySubjectsState()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(subjects) { subject ->
                                SubjectListItem(
                                    subject = subject,
                                    onDelete = { viewModel.deleteSubject(subject) }
                                )
                            }
                        }
                    }
                }
                is Resource.Error -> ErrorScreen(
                    message = result.message ?: "An error occurred",
                    onRetry = { viewModel.getSubjects() }
                )
            }
        }
    }
}

@Composable
fun SubjectListItem(subject: Subject, onDelete: () -> Unit) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = Color(0xFF111619),
            titleContentColor = Color.White,
            textContentColor = Color.Gray,
            title = { Text("Unregister Module") },
            text = { Text("Are you sure you want to remove ${subject.name} from the elite curriculum?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteConfirm = false
                }) {
                    Text("Remove", color = Color(0xFFE91E63))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { /* Navigate to subject details if needed */ }),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111619)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Background Gradient Accent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF9C27B0).copy(alpha = 0.12f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF9C27B0).copy(alpha = 0.2f)
                    ) {
                        Icon(
                            Icons.Rounded.AutoStories,
                            contentDescription = null,
                            tint = Color(0xFFE1BEE7),
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Surface(
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Text(
                                text = "CODE: ${subject.code}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9C27B0).copy(alpha = 0.8f),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier
                            .background(Color.Red.copy(alpha = 0.1f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Bottom Info Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.02f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.AccountBalance,
                            contentDescription = null,
                            tint = Color(0xFF9C27B0).copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Elite Academic Faculty",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                    
                    Icon(
                        Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptySubjectsState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No elite modules registered", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
    }
}

