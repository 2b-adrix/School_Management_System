package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRecord(
    val id: String,
    val schoolId: String,
    val studentId: String,
    val classId: String,
    val subjectId: String,
    val date: String,
    val isPresent: Boolean
)
