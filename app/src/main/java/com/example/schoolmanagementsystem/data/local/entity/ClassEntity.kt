package com.example.schoolmanagementsystem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.schoolmanagementsystem.domain.model.SchoolClass

@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey val id: String,
    val schoolId: String,
    val name: String,
    val section: String,
    val roomNumber: String?,
    val classTeacherId: String?
)

fun ClassEntity.toDomain() = SchoolClass(
    id = id,
    schoolId = schoolId,
    name = name,
    section = section,
    roomNumber = roomNumber,
    classTeacherId = classTeacherId
)

fun SchoolClass.toEntity() = ClassEntity(
    id = id,
    schoolId = schoolId,
    name = name,
    section = section,
    roomNumber = roomNumber,
    classTeacherId = classTeacherId
)
