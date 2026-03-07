package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Exam(
    val id: String,
    val schoolId: String,
    val classId: String,
    val name: String,
    val date: String
)
