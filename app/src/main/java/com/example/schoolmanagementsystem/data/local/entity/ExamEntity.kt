package com.example.schoolmanagementsystem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.schoolmanagementsystem.domain.model.Exam

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey val id: String,
    val title: String,
    val classId: String,
    val subjectId: String,
    val date: String,
    val totalMarks: Int
)

fun ExamEntity.toDomain() = Exam(
    id = id,
    title = title,
    classId = classId,
    subjectId = subjectId,
    date = date,
    totalMarks = totalMarks
)

fun Exam.toEntity() = ExamEntity(
    id = id,
    title = title,
    classId = classId,
    subjectId = subjectId,
    date = date,
    totalMarks = totalMarks
)
