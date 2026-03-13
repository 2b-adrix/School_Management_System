package com.example.schoolmanagementsystem.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.model.Student
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.*
import com.example.schoolmanagementsystem.ui.theme.spacing

@Composable
fun StudentDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: StudentDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is StudentDetailViewModel.UiEvent.DeleteSuccess -> onNavigateBack()
                is StudentDetailViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SchoolTopAppBar(
                title = "Student Profile",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { viewModel.downloadProfile(context) }) {
                        Icon(
                            imageVector = Icons.Rounded.Download,
                            contentDescription = "Download PDF",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { viewModel.deleteStudent() }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when (val result = state) {
                is Resource.Loading -> LoadingScreen()
                is Resource.Success -> {
                    result.data?.let { student ->
                        StudentDetailContent(student = student)
                    }
                }
                is Resource.Error -> ErrorScreen(
                    message = result.message ?: "Unknown error",
                    onRetry = { viewModel.loadStudent() }
                )
            }
        }
    }
}

@Composable
fun StudentDetailContent(student: Student) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing().spacing4),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Profile Section
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing().spacing4))

        Text(
            text = "${student.firstName} ${student.lastName}",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Roll No: ${student.rollNumber}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(spacing().spacing6))

        // Academic Info Section
        SectionTitle(title = "Academic Information")
        AppCard {
            Column(modifier = Modifier.padding(spacing().spacing4)) {
                DetailRow(icon = Icons.Rounded.Class, label = "Class", value = student.className)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DetailRow(icon = Icons.Rounded.Numbers, label = "Enrollment No", value = student.id.takeLast(8).uppercase())
            }
        }

        Spacer(modifier = Modifier.height(spacing().spacing4))

        // Personal Info Section
        SectionTitle(title = "Personal Details")
        AppCard {
            Column(modifier = Modifier.padding(spacing().spacing4)) {
                DetailRow(icon = Icons.Rounded.Cake, label = "Date of Birth", value = student.dateOfBirth)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DetailRow(icon = Icons.Rounded.LocationOn, label = "Address", value = student.address)
            }
        }

        Spacer(modifier = Modifier.height(spacing().spacing4))

        // Guardian Info Section
        SectionTitle(title = "Guardian Information")
        AppCard {
            Column(modifier = Modifier.padding(spacing().spacing4)) {
                DetailRow(icon = Icons.Rounded.FamilyRestroom, label = "Parent/Guardian", value = student.parentName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DetailRow(icon = Icons.Rounded.Phone, label = "Contact Number", value = student.parentContact)
            }
        }

        Spacer(modifier = Modifier.height(spacing().spacing8))
    }
}
