package com.example.schoolmanagementsystem.backend.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.google.gson.annotations.SerializedName

@Serializable
enum class UserRole {
    @SerialName("SUPER_ADMIN") SUPER_ADMIN,
    @SerialName("SCHOOL_ADMIN") SCHOOL_ADMIN,
    @SerialName("TEACHER") TEACHER,
    @SerialName("STUDENT") STUDENT
}

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    @SerializedName("schoolId")
    val schoolId: String,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String? = null
)
