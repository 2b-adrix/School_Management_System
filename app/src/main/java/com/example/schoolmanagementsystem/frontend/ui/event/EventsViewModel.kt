package com.example.schoolmanagementsystem.frontend.ui.event

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(EventsState())
    val state = _state.asStateFlow()

    data class EventsState(
        val selectedTab: Int = 0,
        val events: List<EventItem> = listOf(
            EventItem("02", "Mon", "Class Suspended : Nursery to Class VIII", "Holiday", "March 2026", EventType.HOLIDAY),
            EventItem("03", "Tue", "Class Suspended : Nursery to Class VIII", "Holiday", "March 2026", EventType.HOLIDAY),
            EventItem("03", "Tue", "Dola Purnima", "Holiday", "March 2026", EventType.HOLIDAY),
            EventItem("04", "Wed", "Class Suspended : Nursery to Class VIII", "Holiday", "March 2026", EventType.HOLIDAY)
        )
    )

    data class EventItem(
        val day: String,
        val dayName: String,
        val title: String,
        val subtitle: String,
        val monthYear: String,
        val type: EventType
    )

    enum class EventType {
        ALL, FEES, EXAMS, HOLIDAY
    }

    fun onTabSelected(index: Int) {
        _state.value = _state.value.copy(selectedTab = index)
    }
}

