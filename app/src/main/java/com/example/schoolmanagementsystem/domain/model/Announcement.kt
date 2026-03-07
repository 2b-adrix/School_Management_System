package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Announcement(
    val id: String,
    val schoolId: String,
    val title: String,
    val content: String,
    val targetRole: String, // ALL, TEACHER, STUDENT, CLASS
    val targetId: String? = null, // Class ID if targetRole is CLASS
    val createdAt: String
)
