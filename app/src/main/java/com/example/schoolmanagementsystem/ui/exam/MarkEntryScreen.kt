package com.example.schoolmanagementsystem.ui.exam

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.ui.components.LoadingScreen
import com.example.schoolmanagementsystem.ui.components.SchoolTopAppBar
import androidx.compose.foundation.text.KeyboardOptions
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MarkEntryScreen(
    onNavigateBack: () -> Unit,
    viewModel: MarkEntryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is MarkEntryViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is MarkEntryViewModel.UiEvent.ShowSnackbar -> {
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
                title = "Enter Marks",
                onBackClick = onNavigateBack,
                actions = {
                    TextButton(onClick = { viewModel.saveMarks() }) {
                        Text("Save", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                LoadingScreen()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.students) { student ->
                        MarkRow(
                            studentName = "${student.firstName} ${student.lastName}",
                            marks = state.marks[student.id] ?: "",
                            onMarksChanged = { viewModel.onMarkChanged(student.id, it) }
                        )
                    }
                }
            }
            
            if (state.isSaving) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun MarkRow(studentName: String, marks: String, onMarksChanged: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = studentName, modifier = Modifier.weight(1f))
            OutlinedTextField(
                value = marks,
                onValueChange = onMarksChanged,
                label = { Text("Marks") },
                modifier = Modifier.width(100.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}
