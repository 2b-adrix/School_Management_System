package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SchoolClass(
    val id: String,
    val schoolId: String,
    val name: String,
    val section: String,
    val roomNumber: String? = null,
    val classTeacherId: String? = null
)
