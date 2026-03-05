package com.example.schoolmanagementsystem.ui.schoolclass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.SchoolClass
import com.example.schoolmanagementsystem.domain.repository.ClassRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ClassListViewModel @Inject constructor(
    private val repository: ClassRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<SchoolClass>>>(Resource.Loading())
    val state = _state.asStateFlow()

    init {
        getClasses()
    }

    fun getClasses() {
        repository.getAllClasses().onEach { result ->
            _state.value = result
        }.launchIn(viewModelScope)
    }
}
