package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FeeStructure(
    val id: String,
    val schoolId: String,
    val classId: String,
    val feeName: String,
    val amount: Double,
    val dueDate: String? = null,
    val description: String? = null
)
