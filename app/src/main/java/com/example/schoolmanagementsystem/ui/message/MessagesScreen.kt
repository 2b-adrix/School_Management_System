package com.example.schoolmanagementsystem.ui.message

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddComment
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.ui.dashboard.DashboardBottomNavigation
import com.example.schoolmanagementsystem.ui.navigation.Screen
import com.example.schoolmanagementsystem.ui.theme.EliteGoldGradient
import com.example.schoolmanagementsystem.ui.theme.PremiumBlueGradient
import com.example.schoolmanagementsystem.ui.theme.glassmorphic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onNavigate: (String) -> Unit,
    viewModel: MessagesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color(0xFF0F172A), // Premium Dark Blue background
        topBar = {
            Column(
                modifier = Modifier
                    .background(PremiumBlueGradient)
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                Text(
                    "Communications",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                
                // Search Bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(56.dp),
                    placeholder = { Text("Search conversations...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD4AF37),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* New Message */ },
                containerColor = Color(0xFFD4AF37), // Elite Gold
                contentColor = Color.Black,
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(state.messages) { message ->
                MessageItemRow(message)
            }
        }
    }
}

@Composable
fun MessageItemRow(message: MessagesViewModel.MessageItem) {
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
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with Status Indicator
            Box {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                if (message.isOnline) {
                    Canvas(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .border(2.dp, Color(0xFF0F172A), CircleShape)
                    ) {
                        drawCircle(color = Color(0xFF4CAF50)) // Online Green
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
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        message.time,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    message.role.uppercase(),
                    color = Color(0xFFD4AF37), // Elite Gold
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        message.content,
                        color = if (message.unreadCount > 0) Color.White else Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = if (message.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (message.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(EliteGoldGradient, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                message.unreadCount.toString(),
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
