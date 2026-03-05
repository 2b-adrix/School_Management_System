package com.example.schoolmanagementsystem.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.Student
import com.example.schoolmanagementsystem.domain.repository.StorageRepository
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
class AddStudentViewModel @Inject constructor(
    private val repository: StudentRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState = _saveState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun saveStudent(
        firstName: String,
        lastName: String,
        rollNumber: String,
        classId: String,
        className: String,
        parentName: String,
        parentContact: String,
        address: String,
        dob: String,
        imageBytes: ByteArray? = null
    ) {
        if (firstName.isBlank() || lastName.isBlank() || rollNumber.isBlank() || classId.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Please fill required fields"))
            }
            return
        }

        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            
            var imageUrl: String? = null
            val studentId = UUID.randomUUID().toString()
            
            if (imageBytes != null) {
                val imageResult = storageRepository.uploadProfileImage(
                    path = "students/$studentId.jpg",
                    imageBytes = imageBytes
                )
                if (imageResult is Resource.Success) {
                    imageUrl = storageRepository.getPublicUrl("students/$studentId.jpg")
                }
            }

            val student = Student(
                id = studentId,
                firstName = firstName,
                lastName = lastName,
                rollNumber = rollNumber,
                classId = classId,
                className = className,
                parentName = parentName,
                parentContact = parentContact,
                address = address,
                dateOfBirth = dob,
                profileImageUrl = imageUrl
            )

            val result = repository.addStudent(student)
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
