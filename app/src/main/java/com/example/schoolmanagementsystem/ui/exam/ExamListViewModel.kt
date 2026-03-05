package com.example.schoolmanagementsystem.ui.exam

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.Exam
import com.example.schoolmanagementsystem.domain.repository.ExamRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ExamListViewModel @Inject constructor(
    private val repository: ExamRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val classId: String? = savedStateHandle["classId"]

    private val _state = MutableStateFlow<Resource<List<Exam>>>(Resource.Loading())
    val state = _state.asStateFlow()

    init {
        getExams()
    }

    fun getExams() {
        classId?.let { id ->
            repository.getExamsByClass(id).onEach { result ->
                _state.value = result
            }.launchIn(viewModelScope)
        }
    }
}
