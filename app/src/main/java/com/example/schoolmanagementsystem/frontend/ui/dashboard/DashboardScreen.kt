package com.example.schoolmanagementsystem.frontend.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.schoolmanagementsystem.backend.domain.model.UserRole
import com.example.schoolmanagementsystem.R
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.animation.core.*
import com.example.schoolmanagementsystem.frontend.ui.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val pullToRefreshState = rememberPullToRefreshState()
    
    var showPaymentSheet by remember { mutableStateOf(false) }
    var showAcademicAlert by remember { mutableStateOf<Pair<String, String>?>(null) }

    // Floating AI Icon position
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is DashboardViewModel.UiEvent.LogoutSuccess -> onLogout()
                is DashboardViewModel.UiEvent.ShowAcademicAlert -> {
                    showAcademicAlert = event.title to event.message
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF111619),
                drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
                modifier = Modifier.width(320.dp),
                windowInsets = WindowInsets(0)
            ) {
                DrawerHeader(state)
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    DrawerItem(Icons.Rounded.Dashboard, "Dashboard", true) { 
                        scope.launch { drawerState.close() } 
                    }
                    DrawerItem(Icons.Rounded.Person, "Elite Profile") { 
                        scope.launch { drawerState.close() }
                        onNavigate(Screen.Profile.route) 
                    }
                    DrawerItem(Icons.Rounded.AccountBalanceWallet, "Fees & Payments") { 
                        scope.launch { drawerState.close() }
                        onNavigate(Screen.FeeList.route) 
                    }
                    DrawerItem(Icons.Rounded.Analytics, "Academic Insights") { 
                        scope.launch { drawerState.close() }
                    }
                    DrawerItem(Icons.Rounded.Settings, "App Settings") { 
                        scope.launch { drawerState.close() }
                        onNavigate(Screen.Me.route) 
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                        color = Color.White.copy(alpha = 0.1f)
                    )
                    
                    DrawerItem(
                        Icons.AutoMirrored.Rounded.Logout, 
                        "Sign Out", 
                        textColor = Color(0xFFE91E63)
                    ) { 
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        viewModel.logout() 
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    ) {
        Scaffold(
            containerColor = Color(0xFF090C0E),
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { 
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                            scope.launch { drawerState.open() } 
                        }) {
                            Icon(Icons.Rounded.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    title = {
                        Text(
                            "Siksha",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        )
                    },
                    actions = {
                        IconButton(onClick = { onNavigate(Screen.NotificationList.route) }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                DashboardBottomNavigation(currentRoute = Screen.Dashboard.route, onNavigate = onNavigate)
            }
        ) { padding ->
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = state.isRefreshing,
                onRefresh = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.refreshData()
                },
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = state.isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                        containerColor = Color(0xFF111619),
                        color = Color(0xFFFFD54F)
                    )
                }
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (state.isLoading && !state.isRefreshing) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFFFFD54F))
                        }
                    } else {
                        when (state.user?.role) {
                            UserRole.STUDENT -> StudentDashboard(state, onNavigate) { showPaymentSheet = true }
                            UserRole.TEACHER -> TeacherDashboard(state, onNavigate)
                            UserRole.SCHOOL_ADMIN, UserRole.SUPER_ADMIN -> AdminDashboard(state, onNavigate)
                            null -> StudentDashboard(state, onNavigate) { showPaymentSheet = true }
                        }
                    }
                }

                // Draggable Floating AI Icon
                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 100.dp)
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFFD54F), Color(0xFFFBC02D))
                            )
                        )
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                        .clickable {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            viewModel.toggleAIDialog(true)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = Color(0xFF090C0E),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // AI Dialog
            if (state.isAIDialogOpen) {
                AIDialog(
                    insight = state.aiInsight,
                    isLoading = state.isAILoading,
                    onDismiss = { viewModel.toggleAIDialog(false) },
                    onQuery = { viewModel.askAI(it) }
                )
            }

            // Payment Sheet Simulation
            if (showPaymentSheet) {
                SettlePaymentBottomSheet(
                    amount = "₹ ${String.format("%.2f", state.feeDuesAmount)}",
                    onDismiss = { showPaymentSheet = false },
                    onConfirm = {
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        // Mock success
                        showPaymentSheet = false
                        viewModel.refreshData()
                    }
                )
            }

            // Academic Alert Dialog
            showAcademicAlert?.let { (title, message) ->
                AlertDialog(
                    onDismissRequest = { showAcademicAlert = null },
                    containerColor = Color(0xFF111619),
                    icon = {
                        Icon(
                            if (title.contains("Warning")) Icons.Rounded.WarningAmber else Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = if (title.contains("Warning")) Color(0xFFFFD54F) else Color(0xFF4CAF50),
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    title = {
                        Text(
                            title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    text = {
                        Text(
                            message,
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { showAcademicAlert = null }) {
                            Text("Acknowledge", color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                        }
                    },
                    shape = RoundedCornerShape(28.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettlePaymentBottomSheet(
    amount: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.payment_success))

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            kotlinx.coroutines.delay(150)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    ModalBottomSheet(
        onDismissRequest = if (!isProcessing) onDismiss else ({}),
        containerColor = Color(0xFF111619),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray.copy(alpha = 0.5f)) },
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isSuccess) {
                LottieAnimation(
                    composition = composition,
                    iterations = 1,
                    modifier = Modifier.size(200.dp)
                )
                Text(
                    "Payment Successful!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A2127)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Close", color = Color.White)
                }
            } else {
                Text(
                    "Settle Account",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Verify payment details before confirming.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1A2127),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Payable Amount", color = Color.Gray)
                            Text(amount, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Gateway Fee", color = Color.Gray)
                            Text("₹ 0.00", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray.copy(alpha = 0.1f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Due", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(amount, color = Color(0xFFFFD54F), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        isProcessing = true
                        scope.launch {
                            kotlinx.coroutines.delay(2000)
                            isProcessing = false
                            isSuccess = true
                            onConfirm()
                        }
                    },
                    enabled = !isProcessing,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF090C0E), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color(0xFF090C0E), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Confirm & Pay Securely", color = Color(0xFF090C0E), fontWeight = FontWeight.ExtraBold)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Secured by Siksha Pay Gateway",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun AIDialog(
    insight: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onQuery: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai_thinking))

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF111619),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = Color(0xFFFFD54F))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Siksha AI Insight", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        LottieAnimation(
                            composition = composition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier.size(120.dp)
                        )
                    }
                } else {
                    Text(
                        insight.ifEmpty { "Hello! I am your AI assistant. Ask me anything about your academic performance or schedules." },
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Ask Siksha Brain...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFFD54F),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { 
                            if (query.isNotBlank()) {
                                onQuery(query)
                                query = ""
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Send", tint = Color(0xFFFFD54F))
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFFFFD54F))
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun StudentDashboard(
    state: DashboardViewModel.DashboardState, 
    onNavigate: (String) -> Unit,
    onSettleAccount: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hello Section
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    "Hello, ${state.userName.split(" ").firstOrNull() ?: "User"}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 32.sp
                    )
                )
                Text(
                    "ACADEMIC STATUS: OPTIMAL",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.Gray,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Attendance Card
        item {
            SikshaAttendanceCard(progress = state.attendancePercentage.coerceIn(0f, 1f))
        }

        // Current Session Card
        item {
            CurrentSessionCard(
                session = state.currentSession,
                onNavigate = onNavigate
            )
        }

        // Action Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    GridActionCard("Timetable", Icons.Rounded.CalendarMonth, Color(0xFF4FC3F7), Modifier.weight(1f)) { onNavigate(Screen.Events.route) }
                    GridActionCard("Assignments", Icons.Rounded.AssignmentTurnedIn, Color(0xFF4CAF50), Modifier.weight(1f)) { onNavigate(Screen.Assignments.route) }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    GridActionCard("Exams", Icons.Rounded.Quiz, Color(0xFFE91E63), Modifier.weight(1f)) { onNavigate(Screen.ExamClassSelect.route) }
                    GridActionCard("Library", Icons.Rounded.AutoStories, Color(0xFF9C27B0), Modifier.weight(1f)) { onNavigate(Screen.Library.route) }
                }
            }
        }

        // AI Insight Section
        item {
            EliteInsightCard(state.aiInsight, state.isAILoading)
        }

        // Pending Fees Card
        item {
            PendingFeesCard(
                amount = "₹ ${String.format("%.2f", state.feeDuesAmount)}",
                onClick = onSettleAccount
            )
        }

         // Upcoming Holidays Section
        if (state.holidays.isNotEmpty()) {
            item {
                UpcomingHolidaysSection(state.holidays)
            }
        }

        // System Ledger Activity
        item {
            SystemLedgerSection()
        }
        
        item { Spacer(modifier = Modifier.height(110.dp)) }
    }
}

@Composable
fun SikshaAttendanceCard(progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111619)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "ATTENDANCE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 36.sp
                        )
                    )
                    Surface(
                        color = Color(0xFF1B2D24),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)
                    ) {
                        Text(
                            "TARGET MET",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    Box(modifier = Modifier.size(6.dp).background(Color(0xFF4CAF50), CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Updated just now",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White.copy(alpha = 0.1f),
                    strokeWidth = 8.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF4CAF50),
                    strokeWidth = 8.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text(
                    "${(progress * 100).toInt()}%",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CurrentSessionCard(
    session: DashboardViewModel.CurrentSession?,
    onNavigate: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111619)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Current Session",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                if (session?.isLive == true) {
                    Surface(
                        color = Color(0xFF2D1B1B),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(Color(0xFFE91E63), CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "LIVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE91E63),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        session?.subject ?: "No Classes Scheduled",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                        Icon(Icons.Rounded.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            session?.time ?: "Check later",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Rounded.Room, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            session?.room ?: "N/A",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                
                Button(
                    onClick = { 
                        if (session != null) {
                            onNavigate(Screen.TimetableList.createRoute(session.subject)) // Temporary routing hack
                        } else {
                            onNavigate(Screen.MyClass.route)
                        }
                    },
                    modifier = Modifier.height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A2127)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text("Details", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GridActionCard(title: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111619))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = color.copy(alpha = 0.15f)
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(14.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                title, 
                style = MaterialTheme.typography.bodyMedium, 
                color = Color.White, 
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EliteInsightCard(insight: String, isLoading: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111619)),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A2127))
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color(0xFF1B2D24)
            ) {
                Icon(Icons.Rounded.Stars, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "AI INSTITUTIONAL INSIGHT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                        color = Color(0xFF4CAF50),
                        trackColor = Color.Transparent
                    )
                } else {
                    Text(
                        insight.ifEmpty { "Projected GPA: 3.8. Focus on 'Algorithmic Complexity' for the upcoming midterm to maintain peak performance." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PendingFeesCard(amount: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111619)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "PENDING FEES",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                    Text(
                        amount,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.05f)
                ) {
                    Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.padding(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB4C0FF)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Settle Account", color = Color(0xFF090C0E), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}


@Composable
fun SystemLedgerSection() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.History, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "SYSTEM LEDGER ACTIVITY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111619)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                LedgerItem(
                    title = "Assignment Submitted",
                    subtitle = "Network Security: Module 4",
                    time = "2 hours ago",
                    isFirst = true
                )
                LedgerItem(
                    title = "Grade Published",
                    subtitle = "Calculus III: Midterm Result (A-)",
                    time = "Yesterday, 4:30 PM",
                    isLast = true
                )
            }
        }
    }
}

@Composable
fun LedgerItem(title: String, subtitle: String, time: String, isFirst: Boolean = false, isLast: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(if (isFirst) Color(0xFF4CAF50) else Color(0xFF4FC3F7), CircleShape)
                    .border(2.dp, Color(0xFF111619), CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(50.dp)
                        .background(Color.Gray.copy(alpha = 0.2f))
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 24.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(subtitle, color = Color.Gray, fontSize = 13.sp)
            Text(time, color = Color.Gray.copy(alpha = 0.6f), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun DashboardBottomNavigation(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFF090C0E),
        tonalElevation = 0.dp,
        modifier = Modifier.height(85.dp)
    ) {
        val items = listOf(
            Triple(Screen.Dashboard.route, Icons.Rounded.GridView, "Home"),
            Triple(Screen.Messages.route, Icons.Rounded.ChatBubbleOutline, "Messages"),
            Triple(Screen.MyClass.route, Icons.Rounded.MenuBook, "My Class"),
            Triple(Screen.Events.route, Icons.Rounded.CalendarToday, "Events"),
            Triple(Screen.Me.route, Icons.Rounded.PersonOutline, "Me")
        )

        items.forEach { (route, icon, label) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                icon = { 
                    if (isSelected) {
                        Surface(
                            color = Color(0xFF1A2127),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                icon, 
                                contentDescription = label, 
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), 
                                tint = Color(0xFF4FC3F7)
                            )
                        }
                    } else {
                        Icon(icon, contentDescription = label, tint = Color.Gray, modifier = Modifier.size(26.dp))
                    }
                },
                label = { 
                    Text(
                        label, 
                        style = MaterialTheme.typography.labelSmall, 
                        color = if (isSelected) Color(0xFF4FC3F7) else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    ) 
                },
                selected = isSelected,
                onClick = { if (!isSelected) onNavigate(route) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
            )
        }
    }
}

@Composable
fun DrawerHeader(state: DashboardViewModel.DashboardState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF090C0E))
            .padding(start = 24.dp, end = 24.dp, top = 60.dp, bottom = 24.dp)
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD54F))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null,
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            state.userName,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 22.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            color = Color(0xFF1B2D24),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                "ELITE STUDENT",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                color = Color(0xFF4CAF50),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}


@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    selected: Boolean = false,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { 
            Icon(
                icon, 
                contentDescription = null, 
                tint = if (selected) Color(0xFF4FC3F7) else textColor.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            ) 
        },
        label = { 
            Text(
                label, 
                color = if (selected) Color(0xFF4FC3F7) else textColor,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 16.sp
            ) 
        },
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .padding(vertical = 2.dp)
            .height(56.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = Color(0xFF1A2127),
            selectedIconColor = Color(0xFF4FC3F7),
            unselectedIconColor = textColor.copy(alpha = 0.7f),
            selectedTextColor = Color(0xFF4FC3F7),
            unselectedTextColor = textColor
        ),
        shape = RoundedCornerShape(16.dp)
    )
}


@Composable
fun TeacherDashboard(state: DashboardViewModel.DashboardState, onNavigate: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    "Elite Faculty, ${state.userName.split(" ").firstOrNull() ?: "User"}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 32.sp
                    )
                )
                Text(
                    "SIKSHA ACADEMIC MANAGEMENT",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.Gray,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                AdminStatCard("Students", state.studentCount.toString(), Icons.Rounded.People, Color(0xFF4FC3F7), Modifier.weight(1f))
                AdminStatCard("Classes", state.classCount.toString(), Icons.Rounded.Class, Color(0xFF4CAF50), Modifier.weight(1f))
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    GridActionCard("Mark Attendance", Icons.Rounded.FactCheck, Color(0xFF4FC3F7), Modifier.weight(1f)) { onNavigate(Screen.AttendanceClassSelect.route) }
                    GridActionCard("Manage Exams", Icons.Rounded.Quiz, Color(0xFFE91E63), Modifier.weight(1f)) { onNavigate(Screen.ExamClassSelect.route) }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    GridActionCard("My Subjects", Icons.Rounded.AutoStories, Color(0xFF9C27B0), Modifier.weight(1f)) { onNavigate(Screen.SubjectList.route) }
                    GridActionCard("Announcements", Icons.Rounded.Campaign, Color(0xFFFFD54F), Modifier.weight(1f)) { onNavigate(Screen.NotificationList.route) }
                }
            }
        }

        // Upcoming Holidays Section
        if (state.holidays.isNotEmpty()) {
            item {
                UpcomingHolidaysSection(state.holidays)
            }
        }
        
        item { Spacer(modifier = Modifier.height(110.dp)) }
    }
}

@Composable
fun AdminDashboard(state: DashboardViewModel.DashboardState, onNavigate: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    "Elite Admin, ${state.userName.split(" ").firstOrNull() ?: "User"}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 32.sp
                    )
                )
                Text(
                    "SIKSHA CENTRAL CONTROL",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.Gray,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AdminStatCard("Total Students", state.studentCount.toString(), Icons.Rounded.People, Color(0xFF4FC3F7), Modifier.weight(1f))
                    AdminStatCard("Total Teachers", state.teacherCount.toString(), Icons.Rounded.Person, Color(0xFF4CAF50), Modifier.weight(1f))
                }
                AdminStatCard("Active Classes", state.classCount.toString(), Icons.Rounded.Class, Color(0xFF9C27B0))
            }
        }

        item {
            Text(
                "ADMINISTRATIVE ACTIONS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    GridActionCard("Teachers", Icons.Rounded.School, Color(0xFF4CAF50), Modifier.weight(1f)) { onNavigate(Screen.TeacherList.route) }
                    GridActionCard("Students", Icons.Rounded.People, Color(0xFF4FC3F7), Modifier.weight(1f)) { onNavigate(Screen.StudentList.route) }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    GridActionCard("Classes", Icons.Rounded.Class, Color(0xFF9C27B0), Modifier.weight(1f)) { onNavigate(Screen.ClassList.route) }
                    GridActionCard("Fees Control", Icons.Rounded.Payments, Color(0xFFFFD54F), Modifier.weight(1f)) { onNavigate(Screen.FeeList.route) }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    GridActionCard("Inventory", Icons.Rounded.Inventory, Color(0xFFE91E63), Modifier.weight(1f)) { onNavigate(Screen.Inventory.route) }
                    GridActionCard("Advanced", Icons.Rounded.SettingsApplications, Color(0xFF607D8B), Modifier.weight(1f)) { onNavigate(Screen.AdminPortal.route) }
                }
            }
        }

        // Upcoming Holidays Section
        if (state.holidays.isNotEmpty()) {
            item {
                UpcomingHolidaysSection(state.holidays)
            }
        }
        
        item { Spacer(modifier = Modifier.height(110.dp)) }
    }
}

@Composable
fun AdminStatCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111619))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.15f)
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Color.White))
            }
        }
    }
}

@Composable
fun UpcomingHolidaysSection(holidays: List<DashboardViewModel.Holiday>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "UPCOMING HOLIDAYS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            )
            Text(
                "View All",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable { /* Navigation for holidays could be added here */ }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            holidays.take(2).forEach { holiday ->
                HolidayItemCard(holiday, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun HolidayItemCard(holiday: DashboardViewModel.Holiday, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111619)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color(0xFF1A2127), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    holiday.day,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD54F)
                    )
                )
                Text(
                    holiday.month.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                holiday.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}
