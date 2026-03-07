package com.example.schoolmanagementsystem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.schoolmanagementsystem.domain.model.AttendanceRecord

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey val id: String,
    val schoolId: String,
    val studentId: String,
    val classId: String,
    val subjectId: String,
    val date: String,
    val isPresent: Boolean
)

fun AttendanceEntity.toDomain() = AttendanceRecord(
    id = id,
    schoolId = schoolId,
    studentId = studentId,
    classId = classId,
    subjectId = subjectId,
    date = date,
    isPresent = isPresent
)

fun AttendanceRecord.toEntity() = AttendanceEntity(
    id = id,
    schoolId = schoolId,
    studentId = studentId,
    classId = classId,
    subjectId = subjectId,
    date = date,
    isPresent = isPresent
)
