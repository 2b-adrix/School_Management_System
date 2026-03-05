package com.example.schoolmanagementsystem.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schoolmanagementsystem.domain.model.UserRole
import com.example.schoolmanagementsystem.ui.navigation.Screen
import com.example.schoolmanagementsystem.ui.theme.spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerShape = RoundedCornerShape(0.dp),
                modifier = Modifier.width(300.dp)
            ) {
                // Drawer Header
                DrawerHeader(state)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Drawer Items
                DrawerItem(Icons.Rounded.Home, "Home") { scope.launch { drawerState.close() } }
                DrawerItem(Icons.AutoMirrored.Rounded.VolumeUp, "Announcements") { onNavigate(Screen.NotificationList.route) }
                DrawerItem(Icons.Rounded.CalendarToday, "Events") { onNavigate(Screen.Events.route) }
                DrawerItem(Icons.Rounded.Translate, "Switch language") { }
                DrawerItem(Icons.Rounded.NotificationsNone, "Notification Settings") { }
                DrawerItem(Icons.Rounded.VpnKey, "Change password") { }
                DrawerItem(Icons.Rounded.DarkMode, "Dark Mode") { }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                
                Text(
                    "SWITCH SCHOOL",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                DrawerItem(Icons.AutoMirrored.Rounded.Logout, "LOG OUT") { /* Handle Logout */ }
                
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Version - 1.3.657",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Dashboard",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Rounded.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { onNavigate(Screen.NotificationList.route) }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = primaryColor)
                )
            },
            bottomBar = {
                if (state.user?.role == UserRole.STUDENT) {
                    DashboardBottomNavigation(currentRoute = Screen.Dashboard.route, onNavigate = onNavigate)
                }
            }
        ) { padding ->
            when (state.user?.role) {
                UserRole.ADMIN -> AdminDashboard(padding, state, onNavigate)
                UserRole.TEACHER -> TeacherDashboard(padding, onNavigate)
                UserRole.STUDENT -> StudentDashboard(padding, state, onNavigate)
                else -> LoadingScreen()
            }
        }
    }
}

@Composable
fun DrawerHeader(state: DashboardViewModel.DashboardState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = state.userName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = state.userSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun StudentDashboard(padding: PaddingValues, state: DashboardViewModel.DashboardState, onNavigate: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ProfileHeaderCard(
                name = state.userName,
                subtitle = state.userSubtitle,
                onClick = { onNavigate(Screen.Me.route) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item { DashboardItemCard("Attendance", Icons.Rounded.AssignmentTurnedIn, Color(0xFF4CAF50)) { onNavigate(Screen.AttendanceClassSelect.route) } }
        item { BlockedStatusText() }
        item { DashboardItemCard("Fees", Icons.Rounded.AttachMoney, Color(0xFFF44336), state.feeDues) { onNavigate(Screen.FeeList.route) } }
        item { DashboardItemCard("Announcements", Icons.AutoMirrored.Rounded.VolumeUp, Color(0xFFCDDC39)) { onNavigate(Screen.NotificationList.route) } }
        item { DashboardItemCard("My Subjects", Icons.AutoMirrored.Rounded.LibraryBooks, Color(0xFF00BCD4)) { onNavigate(Screen.SubjectList.route) } }
        item { DashboardItemCard("Gallery", Icons.Rounded.Image, Color(0xFF00ACC1)) { onNavigate(Screen.Gallery.route) } }
        item { DashboardItemCard("Exam Reports", Icons.Rounded.Assignment, Color(0xFF7E57C2)) { onNavigate(Screen.ExamClassSelect.route) } }
        item { BlockedStatusText() }
        item { DashboardItemCard("Timetable", Icons.Rounded.Alarm, Color(0xFFFFA000), state.timetableClasses) { onNavigate(Screen.TimetableList.route) } }
        item { DashboardItemCard("Assignments", Icons.Rounded.AssignmentInd, Color(0xFF673AB7)) { onNavigate(Screen.Assignments.route) } }
        item { DashboardItemCard("Library", Icons.AutoMirrored.Rounded.MenuBook, Color(0xFF8BC34A)) { onNavigate(Screen.Library.route) } }
        item { DashboardItemCard("Events", Icons.Rounded.CalendarMonth, Color(0xFFE91E63), state.eventsCount) { onNavigate(Screen.Events.route) } }
        items(state.notices) { notice -> NoticeItemCard(notice) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun TeacherDashboard(padding: PaddingValues, onNavigate: (String) -> Unit) {
    // Teacher-specific dashboard with management items
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { ManagementCard("Mark Attendance", "Take attendance for your classes", Icons.Rounded.FactCheck) { onNavigate(Screen.AttendanceClassSelect.route) } }
        item { ManagementCard("Assignments", "Create and manage assignments", Icons.Rounded.Assignment) { onNavigate(Screen.Assignments.route) } }
        item { ManagementCard("Exam Marks", "Enter and view exam marks", Icons.Rounded.EditNote) { onNavigate(Screen.ExamClassSelect.route) } }
        item { ManagementCard("My Classes", "View your assigned classes", Icons.Rounded.Class) { onNavigate(Screen.ClassList.route) } }
    }
}

@Composable
fun AdminDashboard(padding: PaddingValues, state: DashboardViewModel.DashboardState, onNavigate: (String) -> Unit) {
    // Admin dashboard with full management capabilities
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard("Students", state.studentCount.toString(), Modifier.weight(1f))
                StatCard("Teachers", state.teacherCount.toString(), Modifier.weight(1f))
            }
        }
        item { ManagementCard("Manage Students", "Add, view, and edit student profiles", Icons.Rounded.People) { onNavigate(Screen.StudentList.route) } }
        item { ManagementCard("Manage Teachers", "Add, view, and edit teacher profiles", Icons.Rounded.SupervisorAccount) { onNavigate(Screen.TeacherList.route) } }
        item { ManagementCard("Manage Classes", "Create and manage school classes", Icons.Rounded.Class) { onNavigate(Screen.ClassList.route) } }
        item { ManagementCard("Manage Subjects", "Add or update course subjects", Icons.AutoMirrored.Rounded.MenuBook) { onNavigate(Screen.SubjectList.route) } }
        item { ManagementCard("Manage Fees", "Set up fee structures and track payments", Icons.Rounded.Payments) { onNavigate(Screen.FeeList.route) } }
        item { ManagementCard("Manage Exams", "Schedule exams and manage results", Icons.Rounded.Assignment) { onNavigate(Screen.ExamClassSelect.route) } }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ManagementCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(text = value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 8.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            unselectedIconColor = MaterialTheme.colorScheme.onSurface,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun ProfileHeaderCard(name: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = "Student : $subtitle", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
fun DashboardItemCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    badge: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.weight(1f)
            )
            if (badge != null) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
fun BlockedStatusText() {
    Text(
        text = "The reports have been blocked due to unpaid fees.",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 4.dp)
    )
}

@Composable
fun NoticeItemCard(notice: DashboardViewModel.Notice) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notice.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    fontSize = 14.sp
                )
                Text(text = notice.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = notice.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
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
            icon = { Icon(Icons.Rounded.Groups, contentDescription = "Me") },
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
