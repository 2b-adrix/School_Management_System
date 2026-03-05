package com.example.schoolmanagementsystem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.schoolmanagementsystem.domain.model.Result

@Entity(tableName = "results")
data class ResultEntity(
    @PrimaryKey val id: String,
    val examId: String,
    val studentId: String,
    val subjectId: String,
    val marksObtained: Int,
    val grade: String,
    val remarks: String?
)

fun ResultEntity.toDomain() = Result(
    id = id,
    examId = examId,
    studentId = studentId,
    subjectId = subjectId,
    marksObtained = marksObtained,
    grade = grade,
    remarks = remarks
)

fun Result.toEntity() = ResultEntity(
    id = id,
    examId = examId,
    studentId = studentId,
    subjectId = subjectId,
    marksObtained = marksObtained,
    grade = grade,
    remarks = remarks
)
