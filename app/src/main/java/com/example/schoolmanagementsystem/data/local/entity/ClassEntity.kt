package com.example.schoolmanagementsystem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.schoolmanagementsystem.domain.model.SchoolClass

@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey val id: String,
    val name: String,
    val section: String,
    val classTeacherId: String?
)

fun ClassEntity.toDomain() = SchoolClass(
    id = id,
    name = name,
    section = section,
    classTeacherId = classTeacherId
)

fun SchoolClass.toEntity() = ClassEntity(
    id = id,
    name = name,
    section = section,
    classTeacherId = classTeacherId
)
