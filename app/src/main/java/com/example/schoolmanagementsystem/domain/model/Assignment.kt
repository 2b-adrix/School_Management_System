package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Assignment(
    val id: String,
    val schoolId: String,
    val title: String,
    val description: String,
    val subject: String,
    val classId: String,
    val dueDate: String?,
    val submissionType: String = "Offline",
    val status: String? = "Pending",
    val teacherId: String
)
