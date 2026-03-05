package com.example.schoolmanagementsystem.ui.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.Student
import com.example.schoolmanagementsystem.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentDetailViewModel @Inject constructor(
    private val repository: StudentRepository,
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

    sealed class UiEvent {
        object DeleteSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
