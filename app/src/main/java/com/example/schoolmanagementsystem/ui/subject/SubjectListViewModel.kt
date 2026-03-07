package com.example.schoolmanagementsystem.ui.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.Subject
import com.example.schoolmanagementsystem.domain.repository.SubjectRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectListViewModel @Inject constructor(
    private val repository: SubjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<Subject>>>(Resource.Loading())
    val state = _state.asStateFlow()

    init {
        getSubjects()
    }

    fun getSubjects() {
        repository.getAllSubjects().onEach { result ->
            _state.value = result
        }.launchIn(viewModelScope)
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            val result = repository.deleteSubject(subject)
            if (result is Resource.Success) {
                getSubjects()
            }
        }
    }
}
