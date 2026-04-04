package com.example.schoolmanagementsystem.frontend.ui.myclass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.repository.AttendanceRepository
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.backend.domain.repository.SubjectRepository
import com.example.schoolmanagementsystem.backend.domain.repository.TimetableRepository
import com.example.schoolmanagementsystem.backend.domain.service.GenerativeAIService
import com.example.schoolmanagementsystem.backend.domain.util.Resource
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
    private val timetableRepository: TimetableRepository,
    private val aiService: GenerativeAIService
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

                // 1. Load Attendance + AI Insight
                attendanceRepository.getAttendanceForStudent(user.id).onEach { result ->
                    if (result is Resource.Success) {
                        val records = result.data ?: emptyList()
                        val total = records.size
                        val present = records.count { it.isPresent }
                        val percentage = if (total > 0) (present.toFloat() / total * 100) else 0f
                        _state.update { it.copy(attendancePercentage = "${percentage.toInt()}%") }
                        
                        // Get AI Insight for attendance
                        val insight = aiService.getAttendanceInsight(percentage)
                        if (insight is Resource.Success) {
                            _state.update { it.copy(attendanceInsight = insight.data ?: "") }
                        }
                    }
                }.launchIn(viewModelScope)

                // 2. Load Subjects
                subjectRepository.getAllSubjects().onEach { result ->
                    if (result is Resource.Success) {
                        val filteredCount = result.data?.size ?: 0
                        _state.update { it.copy(subjectsCount = filteredCount) }
                    }
                }.launchIn(viewModelScope)

                // 3. Load Timetable + Priority Insight
                timetableRepository.getTimetableForClass(classId).onEach { result ->
                    if (result is Resource.Success) {
                        val timetable = result.data ?: emptyList()
                        _state.update { it.copy(timetableClasses = "${timetable.size} classes") }
                        
                        // Get AI Insight for today's classes
                        val classNames = timetable.map { it.subjectId } // Simplified for now
                        val priority = aiService.getImportantClassInsight(classNames)
                        if (priority is Resource.Success) {
                            _state.update { it.copy(timetableInsight = priority.data ?: "") }
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    data class MyClassState(
        val attendancePercentage: String = "0%",
        val attendanceInsight: String = "Loading insight...",
        val timetableClasses: String = "0 classes",
        val timetableInsight: String = "Analyzing your day...",
        val subjectsCount: Int = 0
    )
}

