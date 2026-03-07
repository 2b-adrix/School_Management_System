package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Exam(
    val id: String,
    val schoolId: String,
    val classId: String,
    val title: String,
    val description: String? = null,
    val subjectId: String,
    val date: String,
    val totalMarks: Int,
    val createdBy: String
)
