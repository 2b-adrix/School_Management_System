package com.example.schoolmanagementsystem.frontend.ui.assignment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.backend.domain.model.Assignment
import com.example.schoolmanagementsystem.backend.domain.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(
    onNavigateBack: () -> Unit,
    viewModel: AssignmentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFF090C0E),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Elite Assignments", 
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack, 
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter */ }) {
                        Icon(
                            Icons.Rounded.FilterList, 
                            contentDescription = "Filter",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF090C0E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {

            when (val assignmentsResource = state.assignments) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Error -> {
                    Text(
                        text = assignmentsResource.message ?: "Unknown error",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is Resource.Success -> {
                    val assignments = assignmentsResource.data ?: emptyList()
                    if (assignments.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No active assignments",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(assignments) { assignment: Assignment ->
                                AssignmentItemCard(assignment)
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun AssignmentItemCard(assignment: Assignment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111619)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A2127))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        assignment.title, 
                        fontWeight = FontWeight.Bold, 
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFB4C0FF).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = assignment.subject,
                                color = Color(0xFFB4C0FF),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        if (assignment.dueDate != null) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Due: ${assignment.dueDate}", 
                                color = Color.Gray, 
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                
                Surface(
                    color = Color(0xFFE91E63).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = assignment.submissionType.uppercase(), 
                        color = Color(0xFFE91E63), 
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (assignment.status != null) {
                    Surface(
                        color = if (assignment.status == "Submitted") Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(0xFFFFD54F).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = assignment.status ?: "",
                            color = if (assignment.status == "Submitted") Color(0xFF4CAF50) else Color(0xFFFFD54F),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Button(
                    onClick = { /* Submit logic */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB4C0FF)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        if (assignment.status == "Submitted") "View Submission" else "Submit Now",
                        color = Color(0xFF090C0E),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}


