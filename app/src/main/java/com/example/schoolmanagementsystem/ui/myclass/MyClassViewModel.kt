package com.example.schoolmanagementsystem.ui.myclass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.repository.AttendanceRepository
import com.example.schoolmanagementsystem.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.domain.repository.SubjectRepository
import com.example.schoolmanagementsystem.domain.repository.TimetableRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyClassViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val subjectRepository: SubjectRepository,
    private val timetableRepository: TimetableRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MyClassState())
    val state = _state.asStateFlow()

    init {
        loadClassData()
    }

    private fun loadClassData() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser().firstOrNull() ?: return@launch
            val studentRes = studentRepository.getStudentById(user.id)
            
            if (studentRes is Resource.Success) {
                val student = studentRes.data ?: return@launch
                val classId = student.classId

                // 1. Load Attendance
                attendanceRepository.getAttendanceForStudent(user.id).onEach { result ->
                    if (result is Resource.Success) {
                        val records = result.data ?: emptyList()
                        val total = records.size
                        val present = records.count { it.isPresent }
                        val percentage = if (total > 0) (present.toFloat() / total * 100) else 0f
                        _state.update { it.copy(attendancePercentage = "${percentage.toInt()}%") }
                    }
                }.launchIn(viewModelScope)

                // 2. Load Subjects
                subjectRepository.getAllSubjects().onEach { result ->
                    if (result is Resource.Success) {
                        // In a real app, subjects table would have classId. 
                        // For now filtering based on what's available or showing all if generic
                        val filteredCount = result.data?.size ?: 0
                        _state.update { it.copy(subjectsCount = filteredCount) }
                    }
                }.launchIn(viewModelScope)

                // 3. Load Timetable
                timetableRepository.getTimetableForClass(classId).onEach { result ->
                    if (result is Resource.Success) {
                        val count = result.data?.size ?: 0
                        _state.update { it.copy(timetableClasses = "$count classes") }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    data class MyClassState(
        val attendancePercentage: String = "0%",
        val timetableClasses: String = "0 classes",
        val subjectsCount: Int = 0
    )
}
