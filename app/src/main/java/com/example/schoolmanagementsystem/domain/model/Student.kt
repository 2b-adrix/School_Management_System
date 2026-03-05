package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val id: String,
    val firstName: String,
    val lastName: String,
    val rollNumber: String,
    val classId: String,
    val className: String,
    val parentName: String,
    val parentContact: String,
    val address: String,
    val dateOfBirth: String,
    val profileImageUrl: String? = null
)
