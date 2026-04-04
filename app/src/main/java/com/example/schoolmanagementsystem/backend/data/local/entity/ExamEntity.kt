package com.example.schoolmanagementsystem.backend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.schoolmanagementsystem.backend.domain.model.Exam

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey val id: String,
    val schoolId: String,
    val classId: String,
    val title: String,
    val description: String?,
    val subjectId: String,
    val date: String,
    val totalMarks: Int,
    val createdBy: String
)

fun ExamEntity.toDomain() = Exam(
    id = id,
    schoolId = schoolId,
    classId = classId,
    title = title,
    description = description,
    subjectId = subjectId,
    date = date,
    totalMarks = totalMarks,
    createdBy = createdBy
)

fun Exam.toEntity() = ExamEntity(
    id = id,
    schoolId = schoolId,
    classId = classId,
    title = title,
    description = description,
    subjectId = subjectId,
    date = date,
    totalMarks = totalMarks,
    createdBy = createdBy
)

