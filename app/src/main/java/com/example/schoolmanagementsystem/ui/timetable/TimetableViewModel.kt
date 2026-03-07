package com.example.schoolmanagementsystem.ui.timetable

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.repository.TimetableRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import com.example.schoolmanagementsystem.domain.model.TimetableEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val repository: TimetableRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val classId: String? = savedStateHandle["classId"]

    private val _state = MutableStateFlow(TimetableState())
    val state = _state.asStateFlow()

    init {
        loadTimetable()
    }

    fun loadTimetable() {
        classId?.let { id ->
            repository.getTimetableForClass(id).onEach { result ->
                when (result) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Resource.Success -> {
                        val schedule = result.data?.groupBy { it.dayOfWeek } ?: emptyMap()
                        _state.value = _state.value.copy(
                            schedule = schedule,
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    data class TimetableState(
        val selectedDay: Int = 0,
        val schedule: Map<Int, List<TimetableEntry>> = emptyMap(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    fun onDaySelected(index: Int) {
        _state.value = _state.value.copy(selectedDay = index)
    }
}
