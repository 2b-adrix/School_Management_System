package com.example.schoolmanagementsystem.domain.model

import java.util.Date

data class Exam(
    val id: String,
    val title: String,
    val description: String,
    val subjectId: String,
    val classId: String,
    val date: Date,
    val totalMarks: Int,
    val createdBy: String
)
