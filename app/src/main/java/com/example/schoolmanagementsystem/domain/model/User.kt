package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    SUPER_ADMIN, SCHOOL_ADMIN, TEACHER, STUDENT
}

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val schoolId: String,
    val profileImageUrl: String? = null
)
