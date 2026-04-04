package com.example.schoolmanagementsystem.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.model.UserRole
import com.example.schoolmanagementsystem.ui.navigation.Screen
import com.example.schoolmanagementsystem.ui.theme.EliteGoldGradient
import com.example.schoolmanagementsystem.ui.theme.glassmorphic
import com.example.schoolmanagementsystem.ui.theme.spacing
import com.example.schoolmanagementsystem.ui.components.EliteAIInsightCard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showActionSheet by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is DashboardViewModel.UiEvent.LogoutSuccess -> onLogout()
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                modifier = Modifier.width(300.dp)
            ) {
                DrawerHeader(state)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { DrawerItem(Icons.Rounded.Home, "Home", true) { scope.launch { drawerState.close() } } }
                    item { DrawerItem(Icons.AutoMirrored.Rounded.VolumeUp, "Announcements") { onNavigate(Screen.NotificationList.route) } }
                    item { DrawerItem(Icons.Rounded.CalendarToday, "Events") { onNavigate(Screen.Events.route) } }
                    
                    if (state.user?.role == UserRole.SCHOOL_ADMIN || state.user?.role == UserRole.SUPER_ADMIN) {
                        item { DrawerItem(Icons.Rounded.AdminPanelSettings, "Admin Portal", textColor = MaterialTheme.colorScheme.primary) { onNavigate(Screen.AdminPortal.route) } }
                    }

                    item { DrawerItem(Icons.Rounded.Translate, "Switch language") { } }
                    item { DrawerItem(Icons.Rounded.NotificationsNone, "Notifications") { } }
                    item { DrawerItem(Icons.Rounded.VpnKey, "Change password") { } }
                    
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                    
                    item {
                        Text(
                            "ACCOUNT",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    item { 
                        DrawerItem(
                            icon = Icons.AutoMirrored.Rounded.Logout, 
                            label = "Logout",
                            textColor = MaterialTheme.colorScheme.error
                        ) { 
                            viewModel.logout()
                        } 
                    }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                LargeTopAppBar(
                    title = {
                        Column {
                            Text(
                                "Hello, ${state.userName.split(" ").firstOrNull() ?: "User"}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    brush = EliteGoldGradient,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "Elite Academic Management",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Rounded.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onNavigate(Screen.NotificationList.route) }) {
                            BadgedBox(badge = { Badge { Text("3") } }) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                            }
                        }
                        IconButton(onClick = { onNavigate(Screen.Me.route) }) {
                            Surface(
                                modifier = Modifier.size(36.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EliteGoldGradient)
                            ) {
                                Icon(
                                    Icons.Rounded.Person, 
                                    contentDescription = "Profile",
                                    modifier = Modifier.padding(6.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showActionSheet = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Quick Action")
                }
            },
            bottomBar = {
                DashboardBottomNavigation(currentRoute = Screen.Dashboard.route, onNavigate = onNavigate)
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (state.user?.role) {
                    UserRole.SUPER_ADMIN, UserRole.SCHOOL_ADMIN -> AdminDashboard(state, onNavigate)
                    UserRole.TEACHER -> TeacherDashboard(onNavigate)
                    UserRole.STUDENT -> StudentDashboard(state, onNavigate)
                    else -> if (state.isLoading) LoadingScreen() else EmptyDashboard()
                }

                if (showActionSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showActionSheet = false },
                        sheetState = sheetState,
                        containerColor = MaterialTheme.colorScheme.surface,
                        dragHandle = { BottomSheetDefaults.DragHandle() }
                    ) {
                        QuickActionMenu(onNavigate) { showActionSheet = false }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionMenu(onNavigate: (String) -> Unit, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
    ) {
        Text(
            "Quick Actions",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionButton("Message", Icons.Rounded.Chat, Modifier.weight(1f)) {
                onDismiss()
                onNavigate(Screen.Messages.route)
            }
            QuickActionButton("Attendance", Icons.Rounded.QrCodeScanner, Modifier.weight(1f)) {
                onDismiss()
                onNavigate(Screen.AttendanceClassSelect.route)
            }
            QuickActionButton("Fees", Icons.Rounded.AccountBalanceWallet, Modifier.weight(1f)) {
                onDismiss()
                onNavigate(Screen.FeeList.route)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun QuickActionButton(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.padding(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun EmptyDashboard() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No data available for your role.")
    }
}

@Composable
fun DrawerHeader(state: DashboardViewModel.DashboardState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(EliteGoldGradient)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        Column {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Icon(
                    Icons.Rounded.School, 
                    contentDescription = null, 
                    tint = Color.White, 
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.userName,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = state.userSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun StudentDashboard(state: DashboardViewModel.DashboardState, onNavigate: (String) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp), // Space for FAB/BottomBar
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Elite Stats Pager (UX: Glanceable Metrics)
        item {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp),
                pageSpacing = 16.dp
            ) { page ->
                when(page) {
                    0 -> MetricCard("Attendance", "85%", Icons.Rounded.PieChart, Color(0xFF4CAF50))
                    1 -> MetricCard("Fees Due", "$ ${state.feeDuesAmount}", Icons.Rounded.AccountBalanceWallet, Color(0xFFFF9800))
                    2 -> MetricCard("Academic Rank", "#4 / 42", Icons.Rounded.EmojiEvents, Color(0xFFD4AF37))
                }
            }
        }

        // AI Insight Section
        item {
            EliteAIInsightCard(
                insight = state.aiInsight,
                isLoading = state.isAILoading,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            Text(
                "Quick Actions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionCard("Timetable", Icons.AutoMirrored.Rounded.EventNote, Color(0xFF2196F3), Modifier.weight(1f)) { onNavigate(Screen.TimetableList.route) }
                ActionCard("Assignments", Icons.Rounded.AssignmentInd, Color(0xFF9C27B0), Modifier.weight(1f)) { onNavigate(Screen.Assignments.route) }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionCard("Exams", Icons.Rounded.Assignment, Color(0xFFFF5722), Modifier.weight(1f)) { onNavigate(Screen.ExamClassSelect.route) }
                ActionCard("Library", Icons.Rounded.MenuBook, Color(0xFF607D8B), Modifier.weight(1f)) { onNavigate(Screen.Library.route) }
            }
        }

        if (state.notices.isNotEmpty()) {
            item {
                Text(
                    "Announcements",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                )
            }

            items(state.notices) { notice ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    NoticeItemCard(notice) {
                        onNavigate(Screen.NotificationList.route)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .glassmorphic(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = color.copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(8.dp))
                }
                Text(
                    title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    brush = if (color == Color(0xFFD4AF37)) EliteGoldGradient else null
                ),
                color = if (color != Color(0xFFD4AF37)) color else Color.Unspecified
            )
        }
    }
}

@Composable
fun ActionCard(title: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .glassmorphic()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun TeacherDashboard(onNavigate: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ManagementCard("Class Attendance", "Take attendance for your assigned periods", Icons.Rounded.HowToReg) { onNavigate(Screen.AttendanceClassSelect.route) }
        }
        item {
            ManagementCard("Assignments", "Review and grade student submissions", Icons.Rounded.HistoryEdu) { onNavigate(Screen.Assignments.route) }
        }
        item {
            ManagementCard("Exam Management", "Upload marks and generate report cards", Icons.Rounded.AppRegistration) { onNavigate(Screen.ExamClassSelect.route) }
        }
        item {
            ManagementCard("Student Queries", "Reply to messages from students/parents", Icons.Rounded.QuestionAnswer) { onNavigate(Screen.Messages.route) }
        }
    }
}

@Composable
fun AdminDashboard(state: DashboardViewModel.DashboardState, onNavigate: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AdminStatCard("Total Students", state.studentCount.toString(), Icons.Rounded.People, Modifier.weight(1f))
                AdminStatCard("Total Teachers", state.teacherCount.toString(), Icons.Rounded.SupervisorAccount, Modifier.weight(1f))
            }
        }
        
        item {
            Text(
                "Admin Control Panel",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item { ManagementCard("Admin Portal", "Advanced school management and configurations", Icons.Rounded.AdminPanelSettings) { onNavigate(Screen.AdminPortal.route) } }
        item { ManagementCard("Students", "Manage student profiles and records", Icons.Rounded.PersonSearch) { onNavigate(Screen.StudentList.route) } }
        item { ManagementCard("Teachers", "Manage faculty staff and assignments", Icons.Rounded.School) { onNavigate(Screen.TeacherList.route) } }
        item { ManagementCard("Academic Classes", "Configure classes, sections and subjects", Icons.Rounded.Class) { onNavigate(Screen.ClassList.route) } }
    }
}

@Composable
fun AdminStatCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.glassmorphic(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value, 
                style = MaterialTheme.typography.headlineSmall.copy(
                    brush = EliteGoldGradient,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector, 
    label: String, 
    selected: Boolean = false,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null, tint = if (selected) MaterialTheme.colorScheme.primary else textColor) },
        label = { Text(label, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            unselectedContainerColor = Color.Transparent,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedTextColor = textColor
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun NoticeItemCard(notice: DashboardViewModel.Notice, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphic()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = notice.title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notice.subtitle, 
                    style = MaterialTheme.typography.bodySmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = notice.date, 
                style = MaterialTheme.typography.labelSmall, 
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun ManagementCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphic()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun DashboardBottomNavigation(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val selectedColor = MaterialTheme.colorScheme.primary
        val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant

        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == Screen.Dashboard.route,
            onClick = { onNavigate(Screen.Dashboard.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.ChatBubble, contentDescription = "Messages") },
            label = { Text("Messages") },
            selected = currentRoute == Screen.Messages.route,
            onClick = { onNavigate(Screen.Messages.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Rounded.MenuBook, contentDescription = "My Class") },
            label = { Text("My Class") },
            selected = currentRoute == Screen.MyClass.route,
            onClick = { onNavigate(Screen.MyClass.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.CalendarToday, contentDescription = "Events") },
            label = { Text("Events") },
            selected = currentRoute == Screen.Events.route,
            onClick = { onNavigate(Screen.Events.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Person, contentDescription = "Me") },
            label = { Text("Me") },
            selected = currentRoute == Screen.Me.route,
            onClick = { onNavigate(Screen.Me.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor,
                indicatorColor = Color.Transparent
            )
        )
    }
}
