package com.example.schoolmanagementsystem.ui.fee

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
import com.example.schoolmanagementsystem.domain.model.FeeStructure
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.ui.components.ErrorScreen
import com.example.schoolmanagementsystem.ui.components.LoadingScreen
import com.example.schoolmanagementsystem.ui.theme.EliteGoldGradient
import com.example.schoolmanagementsystem.ui.theme.PremiumBlueGradient
import com.example.schoolmanagementsystem.ui.theme.glassmorphic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeScreen(
    onNavigateBack: () -> Unit,
    onAddFeeClick: () -> Unit,
    viewModel: FeeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFF0F172A), // Premium Dark Blue
        topBar = {
            Column(modifier = Modifier.background(PremiumBlueGradient)) {
                TopAppBar(
                    title = {
                        Text(
                            "Financial Overview",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
                
                ScrollableTabRow(
                    selectedTabIndex = state.selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                            color = Color(0xFFD4AF37) // Elite Gold indicator
                        )
                    },
                    divider = {}
                ) {
                    val tabs = listOf("Pending Dues", "Payment History", "Fee Structures")
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = state.selectedTab == index,
                            onClick = { viewModel.onTabSelected(index) },
                            text = {
                                Text(
                                    title,
                                    fontSize = 13.sp,
                                    fontWeight = if (state.selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (state.selectedTab == index) Color.White else Color.White.copy(alpha = 0.6f)
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
                    containerColor = Color(0xFFD4AF37),
                    contentColor = Color.Black,
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
                            .glassmorphic(
                                backgroundColor = Color(0xFFD4AF37).copy(alpha = 0.05f),
                                borderColor = Color(0xFFD4AF37).copy(alpha = 0.2f)
                            )
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total Outstanding Balance", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            state.totalDue,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                brush = EliteGoldGradient,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.05f), CircleShape)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Rounded.Info, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Next due date: Dec 15, 2024", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        }
                    }
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text("Detailed Breakdown", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphic(
                backgroundColor = Color.White.copy(alpha = 0.03f),
                borderColor = Color.White.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(structure.feeName, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
                Text("Class: ${structure.classId}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("₹ ${structure.amount}", fontWeight = FontWeight.ExtraBold, color = Color(0xFFD4AF37), fontSize = 18.sp)
                Text("Due Date: ${structure.dueDate}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            IconButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun FeeItemCard(fee: FeeViewModel.FeeItem, isPending: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.05f),
                            Color.White.copy(alpha = 0.02f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(fee.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (isPending) "Scheduled due: ${fee.dueDate}" else "Paid on: ${fee.dueDate}",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
                
                Surface(
                    color = if (isPending) Color(0xFFFF9800).copy(alpha = 0.15f) else Color(0xFF4CAF50).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        if (isPending) "PENDING" else "PAID",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (isPending) Color(0xFFFFB74D) else Color(0xFF81C784),
                        fontSize = 10.sp,
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
                    fontSize = 20.sp,
                    color = Color.White
                )
                
                Button(
                    onClick = { /* View Invoice */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPending) Color(0xFFD4AF37) else Color.White.copy(alpha = 0.1f),
                        contentColor = if (isPending) Color.Black else Color.White
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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
