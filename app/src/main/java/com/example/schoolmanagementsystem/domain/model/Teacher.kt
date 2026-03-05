package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val subjects: List<String>,
    val assignedClasses: List<String>,
    val profileImageUrl: String? = null
)
