package com.example.schoolmanagementsystem.backend.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Exam(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("class_id")
    val classId: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("subject_id")
    val subjectId: String,
    @SerialName("date")
    val date: String,
    @SerialName("total_marks")
    val totalMarks: Int,
    @SerialName("created_by")
    val createdBy: String
)

