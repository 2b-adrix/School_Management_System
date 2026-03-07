package com.example.schoolmanagementsystem.ui.schoolclass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.model.SchoolClass
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.ErrorScreen
import com.example.schoolmanagementsystem.ui.components.LoadingScreen
import com.example.schoolmanagementsystem.ui.components.SchoolTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassListScreen(
    onAddClassClick: () -> Unit,
    onClassClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ClassListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            SchoolTopAppBar(
                title = "Classes",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClassClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Class")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val result = state) {
                is Resource.Loading -> LoadingScreen()
                is Resource.Success -> {
                    val classes = result.data ?: emptyList()
                    if (classes.isEmpty()) {
                        Text(
                            text = "No classes found",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(classes) { schoolClass ->
                                ClassItem(
                                    schoolClass = schoolClass,
                                    onClick = { onClassClick(schoolClass.id) }
                                )
                            }
                        }
                    }
                }
                is Resource.Error -> ErrorScreen(
                    message = result.message ?: "An error occurred",
                    onRetry = { viewModel.getClasses() }
                )
            }
        }
    }
}

@Composable
fun ClassItem(
    schoolClass: SchoolClass,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Class: ${schoolClass.name}", style = MaterialTheme.typography.titleLarge)
            Text(text = "Section: ${schoolClass.section}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Room: ${schoolClass.roomNumber}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
