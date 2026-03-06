package com.example.schoolmanagementsystem.ui.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.Exam
import com.example.schoolmanagementsystem.domain.model.User
import com.example.schoolmanagementsystem.domain.model.UserRole
import com.example.schoolmanagementsystem.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.domain.repository.ExamRepository
import com.example.schoolmanagementsystem.domain.repository.SubjectRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ExamViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val examRepository: ExamRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExamState())
    val state = _state.asStateFlow()

    init {
        observeUser()
        loadSubjects()
    }

    private fun observeUser() {
        authRepository.getCurrentUser()
            .onEach { user -> _state.value = _state.value.copy(user = user) }
            .launchIn(viewModelScope)
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            subjectRepository.getAllSubjects().collect { result ->
                _state.value = _state.value.copy(subjects = result)
            }
        }
    }

    fun getExamsByClass(classId: String) {
        viewModelScope.launch {
            examRepository.getExamsByClass(classId).collect {
                _state.value = _state.value.copy(exams = it)
            }
        }
    }

    fun addExam(exam: Exam) {
        viewModelScope.launch {
            examRepository.addExam(exam)
        }
    }

    fun updateExam(exam: Exam) {
        viewModelScope.launch {
            examRepository.updateExam(exam)
        }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch {
            examRepository.deleteExam(exam)
        }
    }

    data class ExamState(
        val user: User? = null,
        val subjects: Resource<List<com.example.schoolmanagementsystem.domain.model.Subject>> = Resource.Loading(),
        val exams: Resource<List<Exam>> = Resource.Loading(),
        val selectedSubjectId: String = "",
        val selectedDate: Date = Date()
    )
}
