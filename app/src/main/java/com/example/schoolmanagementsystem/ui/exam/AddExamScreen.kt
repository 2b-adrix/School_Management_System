package com.example.schoolmanagementsystem.ui.exam

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.SchoolTopAppBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddExamScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddExamViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var subjectId by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var totalMarks by remember { mutableStateOf("100") }

    val snackbarHostState = remember { SnackbarHostState() }
    val saveState by viewModel.saveState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddExamViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is AddExamViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SchoolTopAppBar(
                title = "Add Exam",
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Exam Title (e.g., Mid-Term) *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = subjectId,
                onValueChange = { subjectId = it },
                label = { Text("Subject ID *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (YYYY-MM-DD) *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = totalMarks,
                onValueChange = { totalMarks = it },
                label = { Text("Total Marks *") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.saveExam(title, subjectId, date, totalMarks) },
                modifier = Modifier.fillMaxWidth(),
                enabled = saveState !is Resource.Loading
            ) {
                if (saveState is Resource.Loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Save Exam")
                }
            }
        }
    }
}
