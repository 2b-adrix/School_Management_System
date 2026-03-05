package com.example.schoolmanagementsystem.ui.subject

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.SchoolTopAppBar

@Composable
fun AddSubjectScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddSubjectViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var classId by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val saveState by viewModel.saveState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AddSubjectViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is AddSubjectViewModel.UiEvent.ShowSnackbar -> {
                    // Show snackbar
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SchoolTopAppBar(
                title = "Add Subject",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Subject Name (e.g., Mathematics) *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Subject Code (e.g., MATH101) *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = classId,
                onValueChange = { classId = it },
                label = { Text("Class ID/Name *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = { 
                    viewModel.saveSubject(
                        name = name, 
                        code = code, 
                        classId = classId, 
                        description = description
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = saveState !is Resource.Loading && name.isNotBlank() && code.isNotBlank() && classId.isNotBlank()
            ) {
                if (saveState is Resource.Loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Save Subject")
                }
            }
        }
    }
}
