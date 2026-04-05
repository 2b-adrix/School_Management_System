package com.example.schoolmanagementsystem.frontend.ui.exam

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.model.Exam
import com.example.schoolmanagementsystem.backend.domain.model.Student
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.repository.ExamRepository
import com.example.schoolmanagementsystem.backend.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.backend.domain.service.PdfService
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ExamListViewModel @Inject constructor(
    private val repository: ExamRepository,
    private val authRepository: AuthRepository,
    private val studentRepository: StudentRepository,
    private val pdfService: PdfService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val classId: String? = savedStateHandle["classId"]

    private val _state = MutableStateFlow<ExamListState>(ExamListState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getExams()
    }

    fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        getExams()
    }

    fun getExams() {
        classId?.let { id ->
            repository.getExamsByClass(id).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.update { it.copy(
                            exams = result.data ?: emptyList(),
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        ) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.message ?: "An unexpected error occurred"
                        ) }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch {
            val result = repository.deleteExam(exam)
            if (result is Resource.Success) {
                getExams()
            }
        }
    }

    fun downloadReportCard(context: Context, examId: String) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser().firstOrNull() ?: return@launch
            val studentResult = studentRepository.getStudentById(user.id)
            
            if (studentResult is Resource.Success) {
                val student = studentResult.data ?: return@launch
                
                // Fetch results for this specific exam and student
                repository.getResultsByStudent(student.id).onEach { result ->
                    if (result is Resource.Success) {
                        val examResults = result.data?.filter { it.examId == examId } ?: emptyList()
                        
                        _eventFlow.emit(UiEvent.ShowSnackbar("Generating Report Card..."))
                        val path = pdfService.generateResultPdf(context, student, examResults)
                        if (path != null) {
                            openPdf(context, path)
                        } else {
                            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to generate report card"))
                        }
                    }
                }.first() // Take the first result from the flow
            }
        }
    }

    private fun openPdf(context: Context, path: String) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(intent, "Open Report Card"))
    }

    data class ExamListState(
        val exams: List<Exam> = emptyList(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val error: String? = null
    )

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}

