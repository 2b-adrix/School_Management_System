package com.example.schoolmanagementsystem.frontend.ui.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Class
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.backend.domain.model.SchoolClass
import com.example.schoolmanagementsystem.backend.domain.model.Subject
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import com.example.schoolmanagementsystem.frontend.ui.components.AppCard
import com.example.schoolmanagementsystem.frontend.ui.components.ErrorScreen
import com.example.schoolmanagementsystem.frontend.ui.components.LoadingScreen
import com.example.schoolmanagementsystem.frontend.ui.components.SchoolTopAppBar
import com.example.schoolmanagementsystem.frontend.ui.schoolclass.ClassListViewModel
import com.example.schoolmanagementsystem.frontend.ui.subject.SubjectListViewModel
import com.example.schoolmanagementsystem.frontend.ui.theme.spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AttendanceClassSelectScreen(
    onClassSelected: (String, String, String) -> Unit,
    onNavigateBack: () -> Unit,
    classViewModel: ClassListViewModel = hiltViewModel(),
    subjectViewModel: SubjectListViewModel = hiltViewModel()
) {
    val classState by classViewModel.state.collectAsState()
    val subjectState by subjectViewModel.state.collectAsState()
    
    var selectedClass by remember { mutableStateOf<SchoolClass?>(null) }
    val selectedDate = remember { LocalDate.now() }
    val formattedDate = remember { selectedDate.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SchoolTopAppBar(
                title = if (selectedClass == null) "Select Class" else "Select Subject",
                onBackClick = {
                    if (selectedClass != null) {
                        selectedClass = null
                    } else {
                        onNavigateBack()
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Info Header
            AppCard(
                modifier = Modifier.padding(spacing().spacing4),
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            ) {
                Column(modifier = Modifier.padding(spacing().spacing4)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = formattedDate, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    if (selectedClass != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Class, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "${selectedClass!!.name} (${selectedClass!!.section})", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (selectedClass == null) {
                    // Class Selection List
                    when (val result = classState) {
                        is Resource.Loading -> LoadingScreen()
                        is Resource.Success -> {
                            val classes = result.data ?: emptyList()
                            LazyColumn(
                                contentPadding = PaddingValues(spacing().spacing4),
                                verticalArrangement = Arrangement.spacedBy(spacing().spacing3)
                            ) {
                                items(classes) { schoolClass ->
                                    ClassSelectItem(schoolClass = schoolClass, onClick = { selectedClass = schoolClass })
                                }
                            }
                        }
                        is Resource.Error -> ErrorScreen(message = result.message ?: "Error", onRetry = { classViewModel.getClasses() })
                    }
                } else {
                    // Subject Selection List for the selected class
                    when (val result = subjectState) {
                        is Resource.Loading -> LoadingScreen()
                        is Resource.Success -> {
                            val subjects = (result.data ?: emptyList()).filter { it.classId == selectedClass!!.id }
                            if (subjects.isEmpty()) {
                                EmptyPlaceholder("No subjects found for this class", Icons.Rounded.MenuBook)
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(spacing().spacing4),
                                    verticalArrangement = Arrangement.spacedBy(spacing().spacing3)
                                ) {
                                    items(subjects) { subject ->
                                        SubjectSelectItem(
                                            subject = subject,
                                            onClick = { onClassSelected(selectedClass!!.id, subject.id, selectedDate.toString()) }
                                        )
                                    }
                                }
                            }
                        }
                        is Resource.Error -> ErrorScreen(message = result.message ?: "Error", onRetry = { subjectViewModel.getSubjects() })
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectSelectItem(subject: Subject, onClick: () -> Unit) {
    AppCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(spacing().spacing4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.width(spacing().spacing4))
            Column {
                Text(text = subject.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Code: ${subject.code}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ClassSelectItem(schoolClass: SchoolClass, onClick: () -> Unit) {
    AppCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(spacing().spacing4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Class, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                }
            }
            Spacer(modifier = Modifier.width(spacing().spacing4))
            Column {
                Text(text = schoolClass.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Section: ${schoolClass.section}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun EmptyPlaceholder(message: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

