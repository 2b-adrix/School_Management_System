package com.example.schoolmanagementsystem.ui.fee

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
fun AddFeeStructureScreen(
    onNavigateBack: () -> Unit,
    viewModel: FeeViewModel = hiltViewModel()
) {
    var className by remember { mutableStateOf("") }
    var feeName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val saveState by viewModel.saveState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is FeeViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is FeeViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SchoolTopAppBar(
                title = "Add Fee Structure",
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
                value = className,
                onValueChange = { className = it },
                label = { Text("Class Name *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = feeName,
                onValueChange = { feeName = it },
                label = { Text("Fee Name *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                label = { Text("Due Date (YYYY-MM-DD) *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.addFeeStructure(className, feeName, amount, dueDate, description) },
                modifier = Modifier.fillMaxWidth(),
                enabled = saveState !is Resource.Loading
            ) {
                if (saveState is Resource.Loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Save Fee Structure")
                }
            }
        }
    }
}
