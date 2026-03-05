package com.example.schoolmanagementsystem.ui.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.Teacher
import com.example.schoolmanagementsystem.domain.repository.StorageRepository
import com.example.schoolmanagementsystem.domain.repository.TeacherRepository
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
class AddTeacherViewModel @Inject constructor(
    private val repository: TeacherRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState = _saveState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun saveTeacher(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        subjects: String,
        assignedClasses: String,
        imageBytes: ByteArray? = null
    ) {
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Please fill required fields"))
            }
            return
        }

        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            
            var imageUrl: String? = null
            val teacherId = UUID.randomUUID().toString()
            
            if (imageBytes != null) {
                val imageResult = storageRepository.uploadProfileImage(
                    path = "teachers/$teacherId.jpg",
                    imageBytes = imageBytes
                )
                if (imageResult is Resource.Success) {
                    imageUrl = storageRepository.getPublicUrl("teachers/$teacherId.jpg")
                }
            }

            val teacher = Teacher(
                id = teacherId,
                firstName = firstName,
                lastName = lastName,
                email = email,
                phoneNumber = phoneNumber,
                subjects = subjects.split(",").map { it.trim() },
                assignedClasses = assignedClasses.split(",").map { it.trim() },
                profileImageUrl = imageUrl
            )

            val result = repository.addTeacher(teacher)
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
