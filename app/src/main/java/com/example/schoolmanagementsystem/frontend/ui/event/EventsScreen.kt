package com.example.schoolmanagementsystem.frontend.ui.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.schoolmanagementsystem.frontend.ui.dashboard.DashboardBottomNavigation
import com.example.schoolmanagementsystem.frontend.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onNavigate: (String) -> Unit,
    viewModel: EventsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()


    Scaffold(
        containerColor = Color(0xFF090C0E),
        topBar = {
            Column(modifier = Modifier.background(Color(0xFF090C0E))) {
                TopAppBar(
                    title = { 
                        Text(
                            "Elite Calendar", 
                            color = Color.White, 
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        ) 
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF090C0E))
                )
                ScrollableTabRow(
                    selectedTabIndex = state.selectedTab,
                    containerColor = Color(0xFF090C0E),
                    contentColor = Color(0xFF4FC3F7),
                    edgePadding = 20.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                            color = Color(0xFF4FC3F7)
                        )
                    },
                    divider = {}
                ) {
                    val tabs = listOf("ALL EVENTS", "FEES", "EXAMS", "HOLIDAYS")
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = state.selectedTab == index,
                            onClick = { viewModel.onTabSelected(index) },
                            text = { 
                                Text(
                                    title, 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.selectedTab == index) Color(0xFF4FC3F7) else Color.Gray
                                ) 
                            }
                        )
                    }
                }
            }
        },
        bottomBar = {
            DashboardBottomNavigation(currentRoute = Screen.Events.route, onNavigate = onNavigate)
        }
    ) { padding ->

        val filteredEvents = when (state.selectedTab) {
            1 -> state.events.filter { it.type == EventsViewModel.EventType.FEES }
            2 -> state.events.filter { it.type == EventsViewModel.EventType.EXAMS }
            3 -> state.events.filter { it.type == EventsViewModel.EventType.HOLIDAY }
            else -> state.events
        }

        Box(modifier = Modifier.padding(padding)) {
            if (filteredEvents.isEmpty()) {
                EmptyEventsState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Group by month
                    val grouped = filteredEvents.groupBy { it.monthYear }
                    grouped.forEach { (month, monthEvents) ->
                        item {
                            Text(
                                text = month.uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(monthEvents) { event ->
                            EventItemRow(event)
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun EventItemRow(event: EventsViewModel.EventItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(45.dp)
        ) {
            Text(
                event.day, 
                fontSize = 22.sp, 
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                event.dayName.uppercase(), 
                fontSize = 11.sp, 
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111619)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A2127))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = when(event.type) {
                            EventsViewModel.EventType.FEES -> Color(0xFFB4C0FF)
                            EventsViewModel.EventType.EXAMS -> Color(0xFFE91E63)
                            EventsViewModel.EventType.HOLIDAY -> Color(0xFF4CAF50)
                            else -> Color(0xFF4FC3F7)
                        }.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            event.type.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = when(event.type) {
                                EventsViewModel.EventType.FEES -> Color(0xFFB4C0FF)
                                EventsViewModel.EventType.EXAMS -> Color(0xFFE91E63)
                                EventsViewModel.EventType.HOLIDAY -> Color(0xFF4CAF50)
                                else -> Color(0xFF4FC3F7)
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    event.title, 
                    color = Color.White, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    event.subtitle, 
                    color = Color.Gray, 
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

    }
}

@Composable
fun EmptyEventsState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Rounded.EventBusy, 
                contentDescription = null, 
                modifier = Modifier.size(64.dp), 
                tint = Color(0xFF1A2127)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No events scheduled", 
                color = Color.Gray, 
                fontWeight = FontWeight.Bold
            )
        }
    }
}
