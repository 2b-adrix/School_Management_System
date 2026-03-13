package com.example.schoolmanagementsystem.ui.student

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.Student
import com.example.schoolmanagementsystem.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.domain.service.PdfService
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class StudentDetailViewModel @Inject constructor(
    private val repository: StudentRepository,
    private val pdfService: PdfService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Student>>(Resource.Loading())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val studentId: String? = savedStateHandle["studentId"]

    init {
        loadStudent()
    }

    fun loadStudent() {
        studentId?.let { id ->
            viewModelScope.launch {
                _state.value = Resource.Loading()
                _state.value = repository.getStudentById(id)
            }
        }
    }

    fun deleteStudent() {
        viewModelScope.launch {
            val currentStudent = (state.value as? Resource.Success)?.data
            currentStudent?.let { student ->
                val result = repository.deleteStudent(student)
                if (result is Resource.Success) {
                    _eventFlow.emit(UiEvent.DeleteSuccess)
                } else {
                    _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to delete"))
                }
            }
        }
    }

    fun downloadProfile(context: Context) {
        val student = (state.value as? Resource.Success)?.data ?: return
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.ShowSnackbar("Generating Profile PDF..."))
            // Using the result PDF generator as a placeholder for full profile PDF
            val path = pdfService.generateResultPdf(context, student, emptyList())
            if (path != null) {
                openPdf(context, path)
            } else {
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to generate PDF"))
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
        context.startActivity(Intent.createChooser(intent, "Open Profile PDF"))
    }

    sealed class UiEvent {
        object DeleteSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
