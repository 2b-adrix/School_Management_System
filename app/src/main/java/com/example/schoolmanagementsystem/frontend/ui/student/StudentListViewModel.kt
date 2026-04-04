package com.example.schoolmanagementsystem.frontend.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.model.Student
import com.example.schoolmanagementsystem.backend.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentListViewModel @Inject constructor(
    private val repository: StudentRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<Student>>>(Resource.Loading())
    val state = _state.asStateFlow()

    init {
        getStudents()
    }

    fun refresh() {
        getStudents()
    }

    private fun getStudents() {
        repository.getAllStudents().onEach { result ->
            _state.value = result
        }.launchIn(viewModelScope)
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            val result = repository.deleteStudent(student)
            if (result is Resource.Success) {
                getStudents()
            }
        }
    }
}

