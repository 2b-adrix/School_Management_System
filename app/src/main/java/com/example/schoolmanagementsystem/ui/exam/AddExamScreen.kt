package com.example.schoolmanagementsystem.ui.exam

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.model.Subject
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.SchoolTopAppBar
import com.example.schoolmanagementsystem.ui.subject.SubjectListViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExamScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddExamViewModel = hiltViewModel(),
    subjectViewModel: SubjectListViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var date by remember { mutableStateOf("") }
    var totalMarks by remember { mutableStateOf("100") }
    
    var isSubjectDropdownExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val saveState by viewModel.saveState.collectAsState()
    val subjectState by subjectViewModel.state.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddExamViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is AddExamViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        date = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SchoolTopAppBar(
                title = "Create Exam",
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
                "Exam Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            ExamTextField(
                value = title,
                onValueChange = { title = it },
                label = "Exam Title",
                placeholder = "e.g. First Terminal Exam",
                icon = Icons.AutoMirrored.Rounded.Assignment
            )

            // Subject Dropdown
            ExposedDropdownMenuBox(
                expanded = isSubjectDropdownExpanded,
                onExpandedChange = { isSubjectDropdownExpanded = !isSubjectDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedSubject?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Subject *") },
                    placeholder = { Text("Choose Subject") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Rounded.MenuBook, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSubjectDropdownExpanded) },
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
                    expanded = isSubjectDropdownExpanded,
                    onDismissRequest = { isSubjectDropdownExpanded = false }
                ) {
                    when (val result = subjectState) {
                        is Resource.Success -> {
                            result.data?.forEach { subject ->
                                DropdownMenuItem(
                                    text = { Text(subject.name) },
                                    onClick = {
                                        selectedSubject = subject
                                        isSubjectDropdownExpanded = false
                                    }
                                )
                            }
                        }
                        is Resource.Loading -> {
                            DropdownMenuItem(text = { Text("Loading subjects...") }, onClick = {})
                        }
                        is Resource.Error -> {
                            DropdownMenuItem(text = { Text("Error loading subjects") }, onClick = {})
                        }
                    }
                }
            }

            // Date Picker Field
            OutlinedTextField(
                value = date,
                onValueChange = {},
                readOnly = true,
                label = { Text("Exam Date *") },
                placeholder = { Text("YYYY-MM-DD") },
                leadingIcon = { Icon(Icons.Rounded.CalendarToday, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Rounded.EditCalendar, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            ExamTextField(
                value = totalMarks,
                onValueChange = { totalMarks = it },
                label = "Total Marks",
                placeholder = "e.g. 100",
                icon = Icons.Rounded.Score
            )

            ExamTextField(
                value = description,
                onValueChange = { description = it },
                label = "Instructions / Description",
                placeholder = "Optional instructions for students...",
                icon = Icons.Rounded.Description,
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    selectedSubject?.let { subject ->
                        viewModel.saveExam(title, description, subject.id, date, totalMarks)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = saveState !is Resource.Loading && title.isNotBlank() && selectedSubject != null && date.isNotBlank(),
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
                        "Schedule Exam",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun ExamTextField(
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
        label = { Text(label + if (label.contains("Optional") || label.contains("Instructions")) "" else " *") },
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
