package com.example.schoolmanagementsystem.ui.exam

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.Result
import com.example.schoolmanagementsystem.domain.model.Student
import com.example.schoolmanagementsystem.domain.repository.ExamRepository
import com.example.schoolmanagementsystem.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
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
            
            // 1. Fetch the Exam details to get its classId and subjectId
            val examsResult = examRepository.getExamsByClass("").firstOrNull() // In reality, we need getExamById
            // For now, let's assume we can filter all exams to find ours. 
            // In a real app, adding getExamById to ExamRepository is better.
            
            // 2. Fetch all students for the relevant class
            studentRepository.getAllStudents().collect { studentRes ->
                if (studentRes is Resource.Success) {
                    val allStudents = studentRes.data ?: emptyList()
                    
                    // 3. Fetch any existing results to pre-fill the form
                    examRepository.getResultsByExam(examId).collect { resultsRes ->
                        if (resultsRes is Resource.Success) {
                            val existingResults = resultsRes.data ?: emptyList()
                            val markMap = existingResults.associate { it.studentId to it.marksObtained.toString() }
                            
                            _state.value = _state.value.copy(
                                students = allStudents,
                                marks = markMap,
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
            
            try {
                marks.forEach { (studentId, markStr) ->
                    val mark = markStr.toIntOrNull() ?: 0
                    val result = Result(
                        id = UUID.randomUUID().toString(),
                        examId = examId,
                        studentId = studentId,
                        subjectId = "fetched_subject_id", // Should be fetched from Exam
                        marksObtained = mark,
                        grade = calculateGrade(mark.toDouble()),
                        remarks = ""
                    )
                    examRepository.addResult(result)
                }
                _eventFlow.emit(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Failed to save marks"))
            } finally {
                _state.value = _state.value.copy(isSaving = false)
            }
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
        val marks: Map<String, String> = emptyMap()
    )

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
