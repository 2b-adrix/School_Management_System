package com.example.schoolmanagementsystem.ui.notification

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state = _state.asStateFlow()

    data class NotificationState(
        val announcements: List<AnnouncementItem> = listOf(
            AnnouncementItem(
                "Fees Structure of session - 2025 - 2026",
                "Notice Regarding School Fee For the Session - 2025 - 202610 April 2025Dear Parents/Students,Please clear up the 1st Instalment of school fees and",
                "a year ago"
            ),
            AnnouncementItem(
                "Rangoli Competition, Art and Craft Exhibition & Food Fest",
                "Hi All, On Account of Children\\'s Day celebration(14th-Nov-2024) ,there will be Art & Craft exhibition, Rangoli Competition & Food Fest . Int",
                "a year ago"
            ),
            AnnouncementItem(
                "Notice Regarding (SOF) OLYMPIAD EXAM & Registration Details - 2024 - 2025",
                "Dear Parent / Students,The Science Olympiad Foundation (SOF) Examination will be held as per the following schedule.1. International",
                "a year ago"
            )
        )
    )

    data class AnnouncementItem(
        val title: String,
        val content: String,
        val time: String
    )
}
