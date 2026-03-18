package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("email")
    val email: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("qualification")
    val qualification: String,
    @SerialName("join_date")
    val joinDate: String,
    @SerialName("subjects")
    val subjects: List<String> = emptyList(),
    @SerialName("assigned_classes")
    val assignedClasses: List<String> = emptyList(),
    @SerialName("profile_image_url")
    val profileImageUrl: String? = null
)
