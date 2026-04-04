package com.example.schoolmanagementsystem.frontend.ui.teacher

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.model.Teacher
import com.example.schoolmanagementsystem.backend.domain.repository.TeacherRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherDetailViewModel @Inject constructor(
    private val repository: TeacherRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Teacher>>(Resource.Loading())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val teacherId: String? = savedStateHandle["teacherId"]

    init {
        loadTeacher()
    }

    fun loadTeacher() {
        teacherId?.let { id ->
            viewModelScope.launch {
                _state.value = Resource.Loading()
                _state.value = repository.getTeacherById(id)
            }
        }
    }

    fun deleteTeacher() {
        viewModelScope.launch {
            val currentTeacher = (state.value as? Resource.Success)?.data
            currentTeacher?.let { teacher ->
                val result = repository.deleteTeacher(teacher)
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

