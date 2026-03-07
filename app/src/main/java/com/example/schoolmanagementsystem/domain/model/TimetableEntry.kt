package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TimetableEntry(
    val id: String,
    val schoolId: String,
    val classId: String,
    val subjectId: String,
    val teacherId: String,
    val dayOfWeek: Int, // 0 = Monday, 1 = Tuesday, etc.
    val startTime: String, // e.g., 09:00 AM
    val endTime: String,   // e.g., 10:00 AM
    val roomNumber: String? = null
)
