package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val id: String,
    val schoolId: String,
    val examId: String,
    val studentId: String,
    val subjectId: String,
    val marksObtained: Int,
    val grade: String,
    val remarks: String? = null
)
