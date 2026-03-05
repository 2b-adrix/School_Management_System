package com.example.schoolmanagementsystem.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.model.Teacher
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.*
import com.example.schoolmanagementsystem.ui.theme.spacing

@Composable
fun TeacherDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: TeacherDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is TeacherDetailViewModel.UiEvent.ShowSnackbar -> {
                    // Show snackbar
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SchoolTopAppBar(
                title = "Teacher Profile",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when (val result = state) {
                is Resource.Loading -> LoadingScreen()
                is Resource.Success -> {
                    result.data?.let { teacher ->
                        TeacherDetailContent(teacher = teacher)
                    }
                }
                is Resource.Error -> ErrorScreen(
                    message = result.message ?: "Unknown error",
                    onRetry = { viewModel.loadTeacher() }
                )
            }
        }
    }
}

@Composable
fun TeacherDetailContent(teacher: Teacher) {
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
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.School,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing().spacing4))

        Text(
            text = "${teacher.firstName} ${teacher.lastName}",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = teacher.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(spacing().spacing6))

        // Professional Info Section
        SectionTitle(title = "Professional Information")
        AppCard {
            Column(modifier = Modifier.padding(spacing().spacing4)) {
                DetailRow(icon = Icons.AutoMirrored.Rounded.MenuBook, label = "Subjects", value = teacher.subjects.joinToString())
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DetailRow(icon = Icons.Rounded.Class, label = "Assigned Classes", value = teacher.assignedClasses.joinToString())
            }
        }

        Spacer(modifier = Modifier.height(spacing().spacing4))

        // Contact Info Section
        SectionTitle(title = "Contact Details")
        AppCard {
            Column(modifier = Modifier.padding(spacing().spacing4)) {
                DetailRow(icon = Icons.Rounded.Phone, label = "Phone Number", value = teacher.phoneNumber)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DetailRow(icon = Icons.Rounded.Email, label = "Email Address", value = teacher.email)
            }
        }

        Spacer(modifier = Modifier.height(spacing().spacing8))
    }
}
