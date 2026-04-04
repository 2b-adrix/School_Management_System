package com.example.schoolmanagementsystem.frontend.ui.subject

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.backend.domain.model.SchoolClass
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import com.example.schoolmanagementsystem.frontend.ui.components.SchoolTopAppBar
import com.example.schoolmanagementsystem.frontend.ui.schoolclass.ClassListViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubjectScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddSubjectViewModel = hiltViewModel(),
    classListViewModel: ClassListViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf<SchoolClass?>(null) }
    var description by remember { mutableStateOf("") }
    var isClassDropdownExpanded by remember { mutableStateOf(false) }

    val saveState by viewModel.saveState.collectAsState()
    val classState by classListViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddSubjectViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is AddSubjectViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SchoolTopAppBar(
                title = "Create Subject",
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
                "Subject Information",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            SubjectTextField(
                value = name,
                onValueChange = { name = it },
                label = "Subject Name",
                placeholder = "e.g. Mathematics",
                icon = Icons.Rounded.Book
            )

            SubjectTextField(
                value = code,
                onValueChange = { code = it },
                label = "Subject Code",
                placeholder = "e.g. MATH-101",
                icon = Icons.Rounded.Code
            )

            // Class Dropdown
            ExposedDropdownMenuBox(
                expanded = isClassDropdownExpanded,
                onExpandedChange = { isClassDropdownExpanded = !isClassDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedClass?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Assign to Class *") },
                    placeholder = { Text("Select Class") },
                    leadingIcon = { Icon(Icons.Rounded.Class, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isClassDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
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
                        is Resource.Loading -> {
                            DropdownMenuItem(text = { Text("Loading classes...") }, onClick = {})
                        }
                        is Resource.Error -> {
                            DropdownMenuItem(text = { Text("Error loading classes") }, onClick = {})
                        }
                    }
                }
            }

            SubjectTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description (Optional)",
                placeholder = "Describe the subject curriculum...",
                icon = Icons.Rounded.Description,
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { 
                    selectedClass?.let { 
                        viewModel.saveSubject(name, code, it.id, description)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = saveState !is Resource.Loading && name.isNotBlank() && code.isNotBlank() && selectedClass != null,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (saveState is Resource.Loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Create Subject",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label + if (label.contains("Optional")) "" else " *") },
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    )
}

