package com.example.schoolmanagementsystem.ui.timetable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.model.Subject
import com.example.schoolmanagementsystem.domain.model.Teacher
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.SchoolTopAppBar
import com.example.schoolmanagementsystem.ui.subject.SubjectListViewModel
import com.example.schoolmanagementsystem.ui.teacher.TeacherListViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTimetableEntryScreen(
    classId: String,
    onNavigateBack: () -> Unit,
    viewModel: AddTimetableEntryViewModel = hiltViewModel(),
    subjectViewModel: SubjectListViewModel = hiltViewModel(),
    teacherViewModel: TeacherListViewModel = hiltViewModel()
) {
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var selectedTeacher by remember { mutableStateOf<Teacher?>(null) }
    var selectedDay by remember { mutableStateOf(0) }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var roomNumber by remember { mutableStateOf("") }

    var isSubjectDropdownExpanded by remember { mutableStateOf(false) }
    var isTeacherDropdownExpanded by remember { mutableStateOf(false) }
    var isDayDropdownExpanded by remember { mutableStateOf(false) }

    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val saveState by viewModel.saveState.collectAsState()
    val subjectState by subjectViewModel.state.collectAsState()
    val teacherState by teacherViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddTimetableEntryViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is AddTimetableEntryViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SchoolTopAppBar(
                title = "Add Timetable Entry",
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
                "Entry Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            // Day Dropdown
            ExposedDropdownMenuBox(
                expanded = isDayDropdownExpanded,
                onExpandedChange = { isDayDropdownExpanded = !isDayDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = days[selectedDay],
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Day of Week *") },
                    leadingIcon = { Icon(Icons.Rounded.Today, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDayDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = isDayDropdownExpanded,
                    onDismissRequest = { isDayDropdownExpanded = false }
                ) {
                    days.forEachIndexed { index, day ->
                        DropdownMenuItem(
                            text = { Text(day) },
                            onClick = {
                                selectedDay = index
                                isDayDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Subject Dropdown
            ExposedDropdownMenuBox(
                expanded = isSubjectDropdownExpanded,
                onExpandedChange = { isSubjectDropdownExpanded = !isSubjectDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedSubject?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Subject *") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Rounded.MenuBook, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSubjectDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = isSubjectDropdownExpanded,
                    onDismissRequest = { isSubjectDropdownExpanded = false }
                ) {
                    if (subjectState is Resource.Success) {
                        (subjectState as Resource.Success<List<Subject>>).data?.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text(subject.name) },
                                onClick = {
                                    selectedSubject = subject
                                    isSubjectDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Teacher Dropdown
            ExposedDropdownMenuBox(
                expanded = isTeacherDropdownExpanded,
                onExpandedChange = { isTeacherDropdownExpanded = !isTeacherDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedTeacher?.firstName?.let { "$it ${selectedTeacher?.lastName}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Teacher *") },
                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTeacherDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = isTeacherDropdownExpanded,
                    onDismissRequest = { isTeacherDropdownExpanded = false }
                ) {
                    if (teacherState is Resource.Success) {
                        (teacherState as Resource.Success<List<Teacher>>).data?.forEach { teacher ->
                            DropdownMenuItem(
                                text = { Text("${teacher.firstName} ${teacher.lastName}") },
                                onClick = {
                                    selectedTeacher = teacher
                                    isTeacherDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Start Time *") },
                    placeholder = { Text("09:00 AM") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("End Time *") },
                    placeholder = { Text("10:00 AM") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value = roomNumber,
                onValueChange = { roomNumber = it },
                label = { Text("Room Number") },
                placeholder = { Text("e.g. 101") },
                leadingIcon = { Icon(Icons.Rounded.MeetingRoom, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.saveTimetableEntry(
                        classId = classId,
                        subjectId = selectedSubject?.id ?: "",
                        teacherId = selectedTeacher?.id ?: "",
                        dayOfWeek = selectedDay,
                        startTime = startTime,
                        endTime = endTime,
                        roomNumber = roomNumber.ifBlank { null }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = saveState !is Resource.Loading && selectedSubject != null && selectedTeacher != null && startTime.isNotBlank() && endTime.isNotBlank()
            ) {
                if (saveState is Resource.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Add to Timetable", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
