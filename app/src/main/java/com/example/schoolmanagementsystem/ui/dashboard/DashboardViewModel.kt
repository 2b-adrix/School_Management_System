package com.example.schoolmanagementsystem.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.User
import com.example.schoolmanagementsystem.domain.model.UserRole
import com.example.schoolmanagementsystem.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.domain.repository.ClassRepository
import com.example.schoolmanagementsystem.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.domain.repository.TeacherRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    private val classRepository: ClassRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        observeUser()
        loadStats()
    }

    private fun observeUser() {
        authRepository.getCurrentUser()
            .onEach { user ->
                if (user == null) {
                    _eventFlow.emit(UiEvent.LogoutSuccess)
                }
                _state.value = _state.value.copy(
                    user = user,
                    userName = user?.name ?: "User",
                    userSubtitle = when(user?.role) {
                        UserRole.ADMIN -> "Principal"
                        UserRole.TEACHER -> "Teacher"
                        UserRole.STUDENT -> "Student"
                        null -> ""
                    }
                )
            }
            .launchIn(viewModelScope)
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
            
            _state.value = _state.value.copy(
                studentCount = studentCount,
                teacherCount = teacherCount,
                classCount = classCount,
                isLoading = false
            )
        }.launchIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    data class DashboardState(
        val isLoading: Boolean = true,
        val user: User? = null,
        val studentCount: Int = 0,
        val teacherCount: Int = 0,
        val classCount: Int = 0,
        val userName: String = "Loading...",
        val userSubtitle: String = "",
        val feeDues: String = "3 dues",
        val timetableClasses: String = "8 classes",
        val eventsCount: String = "22 events",
        val notices: List<Notice> = listOf(
            Notice("Annual Sports Day 2025", "Event", "15 Mar 2025"),
            Notice("Final Examination Schedule Out", "Exam", "10 Mar 2025")
        )
    )

    data class Notice(
        val title: String,
        val subtitle: String,
        val date: String
    )

    sealed class UiEvent {
        object LogoutSuccess : UiEvent()
    }
}
