package com.example.schoolmanagementsystem.backend.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Announcement(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
    @SerialName("target_role")
    val targetRole: String, // ALL, TEACHER, STUDENT, CLASS
    @SerialName("target_id")
    val targetId: String? = null, // Class ID if targetRole is CLASS
    @SerialName("created_at")
    val createdAt: String
)

