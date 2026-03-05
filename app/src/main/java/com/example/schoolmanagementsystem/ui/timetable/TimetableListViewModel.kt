package com.example.schoolmanagementsystem.ui.timetable

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.TimetableEntry
import com.example.schoolmanagementsystem.domain.repository.TimetableRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TimetableListViewModel @Inject constructor(
    private val repository: TimetableRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<TimetableEntry>>>(Resource.Loading())
    val state = _state.asStateFlow()

    private val classId: String? = savedStateHandle["classId"]

    init {
        getTimetable()
    }

    fun getTimetable() {
        classId?.let { id ->
            repository.getTimetableForClass(id).onEach { result ->
                _state.value = result
            }.launchIn(viewModelScope)
        } ?: run {
            _state.value = Resource.Error("Class ID not found")
        }
    }
}
