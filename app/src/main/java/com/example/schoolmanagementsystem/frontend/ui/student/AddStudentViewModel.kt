package com.example.schoolmanagementsystem.frontend.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.model.Student
import com.example.schoolmanagementsystem.backend.domain.model.UserRole
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.repository.StorageRepository
import com.example.schoolmanagementsystem.backend.domain.repository.StudentRepository
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
class AddStudentViewModel @Inject constructor(
    private val repository: StudentRepository,
    private val authRepository: AuthRepository,
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
        email: String,
        password: String,
        imageBytes: ByteArray? = null
    ) {
        if (firstName.isBlank() || lastName.isBlank() || rollNumber.isBlank() || classId.isBlank() || email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Please fill all required fields including login credentials"))
            }
            return
        }

        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            
            val adminUser = authRepository.getCurrentUser().firstOrNull()
            val schoolId = adminUser?.schoolId ?: ""
            
            // 1. Create Auth Account for Student
            val authResult = authRepository.signUp(
                email = email,
                password = password,
                role = UserRole.STUDENT,
                fullName = "$firstName $lastName",
                schoolId = schoolId
            )

            if (authResult is Resource.Error) {
                _saveState.value = Resource.Error(authResult.message ?: "Failed to create student account")
                _eventFlow.emit(UiEvent.ShowSnackbar(authResult.message ?: "Failed to create student account"))
                return@launch
            }

            val studentId = authResult.data?.id ?: UUID.randomUUID().toString()
            var imageUrl: String? = null
            
            // 2. Upload Image if provided
            if (imageBytes != null) {
                val imageResult = storageRepository.uploadProfileImage(
                    path = "students/$studentId.jpg",
                    imageBytes = imageBytes
                )
                if (imageResult is Resource.Success) {
                    imageUrl = storageRepository.getPublicUrl("students/$studentId.jpg")
                }
            }

            // 3. Save Student Details in Database
            val student = Student(
                id = studentId,
                schoolId = schoolId,
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
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to save student details"))
            }
        }
    }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}

