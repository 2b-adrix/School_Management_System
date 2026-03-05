package com.example.schoolmanagementsystem.domain.model

enum class UserRole {
    ADMIN, TEACHER, STUDENT
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val profileImageUrl: String? = null
)
