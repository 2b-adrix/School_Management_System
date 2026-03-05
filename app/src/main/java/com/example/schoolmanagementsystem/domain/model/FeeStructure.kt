package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FeeStructure(
    val id: String,
    val className: String,
    val amount: Double,
    val dueDate: String,
    val description: String
)
