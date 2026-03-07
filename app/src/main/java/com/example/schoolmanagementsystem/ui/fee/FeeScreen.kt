package com.example.schoolmanagementsystem.ui.fee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.model.FeeStructure
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.AppCard
import com.example.schoolmanagementsystem.ui.components.ErrorScreen
import com.example.schoolmanagementsystem.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeScreen(
    onNavigateBack: () -> Unit,
    onAddFeeClick: () -> Unit,
    viewModel: FeeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            Column(modifier = Modifier.background(primaryColor)) {
                TopAppBar(
                    title = { Text("Fees", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
                )
                TabRow(
                    selectedTabIndex = state.selectedTab,
                    containerColor = primaryColor,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                            color = Color.White
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = state.selectedTab == 0,
                        onClick = { viewModel.onTabSelected(0) },
                        text = { Text("FEES DUE", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = state.selectedTab == 1,
                        onClick = { viewModel.onTabSelected(1) },
                        text = { Text("PAID FEES", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = state.selectedTab == 2,
                        onClick = { viewModel.onTabSelected(2) },
                        text = { Text("STRUCTURES", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (state.selectedTab == 2) {
                FloatingActionButton(
                    onClick = onAddFeeClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Fee Structure")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state.selectedTab) {
                0 -> {
                    // Fees Due Section
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Fees Due", color = Color.Gray, fontSize = 14.sp)
                        Text(
                            state.totalDue, 
                            color = Color(0xFFD32F2F), 
                            fontSize = 24.sp, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.dueFees) { fee ->
                            FeeItemCard(fee)
                        }
                    }
                }
                1 -> {
                    // Paid Fees Section
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.paidFees) { fee ->
                            FeeItemCard(fee)
                        }
                    }
                }
                2 -> {
                    // Fee Structures Section
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (val result = state.feeStructures) {
                            is Resource.Loading -> LoadingScreen()
                            is Resource.Success -> {
                                val structures = result.data ?: emptyList()
                                if (structures.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("No fee structures found", color = Color.Gray)
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(structures) { structure ->
                                            FeeStructureItem(
                                                structure = structure,
                                                onDelete = { viewModel.deleteFeeStructure(structure) }
                                            )
                                        }
                                    }
                                }
                            }
                            is Resource.Error -> ErrorScreen(
                                message = result.message ?: "Error",
                                onRetry = { viewModel.getFeeStructures() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeeStructureItem(structure: FeeStructure, onDelete: () -> Unit) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Fee Structure") },
            text = { Text("Are you sure you want to delete ${structure.feeName}?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteConfirm = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    AppCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(structure.feeName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Class: ${structure.classId}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text("Amount: ₹ ${structure.amount}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Due Date: ${structure.dueDate}", style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun FeeItemCard(fee: FeeViewModel.FeeItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(fee.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Due date : ${fee.dueDate}", color = Color.Gray, fontSize = 14.sp)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(fee.amount, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                Row {
                    OutlinedButton(
                        onClick = { /* View Invoice */ },
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFD32F2F)))
                    ) {
                        Text("INVOICE", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
