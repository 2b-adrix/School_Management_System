package com.example.schoolmanagementsystem

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.schoolmanagementsystem.ui.admin.AdminPortalScreen
import com.example.schoolmanagementsystem.ui.assignment.AssignmentScreen
import com.example.schoolmanagementsystem.ui.attendance.AttendanceClassSelectScreen
import com.example.schoolmanagementsystem.ui.attendance.AttendanceMarkScreen
import com.example.schoolmanagementsystem.ui.auth.LoginScreen
import com.example.schoolmanagementsystem.ui.dashboard.DashboardScreen
import com.example.schoolmanagementsystem.ui.event.EventsScreen
import com.example.schoolmanagementsystem.ui.exam.AddExamScreen
import com.example.schoolmanagementsystem.ui.exam.ExamClassSelectScreen
import com.example.schoolmanagementsystem.ui.exam.ExamListScreen
import com.example.schoolmanagementsystem.ui.exam.MarkEntryScreen
import com.example.schoolmanagementsystem.ui.fee.AddFeeStructureScreen
import com.example.schoolmanagementsystem.ui.fee.FeeScreen
import com.example.schoolmanagementsystem.ui.gallery.GalleryScreen
import com.example.schoolmanagementsystem.ui.library.LibraryScreen
import com.example.schoolmanagementsystem.ui.me.MeScreen
import com.example.schoolmanagementsystem.ui.message.MessagesScreen
import com.example.schoolmanagementsystem.ui.myclass.MyClassScreen
import com.example.schoolmanagementsystem.ui.navigation.Screen
import com.example.schoolmanagementsystem.ui.notification.AddAnnouncementScreen
import com.example.schoolmanagementsystem.ui.notification.NotificationListScreen
import com.example.schoolmanagementsystem.ui.profile.ProfileScreen
import com.example.schoolmanagementsystem.ui.schoolclass.AddClassScreen
import com.example.schoolmanagementsystem.ui.schoolclass.ClassListScreen
import com.example.schoolmanagementsystem.ui.student.AddStudentScreen
import com.example.schoolmanagementsystem.ui.student.StudentDetailScreen
import com.example.schoolmanagementsystem.ui.student.StudentListScreen
import com.example.schoolmanagementsystem.ui.subject.AddSubjectScreen
import com.example.schoolmanagementsystem.ui.subject.SubjectListScreen
import com.example.schoolmanagementsystem.ui.teacher.AddTeacherScreen
import com.example.schoolmanagementsystem.ui.teacher.TeacherDetailScreen
import com.example.schoolmanagementsystem.ui.teacher.TeacherListScreen
import com.example.schoolmanagementsystem.ui.timetable.AddTimetableEntryScreen
import com.example.schoolmanagementsystem.ui.timetable.TimetableListScreen
import com.example.schoolmanagementsystem.ui.timetable.TimetableScreen
import com.example.schoolmanagementsystem.ui.theme.SchoolManagementSystemTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by mainViewModel.themeMode.collectAsState()
            val languageCode by mainViewModel.languageCode.collectAsState()

            val darkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            val context = LocalContext.current
            LaunchedEffect(languageCode) {
                updateLocale(context, languageCode)
            }

            SchoolManagementSystemTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Screen.Login.route) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    
                    composable(Screen.Dashboard.route) {
                        DashboardScreen(
                            onNavigate = { route ->
                                navController.navigate(route)
                            },
                            onLogout = {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.AdminPortal.route) {
                        AdminPortalScreen(
                            onNavigate = { route -> navController.navigate(route) },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    
                    // Profile & Me
                    composable(Screen.Me.route) {
                        MeScreen(
                            onNavigate = { route -> navController.navigate(route) },
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    // My Class
                    composable(Screen.MyClass.route) {
                        MyClassScreen(onNavigate = { route -> navController.navigate(route) })
                    }

                    // Events
                    composable(Screen.Events.route) {
                        EventsScreen(onNavigate = { route -> navController.navigate(route) })
                    }

                    // Messages
                    composable(Screen.Messages.route) {
                        MessagesScreen(onNavigate = { route -> navController.navigate(route) })
                    }

                    // Gallery
                    composable(Screen.Gallery.route) {
                        GalleryScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    // Assignments
                    composable(Screen.Assignments.route) {
                        AssignmentScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    // Library
                    composable(Screen.Library.route) {
                        LibraryScreen(
                            onNavigate = { route -> navController.navigate(route) },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    // Timetable
                    composable(
                        route = Screen.TimetableList.route,
                        arguments = listOf(navArgument("classId") { type = NavType.StringType; defaultValue = "" })
                    ) { backStackEntry ->
                        val classId = backStackEntry.arguments?.getString("classId") ?: ""
                        TimetableListScreen(
                            classId = classId,
                            onAddEntryClick = { id -> 
                                navController.navigate(Screen.AddTimetableEntry.createRoute(id))
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = Screen.AddTimetableEntry.route,
                        arguments = listOf(navArgument("classId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val classId = backStackEntry.arguments?.getString("classId") ?: ""
                        AddTimetableEntryScreen(
                            classId = classId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    // Fees
                    composable(Screen.FeeList.route) {
                        FeeScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onAddFeeClick = { navController.navigate(Screen.AddFeeStructure.route) }
                        )
                    }
                    composable(Screen.AddFeeStructure.route) {
                        AddFeeStructureScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    // Students (Admin/Teacher View)
                    composable(Screen.StudentList.route) {
                        StudentListScreen(
                            onStudentClick = { studentId ->
                                navController.navigate(Screen.StudentDetail.createRoute(studentId))
                            },
                            onAddStudentClick = {
                                navController.navigate(Screen.AddStudent.route)
                            },
                             onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.AddStudent.route) {
                        AddStudentScreen(onNavigateBack = { navController.popBackStack() })
                    }
                    composable(
                        route = Screen.StudentDetail.route,
                        arguments = listOf(navArgument("studentId") { type = NavType.StringType })
                    ) {
                        StudentDetailScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    // Teachers
                    composable(Screen.TeacherList.route) {
                        TeacherListScreen(
                            onTeacherClick = { teacherId ->
                                navController.navigate(Screen.TeacherDetail.createRoute(teacherId))
                            },
                            onAddTeacherClick = {
                                navController.navigate(Screen.AddTeacher.route)
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.AddTeacher.route) {
                        AddTeacherScreen(onNavigateBack = { navController.popBackStack() })
                    }
                    composable(
                        route = Screen.TeacherDetail.route,
                        arguments = listOf(navArgument("teacherId") { type = NavType.StringType })
                    ) {
                        TeacherDetailScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    // Classes
                    composable(Screen.ClassList.route) {
                        ClassListScreen(
                            onAddClassClick = { navController.navigate(Screen.AddClass.route) },
                            onClassClick = { classId ->
                                // For admin, maybe show class details or timetable management
                                navController.navigate(Screen.TimetableList.createRoute(classId))
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.AddClass.route) {
                        AddClassScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    // Subjects
                    composable(Screen.SubjectList.route) {
                        SubjectListScreen(
                            onAddSubjectClick = { navController.navigate(Screen.AddSubject.route) },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.AddSubject.route) {
                        AddSubjectScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    // Attendance
                    composable(Screen.AttendanceClassSelect.route) {
                        AttendanceClassSelectScreen(
                            onClassSelected = { classId, subjectId, date ->
                                navController.navigate(Screen.AttendanceMark.createRoute(classId, subjectId, date))
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = Screen.AttendanceMark.route,
                        arguments = listOf(
                            navArgument("classId") { type = NavType.StringType },
                            navArgument("subjectId") { type = NavType.StringType },
                            navArgument("date") { type = NavType.StringType }
                        )
                    ) {
                        AttendanceMarkScreen(onNavigateBack = { navController.popBackStack() })
                    }

                    // Exams & Results
                    composable(Screen.ExamClassSelect.route) {
                        ExamClassSelectScreen(
                            onClassSelected = { classId ->
                                navController.navigate(Screen.ExamList.createRoute(classId))
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = Screen.ExamList.route,
                        arguments = listOf(navArgument("classId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val classId = backStackEntry.arguments?.getString("classId") ?: ""
                        ExamListScreen(
                            classId = classId,
                            onAddExamClick = { id ->
                                navController.navigate(Screen.AddExam.createRoute(id))
                            },
                            onMarkEntryClick = { examId ->
                                navController.navigate(Screen.MarkEntry.createRoute(examId))
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = Screen.AddExam.route,
                        arguments = listOf(navArgument("classId") { type = NavType.StringType })
                    ) {
                        AddExamScreen(onNavigateBack = { navController.popBackStack() })
                    }
                    composable(
                        route = Screen.MarkEntry.route,
                        arguments = listOf(navArgument("examId") { type = NavType.StringType })
                    ) {
                        MarkEntryScreen(onNavigateBack = { navController.popBackStack() })
                    }
                    
                    // Announcements
                    composable(Screen.NotificationList.route) {
                        NotificationListScreen(
                            onAddAnnouncementClick = {
                                navController.navigate(Screen.AddNotification.route)
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.AddNotification.route) {
                        AddAnnouncementScreen(onNavigateBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }

    private fun updateLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
