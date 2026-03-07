package com.example.schoolmanagementsystem.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.TimetableEntry
import com.example.schoolmanagementsystem.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.domain.repository.TimetableRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTimetableEntryViewModel @Inject constructor(
    private val repository: TimetableRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState = _saveState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun saveTimetableEntry(
        classId: String,
        subjectId: String,
        teacherId: String,
        dayOfWeek: Int,
        startTime: String,
        endTime: String,
        roomNumber: String?
    ) {
        if (classId.isBlank() || subjectId.isBlank() || teacherId.isBlank() || startTime.isBlank() || endTime.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("All required fields must be filled"))
            }
            return
        }

        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            val user = authRepository.getCurrentUser().firstOrNull()
            
            val entry = TimetableEntry(
                id = UUID.randomUUID().toString(),
                schoolId = user?.schoolId ?: "",
                classId = classId,
                subjectId = subjectId,
                teacherId = teacherId,
                dayOfWeek = dayOfWeek,
                startTime = startTime,
                endTime = endTime,
                roomNumber = roomNumber
            )

            val result = repository.addTimetableEntry(entry)
            _saveState.value = result
            if (result is Resource.Success) {
                _eventFlow.emit(UiEvent.SaveSuccess)
            } else if (result is Resource.Error) {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to save"))
            }
        }
    }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
