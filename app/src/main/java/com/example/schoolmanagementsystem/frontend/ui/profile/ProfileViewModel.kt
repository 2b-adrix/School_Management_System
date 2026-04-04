package com.example.schoolmanagementsystem.frontend.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser().firstOrNull()
            user?.let {
                _state.value = _state.value.copy(
                    isLoading = true,
                    id = it.id,
                    name = it.name,
                    email = it.email,
                    subtitle = it.role.name
                )
                
                // If it's a student, load student-specific details
                val studentResult = studentRepository.getStudentById(it.id)
                if (studentResult is Resource.Success) {
                    val student = studentResult.data
                    student?.let { s ->
                        _state.value = _state.value.copy(
                            name = "${s.firstName} ${s.lastName}",
                            subtitle = "Student : ${s.className}",
                            className = s.className,
                            admissionNumber = s.rollNumber,
                            guardianName = s.parentName,
                            guardianMobile = s.parentContact,
                            addressHome = s.address,
                            dob = s.dateOfBirth,
                            isLoading = false
                        )
                    }
                } else {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }

    data class ProfileState(
        val isLoading: Boolean = false,
        val id: String = "",
        val name: String = "",
        val subtitle: String = "",
        val admissionNumber: String = "N/A",
        val className: String = "N/A",
        val batch: String = "N/A",
        val admissionDate: String = "N/A",
        val joinedDate: String = "N/A",
        val guardianName: String = "N/A",
        val gender: String = "N/A",
        val dob: String = "N/A",
        val bloodGroup: String = "N/A",
        val birthPlace: String = "N/A",
        val nationality: String = "N/A",
        val religion: String = "N/A",
        val language: String = "N/A",
        val aadharNumber: String = "N/A",
        val pen: String = "N/A",
        val apaarId: String = "N/A",
        val modeOfTransport: String = "N/A",
        val houseName: String = "N/A",
        val height: String = "N/A",
        val weight: String = "N/A",
        val addressHome: String = "N/A",
        val addressCity: String = "N/A",
        val addressState: String = "N/A",
        val addressPin: String = "N/A",
        val phone: String = "N/A",
        val phone2: String = "N/A",
        val guardianMobile: String = "N/A",
        val email: String = ""
    )
}

