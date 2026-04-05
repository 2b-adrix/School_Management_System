package com.example.schoolmanagementsystem.frontend.ui.message

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.frontend.ui.dashboard.DashboardBottomNavigation
import com.example.schoolmanagementsystem.frontend.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onNavigate: (String) -> Unit,
    viewModel: MessagesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFF090C0E),
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(top = 24.dp, bottom = 8.dp)
            ) {
                Text(
                    "Elite Messages",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontSize = 32.sp
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
                
                // Search Bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .height(56.dp),
                    placeholder = { Text("Search by name or content...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4FC3F7),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color(0xFF111619),
                        unfocusedContainerColor = Color(0xFF111619),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* New Message */ },
                containerColor = Color(0xFFB4C0FF),
                contentColor = Color(0xFF090C0E),
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Rounded.AddComment, contentDescription = "New Message")
            }
        },
        bottomBar = {
            DashboardBottomNavigation(currentRoute = Screen.Messages.route, onNavigate = onNavigate)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(state.messages) { message ->
                ExpandableMessageItem(message, onNavigate)
            }
        }
    }
}

@Composable
fun ExpandableMessageItem(message: MessagesViewModel.MessageItem, onNavigate: (String) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) Color(0xFF1A2127) else Color(0xFF111619)
        ),
        border = if (isExpanded) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4FC3F7).copy(alpha = 0.3f)) else null
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar with Status Indicator
                Box {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1A2127)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    if (message.isOnline) {
                        Canvas(
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.BottomEnd)
                                .border(2.dp, Color(0xFF111619), CircleShape)
                        ) {
                            drawCircle(color = Color(0xFF4CAF50))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            message.sender,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                        )
                        Text(
                            message.time,
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Surface(
                        color = if (message.role == "admin") Color(0xFF2D1B1B) else Color(0xFF1B2D24),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            message.role.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = if (message.role == "admin") Color(0xFFE91E63) else Color(0xFF4CAF50),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    message.content,
                    color = if (isExpanded) Color.White else Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    lineHeight = 22.sp
                )
                
                if (!isExpanded && message.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(20.dp)
                            .background(Color(0xFF4FC3F7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            message.unreadCount.toString(),
                            color = Color(0xFF090C0E),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onNavigate(Screen.ChatDetail.createRoute(message.id, message.sender)) }) {
                        Text("Reply", color = Color(0xFF4FC3F7), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onNavigate(Screen.ChatDetail.createRoute(message.id, message.sender)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A2127)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("View Thread", color = Color.White)
                    }
                }
            }
        }
    }
}

