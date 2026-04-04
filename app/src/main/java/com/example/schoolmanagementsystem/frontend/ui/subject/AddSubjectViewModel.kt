package com.example.schoolmanagementsystem.frontend.ui.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.model.Subject
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.repository.SubjectRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
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
class AddSubjectViewModel @Inject constructor(
    private val repository: SubjectRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState = _saveState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun saveSubject(name: String, code: String, classId: String, description: String) {
        if (name.isBlank() || code.isBlank() || classId.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Name, Code and Class are required"))
            }
            return
        }

        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            val user = authRepository.getCurrentUser().firstOrNull()
            
            val subject = Subject(
                id = UUID.randomUUID().toString(),
                schoolId = user?.schoolId ?: "",
                classId = classId,
                name = name,
                code = code,
                description = description
            )

            val result = repository.addSubject(subject)
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

