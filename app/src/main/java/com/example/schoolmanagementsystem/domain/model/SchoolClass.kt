package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SchoolClass(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("name")
    val name: String,
    @SerialName("section")
    val section: String,
    @SerialName("room_number")
    val roomNumber: String? = null,
    @SerialName("class_teacher_id")
    val classTeacherId: String? = null
)
