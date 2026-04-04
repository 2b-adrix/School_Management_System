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
                        loadAIInsight(user.id)
                    }
                }
            }
            .launchIn(viewModelScope)
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

    private fun loadAIInsight(studentId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isAILoading = true) }
            // Simulate attendance percentage for AI demo
            val result = aiService.getAttendanceInsight(85.0f)
            if (result is Resource.Success) {
                _state.update { it.copy(aiInsight = result.data ?: "", isAILoading = false) }
            } else {
                _state.update { it.copy(aiInsight = "Focus on your studies to achieve elite results!", isAILoading = false) }
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

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    data class DashboardState(
        val isLoading: Boolean = true,
        val isAILoading: Boolean = false,
        val aiInsight: String = "",
        val user: User? = null,
        val studentCount: Int = 0,
        val teacherCount: Int = 0,
        val classCount: Int = 0,
        val userName: String = "Loading...",
        val userSubtitle: String = "",
        val notices: List<Notice> = emptyList(),
        val studentClassId: String = "",
        val feeDuesAmount: Double = 0.0
    )

    data class Notice(
        val id: String,
        val title: String,
        val subtitle: String,
        val date: String
    )

    sealed class UiEvent {
        object LogoutSuccess : UiEvent()
    }
}

