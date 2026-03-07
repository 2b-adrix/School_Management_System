package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    val id: String,
    val schoolId: String,
    val name: String,
    val code: String
)
