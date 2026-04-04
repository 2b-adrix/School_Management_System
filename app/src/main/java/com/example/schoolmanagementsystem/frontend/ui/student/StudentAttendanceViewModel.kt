package com.example.schoolmanagementsystem.frontend.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.model.AttendanceRecord
import com.example.schoolmanagementsystem.backend.domain.repository.AttendanceRepository
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentAttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudentAttendanceState())
    val state = _state.asStateFlow()

    init {
        loadAttendance()
    }

    private fun loadAttendance() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val user = authRepository.getCurrentUser().firstOrNull()
            if (user != null) {
                attendanceRepository.getAttendanceForStudent(user.id).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            val records = result.data ?: emptyList()
                            val total = records.size
                            val present = records.count { it.isPresent }
                            val percentage = if (total > 0) (present.toFloat() / total * 100) else 0f
                            
                            _state.update { it.copy(
                                isLoading = false,
                                attendanceRecords = records.sortedByDescending { r -> r.date },
                                totalClasses = total,
                                presentCount = present,
                                attendancePercentage = percentage
                            ) }
                        }
                        is Resource.Error -> {
                            _state.update { it.copy(isLoading = false, error = result.message) }
                        }
                        is Resource.Loading -> {
                            _state.update { it.copy(isLoading = true) }
                        }
                    }
                }.launchIn(viewModelScope)
            } else {
                _state.update { it.copy(isLoading = false, error = "User not found") }
            }
        }
    }

    data class StudentAttendanceState(
        val isLoading: Boolean = false,
        val attendanceRecords: List<AttendanceRecord> = emptyList(),
        val attendancePercentage: Float = 0f,
        val presentCount: Int = 0,
        val totalClasses: Int = 0,
        val error: String? = null
    )
}

