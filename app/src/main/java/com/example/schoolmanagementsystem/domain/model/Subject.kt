package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("class_id")
    val classId: String,
    @SerialName("name")
    val name: String,
    @SerialName("code")
    val code: String,
    @SerialName("description")
    val description: String? = null
)
