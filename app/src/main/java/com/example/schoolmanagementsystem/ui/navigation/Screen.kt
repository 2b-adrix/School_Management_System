package com.example.schoolmanagementsystem.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object AdminPortal : Screen("admin_portal")

    // Student Management
    object StudentList : Screen("student_list")
    object AddStudent : Screen("add_student")
    object StudentDetail : Screen("student_detail/{studentId}") {
        fun createRoute(studentId: String) = "student_detail/$studentId"
    }

    // Teacher Management
    object TeacherList : Screen("teacher_list")
    object AddTeacher : Screen("add_teacher")
    object TeacherDetail : Screen("teacher_detail/{teacherId}") {
        fun createRoute(teacherId: String) = "teacher_detail/$teacherId"
    }

    // Class Management
    object ClassList : Screen("class_list")
    object AddClass : Screen("add_class")

    // Subject Management
    object SubjectList : Screen("subject_list")
    object AddSubject : Screen("add_subject")

    // Attendance
    object AttendanceClassSelect : Screen("attendance_class_select")
    object AttendanceMark : Screen("attendance_mark/{classId}/{subjectId}/{date}") {
        fun createRoute(classId: String, subjectId: String, date: String) = "attendance_mark/$classId/$subjectId/$date"
    }

    // Exams & Results
    object ExamClassSelect : Screen("exam_class_select")
    object ExamList : Screen("exam_list/{classId}") {
        fun createRoute(classId: String) = "exam_list/$classId"
    }
    object AddExam : Screen("add_exam/{classId}") {
        fun createRoute(classId: String) = "add_exam/$classId"
    }
    object MarkEntry : Screen("mark_entry/{examId}") {
        fun createRoute(examId: String) = "mark_entry/$examId"
    }

    // Fees
    object FeeList : Screen("fee_list")
    object AddFeeStructure : Screen("add_fee_structure")
    object FeePaymentList : Screen("fee_payment_list/{studentId}") {
        fun createRoute(studentId: String) = "fee_payment_list/$studentId"
    }
    object AddFeePayment : Screen("add_fee_payment/{studentId}") {
        fun createRoute(studentId: String) = "add_fee_payment/$studentId"
    }

    // Timetable
    object TimetableList : Screen("timetable_list/{classId}") {
        fun createRoute(classId: String) = "timetable_list/$classId"
    }
    object AddTimetableEntry : Screen("add_timetable_entry/{classId}") {
        fun createRoute(classId: String) = "add_timetable_entry/$classId"
    }

    // Notifications & Announcements
    object NotificationList : Screen("notification_list")
    object AddNotification : Screen("add_notification")

    // New Screens from Reference
    object Messages : Screen("messages")
    object MyClass : Screen("my_class")
    object Events : Screen("events")
    object Me : Screen("me")
    object Profile : Screen("profile")
    object Gallery : Screen("gallery")
    object Assignments : Screen("assignments")
    object Library : Screen("library")
    object LibrarySearch : Screen("library_search")
}
