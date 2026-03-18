package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Assignment(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("subject")
    val subject: String,
    @SerialName("class_id")
    val classId: String,
    @SerialName("due_date")
    val dueDate: String?,
    @SerialName("submission_type")
    val submissionType: String = "Offline",
    @SerialName("status")
    val status: String? = "Pending",
    @SerialName("teacher_id")
    val teacherId: String
)
