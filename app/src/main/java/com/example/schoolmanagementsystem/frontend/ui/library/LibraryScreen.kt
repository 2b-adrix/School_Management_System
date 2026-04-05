package com.example.schoolmanagementsystem.frontend.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
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
import com.example.schoolmanagementsystem.frontend.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onNavigate: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFF090C0E),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Elite Library", 
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack, 
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF090C0E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {



            item {
                LibraryActionCard(
                    title = "Search Books",
                    onClick = { onNavigate(Screen.LibrarySearch.route) }
                )
            }

            item {
                LibraryActionCard(
                    title = "Borrowed Books",
                    count = state.borrowedCount,
                    onClick = { /* Navigate to Borrowed */ }
                )
            }

            item {
                LibraryActionCard(
                    title = "Returned Books",
                    count = state.returnedCount,
                    onClick = { /* Navigate to Returned */ }
                )
            }
        }
    }
}

@Composable
fun LibraryActionCard(
    title: String,
    count: Int? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111619)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A2127))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            if (count != null) {
                Surface(
                    color = Color(0xFFB4C0FF).copy(alpha = 0.15f),
                    shape = CircleShape
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFB4C0FF),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight, 
                contentDescription = null, 
                tint = Color.Gray
            )
        }
    }
}


