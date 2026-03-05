package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TimetableEntry(
    val id: String,
    val classId: String,
    val subjectId: String,
    val teacherId: String,
    val dayOfWeek: String, // e.g., MONDAY, TUESDAY
    val startTime: String, // e.g., 09:00
    val endTime: String,   // e.g., 10:00
    val roomNumber: String? = null
)
