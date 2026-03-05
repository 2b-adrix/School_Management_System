package com.example.schoolmanagementsystem.ui.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.Teacher
import com.example.schoolmanagementsystem.domain.repository.TeacherRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TeacherListViewModel @Inject constructor(
    private val repository: TeacherRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<Teacher>>>(Resource.Loading())
    val state = _state.asStateFlow()

    init {
        getTeachers()
    }

    fun getTeachers() {
        repository.getAllTeachers().onEach { result ->
            _state.value = result
        }.launchIn(viewModelScope)
    }
}
