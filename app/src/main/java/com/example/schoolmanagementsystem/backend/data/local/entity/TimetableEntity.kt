package com.example.schoolmanagementsystem.backend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timetable")
data class TimetableEntity(
    @PrimaryKey
    val id: String,
    val schoolId: String,
    val classId: String,
    val subjectId: String,
    val teacherId: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val roomNumber: String?
)
