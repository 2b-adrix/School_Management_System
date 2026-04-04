package com.example.schoolmanagementsystem.frontend.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.schoolmanagementsystem.frontend.ui.components.SchoolTopAppBar
import com.example.schoolmanagementsystem.frontend.ui.navigation.Screen
import com.example.schoolmanagementsystem.frontend.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPortalScreen(
    onNavigate: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            SchoolTopAppBar(
                title = "Admin Portal",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            AdminHeader()
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(adminModules) { module ->
                    AdminModuleCard(module = module) {
                        onNavigate(module.route)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "School Management",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                "Control all academic and administrative tasks from one place.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminModuleCard(module: AdminModule, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .height(140.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = module.color.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = module.icon,
                    contentDescription = null,
                    tint = module.color,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = module.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

data class AdminModule(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

val adminModules = listOf(
    AdminModule("Students", Icons.Rounded.People, Color(0xFF2196F3), Screen.StudentList.route),
    AdminModule("Teachers", Icons.Rounded.SupervisorAccount, Color(0xFF4CAF50), Screen.TeacherList.route),
    AdminModule("Classes", Icons.Rounded.Class, Color(0xFFFF9800), Screen.ClassList.route),
    AdminModule("Subjects", Icons.Rounded.Book, Color(0xFF9C27B0), Screen.SubjectList.route),
    AdminModule("Exams", Icons.Rounded.Assignment, Color(0xFFF44336), Screen.ExamClassSelect.route),
    AdminModule("Fees", Icons.Rounded.AccountBalanceWallet, Color(0xFF607D8B), Screen.FeeList.route),
    AdminModule("Announcements", Icons.AutoMirrored.Rounded.VolumeUp, Color(0xFFE91E63), Screen.NotificationList.route),
    AdminModule("Attendance Report", Icons.Rounded.Assessment, Color(0xFF009688), Screen.AttendanceReport.route),
    AdminModule("Teacher Salary", Icons.Rounded.Payments, Color(0xFF795548), Screen.TeacherSalary.route),
    AdminModule("Inventory", Icons.Rounded.Inventory, Color(0xFFCDDC39), Screen.Inventory.route)
)

