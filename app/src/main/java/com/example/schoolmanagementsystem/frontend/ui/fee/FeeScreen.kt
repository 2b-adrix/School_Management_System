package com.example.schoolmanagementsystem.frontend.ui.fee

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.backend.domain.model.FeeStructure
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import com.example.schoolmanagementsystem.frontend.ui.components.ErrorScreen
import com.example.schoolmanagementsystem.frontend.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeScreen(
    onNavigateBack: () -> Unit,
    onAddFeeClick: () -> Unit,
    viewModel: FeeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(modifier = Modifier.background(Color.Transparent)) {
                TopAppBar(
                    title = {
                        Text(
                            "Financial Overview",
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                
                ScrollableTabRow(
                    selectedTabIndex = state.selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 20.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                            color = MaterialTheme.colorScheme.primary,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    val tabs = listOf("Pending", "History", "Structures")
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = state.selectedTab == index,
                            onClick = { viewModel.onTabSelected(index) },
                            text = {
                                Text(
                                    title,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (state.selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (state.selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (state.selectedTab == 2) {
                FloatingActionButton(
                    onClick = onAddFeeClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total Outstanding Balance", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            state.totalDue,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Next due date: Dec 15, 2024", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text("Detailed Breakdown", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        items(state.dueFees) { fee ->
                            FeeItemCard(fee, isPending = true)
                        }
                    }
                }
                1 -> {
                    // Paid Fees Section
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.paidFees) { fee ->
                            FeeItemCard(fee, isPending = false)
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
                                        contentPadding = PaddingValues(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(structure.feeName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("Class: ${structure.classId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text("₹ ${structure.amount}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
                Text("Due Date: ${structure.dueDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun FeeItemCard(fee: FeeViewModel.FeeItem, isPending: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(fee.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (isPending) "Scheduled due: ${fee.dueDate}" else "Paid on: ${fee.dueDate}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Surface(
                    color = if (isPending) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        if (isPending) "PENDING" else "PAID",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (isPending) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    fee.amount,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleLarge
                )
                
                Button(
                    onClick = { /* View Invoice */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPending) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isPending) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        if (isPending) Icons.Rounded.Payment else Icons.AutoMirrored.Rounded.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isPending) "PAY NOW" else "RECEIPT",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

