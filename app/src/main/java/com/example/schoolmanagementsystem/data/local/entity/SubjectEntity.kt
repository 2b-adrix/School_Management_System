package com.example.schoolmanagementsystem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.schoolmanagementsystem.domain.model.Subject

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val schoolId: String,
    val classId: String,
    val name: String,
    val code: String,
    val description: String?
)

fun SubjectEntity.toDomain() = Subject(
    id = id,
    schoolId = schoolId,
    classId = classId,
    name = name,
    code = code,
    description = description
)

fun Subject.toEntity() = SubjectEntity(
    id = id,
    schoolId = schoolId,
    classId = classId,
    name = name,
    code = code,
    description = description
)
