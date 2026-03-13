package com.example.schoolmanagementsystem.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.AttendanceRecord
import com.example.schoolmanagementsystem.domain.model.SchoolClass
import com.example.schoolmanagementsystem.domain.repository.AttendanceRepository
import com.example.schoolmanagementsystem.domain.repository.ClassRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceReportViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val classRepository: ClassRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AttendanceReportState())
    val state = _state.asStateFlow()

    init {
        loadClasses()
    }

    private fun loadClasses() {
        classRepository.getAllClasses().onEach { result ->
            if (result is Resource.Success) {
                _state.update { it.copy(classes = result.data ?: emptyList()) }
            }
        }.launchIn(viewModelScope)
    }

    fun loadAttendanceReport(classId: String, date: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            attendanceRepository.getAttendanceForClassSubject(classId, "ALL", date).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val records = result.data ?: emptyList()
                        val total = records.size
                        val present = records.count { it.isPresent }
                        val absent = total - present
                        val percentage = if (total > 0) (present.toFloat() / total * 100) else 0f
                        
                        _state.update { it.copy(
                            isLoading = false,
                            records = records,
                            presentCount = present,
                            absentCount = absent,
                            attendancePercentage = percentage
                        ) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    data class AttendanceReportState(
        val isLoading: Boolean = false,
        val classes: List<SchoolClass> = emptyList(),
        val records: List<AttendanceRecord> = emptyList(),
        val presentCount: Int = 0,
        val absentCount: Int = 0,
        val attendancePercentage: Float = 0f,
        val error: String? = null
    )
}
