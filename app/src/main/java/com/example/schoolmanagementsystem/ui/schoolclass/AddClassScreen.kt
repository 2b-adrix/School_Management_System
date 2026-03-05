package com.example.schoolmanagementsystem.ui.schoolclass

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.SchoolTopAppBar

@Composable
fun AddClassScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddClassViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("") }
    var roomNumber by remember { mutableStateOf("") }

    val saveState by viewModel.saveState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AddClassViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is AddClassViewModel.UiEvent.ShowSnackbar -> {
                    // Show snackbar
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SchoolTopAppBar(
                title = "Add Class",
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Class Name (e.g., Grade 10) *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = section,
                onValueChange = { section = it },
                label = { Text("Section (e.g., A) *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = roomNumber,
                onValueChange = { roomNumber = it },
                label = { Text("Room Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.saveClass(name, section, roomNumber) },
                modifier = Modifier.fillMaxWidth(),
                enabled = saveState !is Resource.Loading
            ) {
                if (saveState is Resource.Loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Save Class")
                }
            }
        }
    }
}
