package com.example.schoolmanagementsystem.frontend.ui.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.model.Assignment
import com.example.schoolmanagementsystem.backend.domain.model.User
import com.example.schoolmanagementsystem.backend.domain.model.UserRole
import com.example.schoolmanagementsystem.backend.domain.repository.AssignmentRepository
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AssignmentState())
    val state = _state.asStateFlow()

    init {
        observeUser()
        loadAssignments()
    }

    private fun observeUser() {
        authRepository.getCurrentUser()
            .onEach { user -> _state.value = _state.value.copy(user = user) }
            .launchIn(viewModelScope)
    }

    private fun loadAssignments() {
        viewModelScope.launch {
            assignmentRepository.getAllAssignments().collect {
                _state.value = _state.value.copy(assignments = it)
            }
        }
    }

    fun createAssignment(title: String, description: String, dueDate: String, classId: String) {
        // TODO: Implement assignment creation logic
    }

    data class AssignmentState(
        val user: User? = null,
        val assignments: Resource<List<Assignment>> = Resource.Loading()
    )
}

