package com.example.schoolmanagementsystem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.schoolmanagementsystem.domain.model.Exam
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val classId: String,
    val subjectId: String,
    val date: String,
    val totalMarks: Int,
    val createdBy: String
)

private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

fun ExamEntity.toDomain() = Exam(
    id = id,
    title = title,
    description = description,
    classId = classId,
    subjectId = subjectId,
    date = try { dateFormat.parse(date) ?: Date() } catch (e: Exception) { Date() },
    totalMarks = totalMarks,
    createdBy = createdBy
)

fun Exam.toEntity() = ExamEntity(
    id = id,
    title = title,
    description = description,
    classId = classId,
    subjectId = subjectId,
    date = dateFormat.format(date),
    totalMarks = totalMarks,
    createdBy = createdBy
)
