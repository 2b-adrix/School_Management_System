package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    val id: String,
    val schoolId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val qualification: String,
    val joinDate: String,
    val profileImageUrl: String? = null
)
