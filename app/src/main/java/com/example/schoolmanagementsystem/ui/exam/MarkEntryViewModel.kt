package com.example.schoolmanagementsystem.ui.exam

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.Result
import com.example.schoolmanagementsystem.domain.model.Student
import com.example.schoolmanagementsystem.domain.model.Exam
import com.example.schoolmanagementsystem.domain.repository.ExamRepository
import com.example.schoolmanagementsystem.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MarkEntryViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val studentRepository: StudentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val examId: String = checkNotNull(savedStateHandle["examId"])

    private val _state = MutableStateFlow(MarkEntryState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            // Note: In a full implementation, we'd fetch the specific Exam first to get its classId and subjectId.
            // For now, fetching all students and filtering by class would be ideal if we had classId.
            
            studentRepository.getAllStudents().collect { studentRes ->
                if (studentRes is Resource.Success) {
                    val allStudents = studentRes.data ?: emptyList()
                    
                    examRepository.getResultsByExam(examId).collect { resultsRes ->
                        if (resultsRes is Resource.Success) {
                            val existingResults = resultsRes.data ?: emptyList()
                            val markMap = existingResults.associate { it.studentId to it.marksObtained.toString() }
                            
                            // We need subjectId for the Result object. 
                            // Since we don't have the Exam object handy, we'll try to find it from the first result or use a placeholder
                            val subjectId = existingResults.firstOrNull()?.subjectId ?: "placeholder_subject"
                            
                            _state.value = _state.value.copy(
                                students = allStudents, // Ideally filtered by class
                                marks = markMap,
                                subjectId = subjectId,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun onMarkChange(studentId: String, mark: String) {
        val newMarks = _state.value.marks.toMutableMap()
        newMarks[studentId] = mark
        _state.value = _state.value.copy(marks = newMarks)
    }

    fun saveMarks() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val marks = _state.value.marks
            val subjectId = _state.value.subjectId
            
            marks.forEach { (studentId, markStr) ->
                val mark = markStr.toIntOrNull() ?: 0
                val result = Result(
                    id = UUID.randomUUID().toString(),
                    examId = examId,
                    studentId = studentId,
                    subjectId = subjectId,
                    marksObtained = mark,
                    grade = calculateGrade(mark.toDouble()),
                    remarks = ""
                )
                examRepository.addResult(result)
            }
            
            _state.value = _state.value.copy(isSaving = false)
            _eventFlow.emit(UiEvent.SaveSuccess)
        }
    }

    private fun calculateGrade(mark: Double): String {
        return when {
            mark >= 90 -> "A+"
            mark >= 80 -> "A"
            mark >= 70 -> "B"
            mark >= 60 -> "C"
            mark >= 50 -> "D"
            else -> "F"
        }
    }

    data class MarkEntryState(
        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val students: List<Student> = emptyList(),
        val marks: Map<String, String> = emptyMap(),
        val subjectId: String = ""
    )

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
