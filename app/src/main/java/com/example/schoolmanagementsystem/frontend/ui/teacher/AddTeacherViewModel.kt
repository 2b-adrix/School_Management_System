package com.example.schoolmanagementsystem.frontend.ui.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.model.Teacher
import com.example.schoolmanagementsystem.backend.domain.model.UserRole
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.repository.StorageRepository
import com.example.schoolmanagementsystem.backend.domain.repository.TeacherRepository
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
class AddTeacherViewModel @Inject constructor(
    private val repository: TeacherRepository,
    private val authRepository: AuthRepository,
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
        password: String,
        phoneNumber: String,
        qualification: String,
        joinDate: String,
        subjects: String,
        assignedClasses: String,
        imageBytes: ByteArray? = null
    ) {
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Please fill required fields including password"))
            }
            return
        }

        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            
            val adminUser = authRepository.getCurrentUser().firstOrNull()
            val schoolId = adminUser?.schoolId ?: ""

            // 1. Create Auth Account for Teacher
            val authResult = authRepository.signUp(
                email = email,
                password = password,
                role = UserRole.TEACHER,
                fullName = "$firstName $lastName",
                schoolId = schoolId
            )

            if (authResult is Resource.Error) {
                _saveState.value = Resource.Error(authResult.message ?: "Failed to create teacher account")
                _eventFlow.emit(UiEvent.ShowSnackbar(authResult.message ?: "Failed to create teacher account"))
                return@launch
            }

            val teacherId = authResult.data?.id ?: UUID.randomUUID().toString()
            var imageUrl: String? = null
            
            // 2. Upload Image if provided
            if (imageBytes != null) {
                val imageResult = storageRepository.uploadProfileImage(
                    path = "teachers/$teacherId.jpg",
                    imageBytes = imageBytes
                )
                if (imageResult is Resource.Success) {
                    imageUrl = storageRepository.getPublicUrl("teachers/$teacherId.jpg")
                }
            }

            // 3. Save Teacher Details in Database
            val teacher = Teacher(
                id = teacherId,
                schoolId = schoolId,
                firstName = firstName,
                lastName = lastName,
                email = email,
                phoneNumber = phoneNumber,
                qualification = qualification,
                joinDate = joinDate,
                subjects = if (subjects.isBlank()) emptyList() else subjects.split(",").map { it.trim() },
                assignedClasses = if (assignedClasses.isBlank()) emptyList() else assignedClasses.split(",").map { it.trim() },
                profileImageUrl = imageUrl
            )

            val result = repository.addTeacher(teacher)
            _saveState.value = result
            if (result is Resource.Success) {
                _eventFlow.emit(UiEvent.SaveSuccess)
            } else if (result is Resource.Error) {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to save teacher details"))
            }
        }
    }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}

