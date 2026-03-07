package com.example.schoolmanagementsystem.ui.exam

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.Exam
import com.example.schoolmanagementsystem.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.domain.repository.ExamRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddExamViewModel @Inject constructor(
    private val repository: ExamRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val classId: String? = savedStateHandle["classId"]

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState = _saveState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun saveExam(title: String, description: String, subjectId: String, dateString: String, totalMarks: String) {
        if (title.isBlank() || subjectId.isBlank() || dateString.isBlank() || totalMarks.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Please fill all required fields"))
            }
            return
        }

        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            
            val user = authRepository.getCurrentUser().firstOrNull()

            val exam = Exam(
                id = UUID.randomUUID().toString(),
                schoolId = user?.schoolId ?: "",
                classId = classId ?: "",
                title = title,
                description = description,
                subjectId = subjectId,
                date = dateString,
                totalMarks = totalMarks.toIntOrNull() ?: 100,
                createdBy = user?.id ?: ""
            )

            val result = repository.addExam(exam)
            _saveState.value = result
            if (result is Resource.Success) {
                _eventFlow.emit(UiEvent.SaveSuccess)
            } else if (result is Resource.Error) {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to save"))
            }
        }
    }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
