package com.example.schoolmanagementsystem.frontend.ui.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.backend.domain.model.TimetableEntry
import com.example.schoolmanagementsystem.frontend.ui.components.*
import com.example.schoolmanagementsystem.frontend.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    onNavigateBack: () -> Unit,
    viewModel: TimetableViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .background(DarkBackground)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .background(EliteGlassWhite, CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
                
                Text(
                    text = "Weekly Schedule",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                ScrollableTabRow(
                    selectedTabIndex = state.selectedDay,
                    containerColor = Color.Transparent,
                    contentColor = DarkPrimary,
                    edgePadding = 24.dp,
                    indicator = { tabPositions ->
                        if (state.selectedDay < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedDay]),
                                color = DarkPrimary
                            )
                        }
                    },
                    divider = {}
                ) {
                    val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT")
                    days.forEachIndexed { index, title ->
                        Tab(
                            selected = state.selectedDay == index,
                            onClick = { viewModel.onDaySelected(index) },
                            text = { 
                                Text(
                                    title, 
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (state.selectedDay == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (state.selectedDay == index) DarkPrimary else DarkOnSurfaceVariant
                                ) 
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            indicator = {
                val pullState = rememberPullToRefreshState()
                PullToRefreshDefaults.Indicator(
                    state = pullState,
                    isRefreshing = state.isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = DarkSurface,
                    color = DarkPrimary
                )
            }
        ) {
            if (state.isLoading && state.schedule.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DarkPrimary)
                }
            } else {
                val currentDaySchedule = state.schedule[state.selectedDay] ?: emptyList()

                if (currentDaySchedule.isEmpty()) {
                    TimetableEmptyPlaceholder()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(currentDaySchedule.size) { index ->
                            val displayEntry = currentDaySchedule[index]
                            EliteEntranceAnimation(index = index) {
                                EliteTimetableEntryCard(displayEntry)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EliteTimetableEntryCard(displayEntry: TimetableViewModel.TimetableDisplayEntry) {
    val entry = displayEntry.entry
    EliteGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time Column
            Column(
                modifier = Modifier.width(70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = entry.startTime,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(2.dp)
                        .background(DarkPrimary.copy(alpha = 0.3f))
                )
                Text(
                    text = entry.endTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkOnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Subject Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayEntry.subjectName, 
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Person, 
                        contentDescription = null, 
                        tint = DarkPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = displayEntry.teacherName,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkOnSurfaceVariant
                    )
                    
                    if (entry.roomNumber != null) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            Icons.Rounded.LocationOn, 
                            contentDescription = null, 
                            tint = DarkPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Room ${entry.roomNumber}", 
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkOnSurfaceVariant
                        )
                    }
                }
            }

            // Status Indicator
            Surface(
                modifier = Modifier.size(10.dp),
                shape = CircleShape,
                color = DarkPrimary,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.2f))
            ) {}
        }
    }
}

@Composable
private fun TimetableEmptyPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Rounded.AccessTime,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No classes scheduled",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
