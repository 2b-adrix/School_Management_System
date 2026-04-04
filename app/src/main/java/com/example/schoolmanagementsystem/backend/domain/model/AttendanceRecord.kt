package com.example.schoolmanagementsystem.backend.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRecord(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("student_id")
    val studentId: String,
    @SerialName("class_id")
    val classId: String,
    @SerialName("subject_id")
    val subjectId: String,
    @SerialName("date")
    val date: String,
    @SerialName("is_present")
    val isPresent: Boolean
)

