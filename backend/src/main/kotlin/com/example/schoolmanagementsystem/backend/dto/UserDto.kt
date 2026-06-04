package com.example.schoolmanagementsystem.backend.dto

import com.example.schoolmanagementsystem.backend.model.UserRole

data class UserDto(
    val id: String?,
    val name: String,
    val email: String,
    val role: UserRole,
    val schoolId: String
)

data class AuthResponse(
    val token: String,
    val user: UserDto
)
