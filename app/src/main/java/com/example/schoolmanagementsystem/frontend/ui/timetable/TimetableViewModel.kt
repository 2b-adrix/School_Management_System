package com.example.schoolmanagementsystem.frontend.ui.timetable

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.repository.TimetableRepository
import com.example.schoolmanagementsystem.backend.domain.repository.SubjectRepository
import com.example.schoolmanagementsystem.backend.domain.repository.TeacherRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import com.example.schoolmanagementsystem.backend.domain.model.TimetableEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val timetableRepository: TimetableRepository,
    private val subjectRepository: SubjectRepository,
    private val teacherRepository: TeacherRepository,
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
            combine(
                timetableRepository.getTimetableForClass(id),
                subjectRepository.getAllSubjects(),
                teacherRepository.getAllTeachers()
            ) { timetableRes, subjectsRes, teachersRes ->
                if (timetableRes is Resource.Loading || subjectsRes is Resource.Loading || teachersRes is Resource.Loading) {
                    _state.value = _state.value.copy(isLoading = true)
                    return@combine
                }

                if (timetableRes is Resource.Success && subjectsRes is Resource.Success && teachersRes is Resource.Success) {
                    val subjects = subjectsRes.data?.associateBy { it.id } ?: emptyMap()
                    val teachers = teachersRes.data?.associateBy { it.id } ?: emptyMap()
                    
                    val displayEntries = timetableRes.data?.map { entry ->
                        TimetableDisplayEntry(
                            entry = entry,
                            subjectName = subjects[entry.subjectId]?.name ?: "Unknown Subject",
                            teacherName = teachers[entry.teacherId]?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Teacher"
                        )
                    } ?: emptyList()

                    val schedule = displayEntries.groupBy { it.entry.dayOfWeek }
                    _state.value = _state.value.copy(
                        schedule = schedule,
                        isLoading = false,
                        isRefreshing = false,
                        error = null
                    )
                } else {
                    val errorMessage = (timetableRes as? Resource.Error)?.message 
                        ?: (subjectsRes as? Resource.Error)?.message 
                        ?: (teachersRes as? Resource.Error)?.message 
                        ?: "Failed to load timetable"
                    
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = errorMessage
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    data class TimetableState(
        val selectedDay: Int = 0,
        val schedule: Map<Int, List<TimetableDisplayEntry>> = emptyMap(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val error: String? = null
    )

    data class TimetableDisplayEntry(
        val entry: TimetableEntry,
        val subjectName: String,
        val teacherName: String
    )

    fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadTimetable()
        // The loadTimetable combine flow will eventually set isLoading to false, 
        // but we should also ensure isRefreshing is reset.
        // Since loadTimetable uses a flow, we can't easily wait for completion here 
        // without changing loadTimetable. 
        // Actually, the combine flow in loadTimetable updates the state.
    }

    fun onDaySelected(index: Int) {
        _state.value = _state.value.copy(selectedDay = index)
    }
}

