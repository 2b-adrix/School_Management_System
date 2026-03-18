package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Student(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("roll_number")
    val rollNumber: String,
    @SerialName("class_id")
    val classId: String,
    @SerialName("class_name")
    val className: String,
    @SerialName("parent_name")
    val parentName: String,
    @SerialName("parent_contact")
    val parentContact: String,
    @SerialName("address")
    val address: String,
    @SerialName("date_of_birth")
    val dateOfBirth: String,
    @SerialName("profile_image_url")
    val profileImageUrl: String? = null
)
