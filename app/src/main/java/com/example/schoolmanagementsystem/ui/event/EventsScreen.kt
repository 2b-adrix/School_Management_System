package com.example.schoolmanagementsystem.ui.event

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
import com.example.schoolmanagementsystem.ui.dashboard.DashboardBottomNavigation
import com.example.schoolmanagementsystem.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onNavigate: (String) -> Unit,
    viewModel: EventsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            Column(modifier = Modifier.background(primaryColor)) {
                TopAppBar(
                    title = { Text("Events", color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
                )
                ScrollableTabRow(
                    selectedTabIndex = state.selectedTab,
                    containerColor = primaryColor,
                    contentColor = Color.White,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                            color = Color.White
                        )
                    },
                    divider = {}
                ) {
                    val tabs = listOf("ALL", "FEES", "EXAMS", "HOLIDAYS")
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = state.selectedTab == index,
                            onClick = { viewModel.onTabSelected(index) },
                            text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
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
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Group by month
                    val grouped = filteredEvents.groupBy { it.monthYear }
                    grouped.forEach { (month, monthEvents) ->
                        item {
                            Text(
                                text = month,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(monthEvents) { event ->
                            EventItemRow(event)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventItemRow(event: EventsViewModel.EventItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Text(event.day, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(event.dayName, fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4DB6AC))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(event.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(event.subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun EmptyEventsState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.EventBusy, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No events", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}
