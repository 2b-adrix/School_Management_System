package com.example.schoolmanagementsystem.ui.timetable

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
import com.example.schoolmanagementsystem.domain.model.TimetableEntry
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.ErrorScreen
import com.example.schoolmanagementsystem.ui.components.LoadingScreen
import com.example.schoolmanagementsystem.ui.components.SchoolTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableListScreen(
    classId: String,
    onAddEntryClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TimetableListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            SchoolTopAppBar(
                title = "Class Timetable",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddEntryClick(classId) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val result = state) {
                is Resource.Loading -> LoadingScreen()
                is Resource.Success -> {
                    val entries = result.data ?: emptyList()
                    if (entries.isEmpty()) {
                        Text(
                            text = "No timetable entries for this class",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val groupedEntries = entries.groupBy { it.dayOfWeek }
                            val days = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY")
                            
                            days.forEach { day ->
                                groupedEntries[day]?.let { dayEntries ->
                                    item {
                                        Text(
                                            text = day,
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                    items(dayEntries.sortedBy { it.startTime }) { entry ->
                                        TimetableItem(entry = entry)
                                    }
                                }
                            }
                        }
                    }
                }
                is Resource.Error -> ErrorScreen(
                    message = result.message ?: "Error",
                    onRetry = { viewModel.getTimetable() }
                )
            }
        }
    }
}

@Composable
fun TimetableItem(entry: TimetableEntry) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Subject ID: ${entry.subjectId}", style = MaterialTheme.typography.titleSmall)
                Text(text = "Teacher ID: ${entry.teacherId}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "${entry.startTime} - ${entry.endTime}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
