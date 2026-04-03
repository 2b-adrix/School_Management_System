package com.example.schoolmanagementsystem.ui.notification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.model.SchoolClass
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.SchoolTopAppBar
import com.example.schoolmanagementsystem.ui.schoolclass.ClassListViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnnouncementScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddAnnouncementViewModel = hiltViewModel(),
    classListViewModel: ClassListViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var targetRole by remember { mutableStateOf("ALL") }
    var selectedClass by remember { mutableStateOf<SchoolClass?>(null) }
    var isClassDropdownExpanded by remember { mutableStateOf(false) }

    val saveState by viewModel.saveState.collectAsState()
    val classState by classListViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddAnnouncementViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is AddAnnouncementViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SchoolTopAppBar(
                title = "Create Announcement",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Announcement Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title *") },
                placeholder = { Text("e.g. School Holiday Notice") },
                leadingIcon = { Icon(Icons.Rounded.Title, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content *") },
                placeholder = { Text("Describe the announcement...") },
                leadingIcon = { Icon(Icons.Rounded.Description, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                "Target Audience",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = targetRole == "ALL",
                    onClick = { targetRole = "ALL"; selectedClass = null },
                    label = { Text("Everyone") }
                )
                FilterChip(
                    selected = targetRole == "STUDENT",
                    onClick = { targetRole = "STUDENT"; selectedClass = null },
                    label = { Text("All Students") }
                )
                FilterChip(
                    selected = targetRole == "CLASS",
                    onClick = { targetRole = "CLASS" },
                    label = { Text("Specific Class") }
                )
            }

            if (targetRole == "CLASS") {
                ExposedDropdownMenuBox(
                    expanded = isClassDropdownExpanded,
                    onExpandedChange = { isClassDropdownExpanded = !isClassDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedClass?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Class *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isClassDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = isClassDropdownExpanded,
                        onDismissRequest = { isClassDropdownExpanded = false }
                    ) {
                        when (val result = classState) {
                            is Resource.Success -> {
                                result.data?.forEach { schoolClass ->
                                    DropdownMenuItem(
                                        text = { Text(schoolClass.name) },
                                        onClick = {
                                            selectedClass = schoolClass
                                            isClassDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                            else -> { /* Loading/Error handles */ }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { 
                    viewModel.saveAnnouncement(title, content, targetRole, selectedClass?.id)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = saveState !is Resource.Loading && title.isNotBlank() && content.isNotBlank() && (targetRole != "CLASS" || selectedClass != null)
            ) {
                if (saveState is Resource.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Post Announcement", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
