package com.example.schoolmanagementsystem.ui.exam

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.model.SchoolClass
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.ErrorScreen
import com.example.schoolmanagementsystem.ui.components.LoadingScreen
import com.example.schoolmanagementsystem.ui.schoolclass.ClassListViewModel
import com.example.schoolmanagementsystem.ui.theme.PremiumBlueGradient
import com.example.schoolmanagementsystem.ui.theme.glassmorphic

@Composable
fun ExamClassSelectScreen(
    onClassSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ClassListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFF0F172A), // Premium Deep Slate
        topBar = {
            Column(
                modifier = Modifier
                    .background(PremiumBlueGradient)
                    .statusBarsPadding()
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
                
                Text(
                    text = "Academic Assessment",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Text(
                    text = "Select a class to manage examinations",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val result = state) {
                is Resource.Loading -> LoadingScreen()
                is Resource.Success -> {
                    val classes = result.data ?: emptyList()
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "Available Classes",
                                color = Color(0xFFD4AF37), // Elite Gold
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(classes) { schoolClass ->
                            ClassExamItem(
                                schoolClass = schoolClass,
                                onClick = { onClassSelected(schoolClass.id) }
                            )
                        }
                    }
                }
                is Resource.Error -> ErrorScreen(
                    message = result.message ?: "Error",
                    onRetry = { viewModel.getClasses() }
                )
            }
        }
    }
}

@Composable
fun ClassExamItem(schoolClass: SchoolClass, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .glassmorphic(
                backgroundColor = Color.White.copy(alpha = 0.03f),
                borderColor = Color.White.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = schoolClass.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Section ${schoolClass.section}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.6f)
                    )
                )
            }
            
            Surface(
                color = Color(0xFFD4AF37).copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = schoolClass.name.take(1),
                        color = Color(0xFFD4AF37),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
