package com.example.schoolmanagementsystem.backend.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Result(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("exam_id")
    val examId: String,
    @SerialName("student_id")
    val studentId: String,
    @SerialName("subject_id")
    val subjectId: String,
    @SerialName("marks_obtained")
    val marksObtained: Int,
    @SerialName("grade")
    val grade: String,
    @SerialName("remarks")
    val remarks: String? = null
)

