package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimetableEntry(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("class_id")
    val classId: String,
    @SerialName("subject_id")
    val subjectId: String,
    @SerialName("teacher_id")
    val teacherId: String,
    @SerialName("day_of_week")
    val dayOfWeek: Int, // 0 = Monday, 1 = Tuesday, etc.
    @SerialName("start_time")
    val startTime: String, // e.g., 09:00 AM
    @SerialName("end_time")
    val endTime: String,   // e.g., 10:00 AM
    @SerialName("room_number")
    val roomNumber: String? = null
)
