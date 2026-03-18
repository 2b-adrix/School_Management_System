package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    @SerialName("SUPER_ADMIN") SUPER_ADMIN,
    @SerialName("SCHOOL_ADMIN") SCHOOL_ADMIN,
    @SerialName("TEACHER") TEACHER,
    @SerialName("STUDENT") STUDENT
}

@Serializable
data class User(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("email")
    val email: String,
    @SerialName("role")
    val role: UserRole,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("profile_image_url")
    val profileImageUrl: String? = null
)
