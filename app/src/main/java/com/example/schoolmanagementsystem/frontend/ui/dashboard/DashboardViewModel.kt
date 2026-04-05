package com.example.schoolmanagementsystem.frontend.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.model.User
import com.example.schoolmanagementsystem.backend.domain.model.UserRole
import com.example.schoolmanagementsystem.backend.domain.repository.AnnouncementRepository
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.repository.ClassRepository
import com.example.schoolmanagementsystem.backend.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.backend.domain.repository.TeacherRepository
import com.example.schoolmanagementsystem.backend.domain.repository.FeeRepository
import com.example.schoolmanagementsystem.backend.domain.repository.AttendanceRepository
import com.example.schoolmanagementsystem.backend.domain.repository.TimetableRepository
import com.example.schoolmanagementsystem.backend.domain.repository.SubjectRepository
import com.example.schoolmanagementsystem.backend.domain.service.GenerativeAIService
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    private val classRepository: ClassRepository,
    private val announcementRepository: AnnouncementRepository,
    private val feeRepository: FeeRepository,
    private val attendanceRepository: AttendanceRepository,
    private val timetableRepository: TimetableRepository,
    private val subjectRepository: SubjectRepository,
    private val aiService: GenerativeAIService
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        observeUser()
        loadStats()
        loadAnnouncements()
    }

    private fun observeUser() {
        authRepository.getCurrentUser()
            .onEach { user ->
                if (user == null) {
                    _eventFlow.emit(UiEvent.LogoutSuccess)
                } else {
                    _state.update { it.copy(
                        user = user,
                        userName = user.name,
                        userSubtitle = when(user.role) {
                            UserRole.SUPER_ADMIN -> "Super Admin"
                            UserRole.SCHOOL_ADMIN -> "Principal"
                            UserRole.TEACHER -> "Teacher"
                            UserRole.STUDENT -> "Student"
                        }
                    ) }
                    
                    if (user.role == UserRole.STUDENT) {
                        loadStudentData(user.id)
                        loadStudentAttendance(user.id)
                        loadCurrentSession(user.id)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadCurrentSession(studentId: String) {
        viewModelScope.launch {
            val studentRes = studentRepository.getStudentById(studentId)
            if (studentRes is Resource.Success) {
                val classId = studentRes.data?.classId ?: return@launch
                
                combine(
                    timetableRepository.getTimetableForClass(classId),
                    subjectRepository.getAllSubjects()
                ) { timetableRes, subjectsRes ->
                    if (timetableRes is Resource.Success && subjectsRes is Resource.Success) {
                        val calendar = java.util.Calendar.getInstance()
                        val dayOfWeek = (calendar.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7 // Convert to 0=Mon
                        
                        val sdf24 = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                        val sdf12 = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                        val currentTimeStr = sdf24.format(java.util.Date())
                        val currentTime = try { sdf24.parse(currentTimeStr) } catch(e: Exception) { null }
                        
                        val todayEntries = timetableRes.data?.filter { it.dayOfWeek == dayOfWeek } ?: emptyList()
                        val subjects = subjectsRes.data ?: emptyList()
                        
                        val current = todayEntries.find { entry ->
                            try {
                                val startTime = sdf12.parse(entry.startTime)
                                val endTime = sdf12.parse(entry.endTime)
                                if (startTime != null && endTime != null && currentTime != null) {
                                    val start24 = sdf24.format(startTime)
                                    val end24 = sdf24.format(endTime)
                                    currentTimeStr >= start24 && currentTimeStr <= end24
                                } else false
                            } catch (e: Exception) { false }
                        } ?: todayEntries.firstOrNull()

                        current?.let { entry ->
                            val subjectName = subjects.find { it.id == entry.subjectId }?.name ?: "Unknown Subject"
                            _state.update { it.copy(
                                currentSession = CurrentSession(
                                    subject = subjectName,
                                    time = "${entry.startTime} - ${entry.endTime}",
                                    room = entry.roomNumber ?: "Room 101",
                                    isLive = current != null // If we found one matching current time
                                )
                            ) }
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    private fun loadStudentAttendance(studentId: String) {
        attendanceRepository.getAttendanceForStudent(studentId)
            .onEach { result ->
                if (result is Resource.Success) {
                    val records = result.data ?: emptyList()
                    if (records.isNotEmpty()) {
                        val presentCount = records.count { it.isPresent }
                        val percentage = (presentCount.toFloat() / records.size.toFloat())
                        _state.update { it.copy(attendancePercentage = percentage) }
                        loadAIInsight(studentId, percentage)
                    } else {
                        _state.update { it.copy(attendancePercentage = 0f) }
                        loadAIInsight(studentId, 0f)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun loadStudentData(studentId: String) {
        viewModelScope.launch {
            // Get student profile for classId
            val studentRes = studentRepository.getStudentById(studentId)
            if (studentRes is Resource.Success) {
                _state.update { it.copy(studentClassId = studentRes.data?.classId ?: "") }
            }

            // Calculate fees due
            combine(
                feeRepository.getFeeStructures(),
                feeRepository.getPaymentsByStudent(studentId)
            ) { structuresRes, paymentsRes ->
                if (structuresRes is Resource.Success && paymentsRes is Resource.Success) {
                    val totalTarget = structuresRes.data?.filter { it.classId == _state.value.studentClassId }?.sumOf { it.amount } ?: 0.0
                    val totalPaid = paymentsRes.data?.sumOf { it.amountPaid } ?: 0.0
                    val due = totalTarget - totalPaid
                    _state.update { it.copy(feeDuesAmount = due) }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadAIInsight(studentId: String, attendance: Float) {
        viewModelScope.launch {
            _state.update { it.copy(isAILoading = true) }
            val result = aiService.getAttendanceInsight(attendance * 100)
            if (result is Resource.Success) {
                _state.update { it.copy(aiInsight = result.data ?: "", isAILoading = false) }
            } else {
                _state.update { it.copy(aiInsight = "Consistency is the key to academic excellence!", isAILoading = false) }
            }
        }
    }

    private fun loadStats() {
        combine(
            studentRepository.getAllStudents(),
            teacherRepository.getAllTeachers(),
            classRepository.getAllClasses()
        ) { students, teachers, classes ->
            val studentCount = if (students is Resource.Success) students.data?.size ?: 0 else 0
            val teacherCount = if (teachers is Resource.Success) teachers.data?.size ?: 0 else 0
            val classCount = if (classes is Resource.Success) classes.data?.size ?: 0 else 0
            
            _state.update { it.copy(
                studentCount = studentCount,
                teacherCount = teacherCount,
                classCount = classCount,
                isLoading = false
            ) }
        }.launchIn(viewModelScope)
    }

    private fun loadAnnouncements() {
        announcementRepository.getAllAnnouncements().onEach { result ->
            if (result is Resource.Success) {
                val notices = result.data?.take(5)?.map { 
                    Notice(it.id, it.title, it.content, it.createdAt)
                } ?: emptyList()
                _state.update { it.copy(notices = notices) }
            }
        }.launchIn(viewModelScope)
    }

    fun refreshData() {
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            // Re-trigger data loading
            loadStats()
            loadAnnouncements()
            _state.value.user?.let { user ->
                if (user.role == UserRole.STUDENT) {
                    loadStudentData(user.id)
                    loadStudentAttendance(user.id)
                }
            }
            
            // Simulate an academic alert after refresh
            kotlinx.coroutines.delay(800)
            if (_state.value.attendancePercentage < 0.75f) {
                _eventFlow.emit(UiEvent.ShowAcademicAlert(
                    "Attendance Warning",
                    "Your attendance has dropped below 75%. Please attend upcoming classes to avoid eligibility issues."
                ))
            } else {
                _eventFlow.emit(UiEvent.ShowAcademicAlert(
                    "Academic Excellence",
                    "You're performing exceptionally well! Keep up the great work in your current modules."
                ))
            }

            // Wait a bit for smooth UI
            kotlinx.coroutines.delay(200)
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun toggleAIDialog(isOpen: Boolean) {
        _state.update { it.copy(isAIDialogOpen = isOpen) }
        if (isOpen && _state.value.aiInsight.isEmpty()) {
            _state.value.user?.let { loadAIInsight(it.id, _state.value.attendancePercentage) }
        }
    }

    fun askAI(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isAILoading = true) }
            // In a real app, we'd send the query to Gemini.
            // For now, let's use the performance report generator as a general query handler.
            val result = aiService.generatePerformanceReport("User asked: $query. Context: User is ${_state.value.userSubtitle} ${_state.value.userName}")
            if (result is Resource.Success) {
                _state.update { it.copy(aiInsight = result.data ?: "", isAILoading = false) }
            } else {
                _state.update { it.copy(aiInsight = "I'm having trouble connecting to the Siksha Brain. Please try again later.", isAILoading = false) }
            }
        }
    }

    data class DashboardState(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val isAILoading: Boolean = false,
        val aiInsight: String = "",
        val isAIDialogOpen: Boolean = false,
        val user: User? = null,
        val studentCount: Int = 0,
        val teacherCount: Int = 0,
        val classCount: Int = 0,
        val userName: String = "Loading...",
        val userSubtitle: String = "",
        val notices: List<Notice> = emptyList(),
        val studentClassId: String = "",
        val feeDuesAmount: Double = 0.0,
        val attendancePercentage: Float = 0.0f,
        val gpa: Double = 0.0,
        val currentSession: CurrentSession? = null
    )

    data class CurrentSession(
        val subject: String,
        val time: String,
        val room: String,
        val isLive: Boolean = false
    )

    data class Notice(
        val id: String,
        val title: String,
        val subtitle: String,
        val date: String
    )

    sealed class UiEvent {
        object LogoutSuccess : UiEvent()
        data class ShowAcademicAlert(val title: String, val message: String) : UiEvent()
    }
}

