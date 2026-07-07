package com.example.schoolmanagementsystem.backend.data.remote

import com.example.schoolmanagementsystem.backend.domain.model.*
import retrofit2.http.*

interface SikshaApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): ApiResponse<AuthResponse>

    @GET("students/profile")
    suspend fun getStudentProfile(): ApiResponse<Student>

    @GET("students")
    suspend fun getAllStudents(): ApiResponse<List<Student>>

    @GET("students/{id}")
    suspend fun getStudentById(@Path("id") id: String): ApiResponse<Student>

    @POST("students")
    suspend fun addStudent(@Body student: Student): ApiResponse<Student>

    @PUT("students/{id}")
    suspend fun updateStudent(@Path("id") id: String, @Body student: Student): ApiResponse<Student>

    @DELETE("students/{id}")
    suspend fun deleteStudent(@Path("id") id: String): ApiResponse<Unit>

    @GET("teachers")
    suspend fun getAllTeachers(): ApiResponse<List<Teacher>>

    @GET("teachers/{id}")
    suspend fun getTeacherById(@Path("id") id: String): ApiResponse<Teacher>

    @POST("teachers")
    suspend fun createTeacher(@Body teacher: Teacher): ApiResponse<Teacher>

    @PUT("teachers/{id}")
    suspend fun updateTeacher(@Path("id") id: String, @Body teacher: Teacher): ApiResponse<Teacher>

    @DELETE("teachers/{id}")
    suspend fun deleteTeacher(@Path("id") id: String): ApiResponse<Unit>

    @POST("attendance")
    suspend fun markAttendance(@Body record: AttendanceRecord): ApiResponse<AttendanceRecord>

    @GET("attendance/student/{studentId}")
    suspend fun getStudentAttendance(@Path("studentId") studentId: String): ApiResponse<List<AttendanceRecord>>

    @GET("attendance/class/{classId}")
    suspend fun getClassAttendance(
        @Path("classId") classId: String,
        @Query("date") date: String
    ): ApiResponse<List<AttendanceRecord>>

    @GET("attendance/student/{studentId}/insight")
    suspend fun getAttendanceInsight(@Path("studentId") studentId: String): ApiResponse<Map<String, String>>

    @GET("announcements")
    suspend fun getAllAnnouncements(): ApiResponse<List<Announcement>>

    @POST("announcements")
    suspend fun createAnnouncement(@Body announcement: Announcement): ApiResponse<Announcement>

    @PUT("announcements/{id}")
    suspend fun updateAnnouncement(@Path("id") id: String, @Body announcement: Announcement): ApiResponse<Announcement>

    @DELETE("announcements/{id}")
    suspend fun deleteAnnouncement(@Path("id") id: String): ApiResponse<Unit>

    @GET("assignments")
    suspend fun getAllAssignments(): ApiResponse<List<Assignment>>

    @POST("assignments")
    suspend fun createAssignment(@Body assignment: Assignment): ApiResponse<Assignment>

    @DELETE("assignments/{id}")
    suspend fun deleteAssignment(@Path("id") id: String): ApiResponse<Unit>

    @GET("classes")
    suspend fun getAllClasses(): ApiResponse<List<SchoolClass>>

    @GET("classes/{id}")
    suspend fun getClassById(@Path("id") id: String): ApiResponse<SchoolClass>

    @POST("classes")
    suspend fun createClass(@Body schoolClass: SchoolClass): ApiResponse<SchoolClass>

    @PUT("classes/{id}")
    suspend fun updateClass(@Path("id") id: String, @Body schoolClass: SchoolClass): ApiResponse<SchoolClass>

    @DELETE("classes/{id}")
    suspend fun deleteClass(@Path("id") id: String): ApiResponse<Unit>

    @GET("exams")
    suspend fun getAllExams(): ApiResponse<List<Exam>>

    @POST("exams")
    suspend fun createExam(@Body exam: Exam): ApiResponse<Exam>

    @PUT("exams/{id}")
    suspend fun updateExam(@Path("id") id: String, @Body exam: Exam): ApiResponse<Exam>

    @DELETE("exams/{id}")
    suspend fun deleteExam(@Path("id") id: String): ApiResponse<Unit>

    @GET("results/student/{studentId}")
    suspend fun getResultsByStudent(@Path("studentId") studentId: String): ApiResponse<List<Result>>

    @POST("results")
    suspend fun addResult(@Body result: Result): ApiResponse<Result>

    @GET("fees/structures")
    suspend fun getFeeStructures(): ApiResponse<List<FeeStructure>>

    @POST("fees/structures")
    suspend fun createFeeStructure(@Body structure: FeeStructure): ApiResponse<FeeStructure>

    @GET("fees/payments/student/{studentId}")
    suspend fun getPaymentsByStudent(@Path("studentId") studentId: String): ApiResponse<List<FeePayment>>

    @POST("fees/payments")
    suspend fun addPayment(@Body payment: FeePayment): ApiResponse<FeePayment>

    @GET("inventory")
    suspend fun getAllInventoryItems(): ApiResponse<List<InventoryItem>>

    @POST("inventory")
    suspend fun addInventoryItem(@Body item: InventoryItem): ApiResponse<InventoryItem>

    @PUT("inventory/{id}")
    suspend fun updateInventoryItem(@Path("id") id: String, @Body item: InventoryItem): ApiResponse<InventoryItem>

    @DELETE("inventory/{id}")
    suspend fun deleteInventoryItem(@Path("id") id: String): ApiResponse<Unit>

    @GET("messages")
    suspend fun getMessagesForUser(): ApiResponse<List<ChatMessage>>

    @POST("messages")
    suspend fun sendMessage(@Body message: ChatMessage): ApiResponse<ChatMessage>

    @GET("salaries")
    suspend fun getAllSalaries(): ApiResponse<List<SalaryRecord>>

    @POST("salaries")
    suspend fun addSalaryRecord(@Body record: SalaryRecord): ApiResponse<SalaryRecord>

    @GET("subjects")
    suspend fun getAllSubjects(): ApiResponse<List<Subject>>

    @POST("subjects")
    suspend fun addSubject(@Body subject: Subject): ApiResponse<Subject>

    @GET("timetable/class/{classId}")
    suspend fun getTimetableForClass(@Path("classId") classId: String): ApiResponse<List<TimetableEntry>>

    @POST("timetable")
    suspend fun addTimetableEntry(@Body entry: TimetableEntry): ApiResponse<TimetableEntry>

    @DELETE("timetable/{id}")
    suspend fun deleteTimetableEntry(@Path("id") id: String): ApiResponse<Unit>
}

data class LoginRequest(val email: String, val password: String)
data class SignUpRequest(
    val email: String, 
    val password: String, 
    val fullName: String, 
    val role: String, 
    val schoolId: String
)
data class AuthResponse(val token: String, val user: User)
