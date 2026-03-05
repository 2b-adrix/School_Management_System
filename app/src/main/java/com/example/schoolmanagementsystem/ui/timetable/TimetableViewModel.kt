package com.example.schoolmanagementsystem.ui.timetable

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(TimetableState())
    val state = _state.asStateFlow()

    data class TimetableState(
        val selectedDay: Int = 0,
        val schedule: Map<Int, List<TimetableEntry>> = mapOf(
            0 to listOf(
                TimetableEntry("CT on Roll Period", "Sasmita Sahoo", "07:55 AM - 08:15 AM"),
                TimetableEntry("Physics", "Dipa Singh", "08:15 AM - 08:55 AM"),
                TimetableEntry("Hindi II", "Debashis Mishra", "08:55 AM - 09:35 AM"),
                TimetableEntry("Computer", "Sasmita Sahoo", "09:35 AM - 10:15 AM"),
                TimetableEntry("Break", "", "10:15 AM - 10:35 AM", isBreak = true)
            ),
            1 to listOf(
                TimetableEntry("CT on Roll Period", "Sasmita Sahoo", "07:55 AM - 08:15 AM"),
                TimetableEntry("Hindi", "Debashis Mishra", "08:15 AM - 08:55 AM"),
                TimetableEntry("Odia", "Sharmistha Acharya", "08:55 AM - 09:35 AM"),
                TimetableEntry("English I", "Nithya K", "09:35 AM - 10:15 AM"),
                TimetableEntry("Break", "", "10:15 AM - 10:35 AM", isBreak = true)
            )
        )
    )

    data class TimetableEntry(
        val subject: String,
        val teacher: String,
        val time: String,
        val isBreak: Boolean = false
    )

    fun onDaySelected(index: Int) {
        _state.value = _state.value.copy(selectedDay = index)
    }
}
